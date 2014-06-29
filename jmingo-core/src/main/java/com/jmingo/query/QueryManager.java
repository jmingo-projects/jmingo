/**
 * Copyright 2013-2014 The JMingo Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jmingo.query;


import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.AllowConcurrentEvents;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.jmingo.exceptions.ContextInitializationException;
import com.jmingo.parser.Parser;
import com.jmingo.parser.xml.dom.ParserFactory;
import com.jmingo.query.watch.QuerySetUpdateEvent;
import com.jmingo.query.watch.QuerySetWatchService;
import com.jmingo.util.FileUtils;
import com.jmingo.util.QueryUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicReference;

import static com.jmingo.parser.xml.dom.ParserFactory.ParseComponent.QUERY;
import static org.slf4j.helpers.MessageFormatter.arrayFormat;

/**
 * Query manager contains all query sets and allows get necessary query by composite id.
 * All queries within single context should have different composite ids, duplication prohibited.
 * Query manager doesn't have methods to get {@link com.jmingo.query.QuerySet} for security purposes because QuerySet isn't immutable.
 * Query manager provides methods to get queries and single query by id.
 */
public class QueryManager {

    private static final String DUPLICATED_COMPOSITE_ID_ERROR =
            "duplicated query composite id: '{}'. please check: '{}' and '{}' query sets.";

    private static final String QUERY_NOT_FOUND_ERROR_MSG = "not found query with composite id: '{}'";

    private ExecutorService eventBusThreadPool = Executors.newFixedThreadPool(10);
    private EventBus eventBus = new AsyncEventBus("EventBus_" + getClass().getSimpleName(), eventBusThreadPool);
    private QuerySetWatchService querySetWatchService = new QuerySetWatchService(eventBus);

    private List<AtomicReference<QuerySet>> querySetRegistry = Lists.newArrayList();

    /**
     * key - composite id, value - query.
     * Query should be UNIQUE with multiple query sets.
     */
    private Map<String, Query> queries = Maps.newConcurrentMap();

    // parser for query sets
    private static final Parser<QuerySet> QUERY_PARSER = ParserFactory.createParser(QUERY);
    private static final Logger LOGGER = LoggerFactory.getLogger(QuerySetWatchService.class);

    /**
     * Creates manager and initializes query sets for specified paths.
     *
     * @param paths paths to query set files
     */
    public QueryManager(String... paths) {
        initialize(Sets.newHashSet(paths));
    }

    /**
     * Creates manager and initializes query sets for specified paths.
     *
     * @param paths the set of querySet paths
     */
    public QueryManager(Set<String> paths) {
        initialize(paths);
    }

    /**
     * Initializes query manager.
     *
     * @param querySetPath the set of querySet paths
     */
    private void initialize(Set<String> querySetPath) {
        //register in event bus
        eventBus.register(this);


        querySetPath.forEach(path -> {
            QuerySet qs = loadQuerySet(path);
            querySetRegistry.add(new AtomicReference<>(qs));
        });

        querySetRegistry.forEach(qsRef -> {
            QuerySet querySet = qsRef.get();
            registerInWatchService(querySet.getPath());
            putQueries(querySet);
        });
    }

    /**
     * Gets immutable representation of queries.
     *
     * @return immutable representation of queries
     */
    public Map<String, Query> getQueries() {
        return ImmutableMap.copyOf(queries);
    }

    /**
     * Gets query by composite id.
     *
     * @param compositeId composite id
     * @return query or null if query there are no query with specified composite id
     */
    public Query getQueryByCompositeId(String compositeId) {
        return queries.get(compositeId);
    }

    /**
     * Gets query by composite id.
     * Similar with getQueryByCompositeId but throws exception if query doesn't exist.
     *
     * @param compositeId the composed id to find query
     * @return the query {@link Query} for specified composite id
     * @throws RuntimeException if query with specified composite id doesn't exists
     */
    public Query lookupQuery(String compositeId) throws RuntimeException {
        Query query = queries.get(compositeId);
        if (query == null) {
            throw new RuntimeException(MessageFormatter.format(QUERY_NOT_FOUND_ERROR_MSG, compositeId).getMessage());
        }
        return query;
    }

    /**
     * This method is called when mingo context is being closed.
     * The entry of this method contains actions to properly close all running within current manager services.
     *
     * @throws RuntimeException if any errors occur
     */
    public void shutdown() throws RuntimeException {
        querySetWatchService.shutdown();
        eventBus.unregister(this);
        eventBusThreadPool.shutdown();
    }

    // todo can be private ?
    @Subscribe
    @AllowConcurrentEvents
    public void onChange(QuerySetUpdateEvent event) {
        LOGGER.debug("query set was changed: {}", event);
        reload(event.getPath());
    }

    private void putQueries(QuerySet querySet) {
        querySet.getQueries().forEach(query -> {
            String compositeId = query.getCompositeId();
            if (queries.containsKey(compositeId)) {
                throw new ContextInitializationException(arrayFormat(DUPLICATED_COMPOSITE_ID_ERROR,
                        new Object[]{compositeId, query.getId(), querySet.getPath()}).getMessage());
            } else {
                queries.put(compositeId, query);
            }
        });
    }

    /**
     * The entire method invocation is performed atomically.
     * Any attempts to perform update operations on {@link #queries} by other threads
     * may be blocked while reloading is in progress, so the
     * reloading logic shouldn't take a much time.
     * This method uses optimistic concurrency control thus if during reloading the query set by specified path was
     * changed again then current reloading process will be canceled and necessary message will be showed.
     */
    private void reload(Path path) {
        LOGGER.debug("reload query set: {}", path);
        AtomicReference<QuerySet> currentQuerySetRef = getQuerySetRef(path);
        QuerySet currQuerySet = currentQuerySetRef.get();
        String checksum = FileUtils.checksum(path.toFile());
        if (StringUtils.equals(checksum, currQuerySet.getChecksum())) {
            LOGGER.debug("query set: {} was edited but wasn't changed, content remains the same", path);
            return;
        }
        QuerySet newQuerySet = loadQuerySet(path);
        if (newQuerySet.getQueries().size() > currQuerySet.getQueries().size()) {
            LOGGER.warn("{} queries was added in query set: {}. " +
                            "Operations 'add' and 'remove' on query set aren't supported.",
                    newQuerySet.getQueries().size() - currQuerySet.getQueries().size(), path
            );
        }

        if (newQuerySet.getQueries().size() < currQuerySet.getQueries().size()) {
            LOGGER.warn("{} queries was removed from query set: {}. " +
                            "Operations 'add' and 'remove' on query set aren't supported.",
                    currQuerySet.getQueries().size() - newQuerySet.getQueries().size(), path
            );
        }

        if (currentQuerySetRef.compareAndSet(currQuerySet, newQuerySet)) {
            for (Query updatedQuery : newQuerySet.getQueries()) {
                String compositeId = QueryUtils.buildCompositeId(newQuerySet.getCollectionName(), updatedQuery.getId());
                queries.computeIfPresent(compositeId, (key, currentQuery) -> {
                    LOGGER.debug("query with composite id:'{}' was refreshed. query set: '{}'",
                            compositeId, newQuerySet.getPath());
                    return updatedQuery;
                });
            }
            LOGGER.debug("query set: {} was successfully reloaded", path);
        } else {
            LOGGER.error("query set with path: {} was changed by someone before the actual update operation ended, " +
                    "please refresh file {} and try to edit this query set again", path, path);
        }
    }

    private QuerySet loadQuerySet(String path) {
        Path absolutePath = FileUtils.getAbsolutePath(path);
        return loadQuerySet(absolutePath);
    }

    private QuerySet loadQuerySet(Path path) {
        QuerySet querySet = QUERY_PARSER.parse(path);
        querySet.setPath(path);
        LOGGER.debug("'{}' query set was successfully loaded", path);
        return querySet;
    }

    private void registerInWatchService(Path path) {
        if (!Files.exists(path)) {
            throw new RuntimeException("query set with path'" + path + "' is not exists");
        }
        querySetWatchService.regiser(path);
    }

    private AtomicReference<QuerySet> getQuerySetRef(Path path) {
        Optional<AtomicReference<QuerySet>> result = Iterables.tryFind(querySetRegistry, querySet ->
                querySet.get().getPath().equals(path));
        return result.or(new AtomicReference<>(null));
    }

}
