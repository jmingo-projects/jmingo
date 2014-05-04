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


import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.eventbus.AsyncEventBus;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.mingo.exceptions.ContextInitializationException;
import com.mingo.parser.Parser;
import com.mingo.parser.xml.dom.ParserFactory;
import com.mingo.query.watch.QuerySetUpdateEvent;
import com.mingo.query.watch.QuerySetWatchService;
import com.mingo.util.QueryUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.helpers.MessageFormatter;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;

import static com.mingo.parser.xml.dom.ParserFactory.ParseComponent.QUERY;
import static com.mingo.util.FileUtils.getAsInputStream;
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

    private EventBus eventBus = new AsyncEventBus(Executors.newSingleThreadExecutor());
    private QuerySetWatchService querySetWatchService = new QuerySetWatchService(eventBus);

    /**
     * key - composite id, value - query.
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
        //register in bus
        eventBus.register(this);

        List<QuerySet> querySets = Lists.newArrayList();

        querySetPath.forEach(path -> querySets.add(loadQuerySet(path)));
        querySets.forEach(querySet -> {

            registerInWatchService(querySet.getPath());
            putQueries(querySet);
        });
    }


    public Map<String, Query> getQueries() {
        return ImmutableMap.copyOf(queries);
    }

    @Subscribe
    public void onChange(QuerySetUpdateEvent event) {
        LOGGER.debug("query set was changed: {}", event);
        reload(event.getPath().toString());
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

    private void updateQueries(QuerySet querySet) {
        querySet.getQueries().forEach(query -> {
            String compositeId = QueryUtils.buildCompositeId(querySet.getCollectionName(), query.getId());
            validateConverter(query, querySet);
            queries.replace(compositeId, query);
        });
    }

    private void reload(String querySetPath) {
        LOGGER.debug("reload query set: {}", querySetPath);
        QuerySet querySet = loadQuerySet(querySetPath);
        updateQueries(querySet);
    }

    private QuerySet loadQuerySet(String path) {
        QuerySet querySet = QUERY_PARSER.parse(getAsInputStream(path));
        querySet.setPath(path);
        LOGGER.debug("'{}' query set was successfully loaded", path);
        return querySet;
    }

    private void registerInWatchService(String path) {
        try {
            Path fullPath = Paths.get(path);
            if (!Paths.get(path).isAbsolute()) {
                URI uri = QueryManager.class.getResource(path).toURI();
                fullPath = Paths.get(uri);
            }
            if (!Files.exists(fullPath)) {
                throw new RuntimeException("file '" + fullPath + "' is not exists" );
            }
            querySetWatchService.regiser(fullPath);
        } catch (URISyntaxException e) {
            throw Throwables.propagate(e);
        }

    }

    public void shutdown() {
        querySetWatchService.shutdown();
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
