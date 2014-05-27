/**
 * Copyright 2012-2013 The Mingo Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mingo.query;


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
import com.mingo.exceptions.ContextInitializationException;
import com.mingo.parser.Parser;
import com.mingo.parser.xml.dom.ParserFactory;
import com.mingo.query.watch.QuerySetUpdateEvent;
import com.mingo.query.watch.QuerySetWatchService;
import com.mingo.util.FileUtils;
import com.mingo.util.QueryUtils;
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

import static com.mingo.parser.xml.dom.ParserFactory.ParseComponent.QUERY;
import static org.slf4j.helpers.MessageFormatter.arrayFormat;

/**
 * Query manager contains all query sets and allows get necessary query by composite id.
 * All queries within single context should have different composite ids, duplication prohibited.
 */
public class QueryManager {

    private static final String CONVERTER_METHOD_DEFINITION_ERROR = "if converter class was defined then " +
            "converter method must be defined too. see query set : '{}', query: '{}'";
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

    public QueryManager(Set<String> paths) {
        initialize(paths);
    }

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


    public Map<String, Query> getQueries() {
        return ImmutableMap.copyOf(queries);
    }

    @Subscribe
    @AllowConcurrentEvents
    public void onChange(QuerySetUpdateEvent event) {
        LOGGER.debug("query set was changed: {}", event);
        reload(event.getPath());
    }

    private void putQueries(QuerySet querySet) {
        querySet.getQueries().forEach(query -> {
            String compositeId = QueryUtils.buildCompositeId(querySet.getCollectionName(), query.getId());
            if (queries.containsKey(compositeId)) {
                throw new ContextInitializationException(arrayFormat(DUPLICATED_COMPOSITE_ID_ERROR,
                        new Object[]{compositeId, query.getId(), querySet.getPath()}).getMessage());
            } else {
                validateConverter(query, querySet);
                queries.put(compositeId, query);
            }
        });
    }

    /**
     * The entire method invocation is performed atomically.
     * Some attempted update operations on this map by other threads
     * may be blocked while computation is in progress, so the
     * computation should be short and simple.
     */
    private void reload(Path path) {
        LOGGER.debug("reload query set: {}", path);
        AtomicReference<QuerySet> currentQuerySetRef = getQuerySetRef(path);
        QuerySet currQuerySet = currentQuerySetRef.get();
        String checksum = FileUtils.checksum(path.toFile());
        if(StringUtils.equals(checksum, currQuerySet.getChecksum())) {
            LOGGER.debug("query set: {} wasn't changed, content remains the same", path);
            return;
        }
        QuerySet newQuerySet = loadQuerySet(path);
        if(newQuerySet.getQueries().size() > currQuerySet.getQueries().size()) {
            LOGGER.warn("{} queries was added in query set: {}. " +
                "Operations 'add' and 'remove' on query set aren't supported.",
                newQuerySet.getQueries().size() - currQuerySet.getQueries().size(), path);
        }

        if(newQuerySet.getQueries().size() < currQuerySet.getQueries().size()) {
            LOGGER.warn("{} queries was removed from query set: {}. " +
                    "Operations 'add' and 'remove' on query set aren't supported.",
                currQuerySet.getQueries().size() - newQuerySet.getQueries().size(), path
            );
        }

        if(currentQuerySetRef.compareAndSet(currQuerySet, newQuerySet)) {
            for(Query updatedQuery : newQuerySet.getQueries()) {
                String compositeId = QueryUtils.buildCompositeId(newQuerySet.getCollectionName(), updatedQuery.getId());
                queries.computeIfPresent(compositeId, (key, currentQuery) -> {
                    LOGGER.debug("query with composite id:'{}' was refreshed. query set: '{}'",
                        compositeId, newQuerySet.getPath());
                    return updatedQuery;
                });
            }
            LOGGER.debug("query set: {} was successfully refreshed", path);
        } else {
            LOGGER.error("query set with path: {} was changed by someone before the actual update operation ended, " +
                "please refresh file {} and try again", path, path);
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
            throw new RuntimeException("query set with path'" + path + "' is not exists" );
        }
        querySetWatchService.regiser(path);
    }

    public void shutdown() {
        querySetWatchService.shutdown();
        eventBus.unregister(this);
        eventBusThreadPool.shutdown();
    }

    private AtomicReference<QuerySet> getQuerySetRef(Path path) {
        Optional<AtomicReference<QuerySet>> result = Iterables.tryFind(querySetRegistry, querySet ->
                querySet.get().getPath().equals(path));
        return result.or(new AtomicReference<>(null));
    }

    /**
     * Gets query by composite id.
     *
     * @param compositeId composite id
     * @return query or null if query not found
     */
    public Query getQueryByCompositeId(String compositeId) {
        //validateCompositeId(compositeId); todo move validation in executor
        return queries.get(compositeId);
    }

    /**
     * Gets query by composite id.
     * Similar with getQueryByCompositeId but throws exception if query isn't exist.
     *
     * @param compositeId - composed id
     * @return query {@link Query}
     * @throws RuntimeException {@link RuntimeException}
     */
    public Query lookupQuery(String compositeId) throws RuntimeException {
        Query query = queries.get(compositeId);
        if (query == null) {
            throw new RuntimeException(MessageFormatter.format(QUERY_NOT_FOUND_ERROR_MSG, compositeId).getMessage());
        }
        return query;
    }

    // todo refine this method
    private void validateConverter(Query query, QuerySet querySet) {
        if (StringUtils.isNotBlank(query.getConverter()) &&
                StringUtils.isBlank(query.getConverterMethod())) {
            throw new ContextInitializationException(MessageFormatter.arrayFormat(CONVERTER_METHOD_DEFINITION_ERROR,
                    new Object[]{querySet.getPath(), query.getId()}).getMessage());
        }
    }

}
