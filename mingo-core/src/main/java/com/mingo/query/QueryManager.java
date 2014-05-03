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


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mingo.exceptions.ContextInitializationException;
import com.mingo.parser.Parser;
import com.mingo.parser.xml.dom.ParserFactory;
import com.mingo.query.watch.QuerySetWatchService;
import com.mingo.util.QueryUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.helpers.MessageFormatter;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    private QuerySetWatchService querySetWatchService = new QuerySetWatchService();

    private List<QuerySet> querySetRegistry = Lists.newArrayList();
    /**
     * key - composite id, value - query.
     */
    private Map<String, Query> queries = Maps.newHashMap();
    // parser for query sets
    private static final Parser<QuerySet> QUERY_PARSER = ParserFactory.createParser(QUERY);

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
        querySetPath.forEach(path -> querySetRegistry.add(loadQuerySet(path)));
        querySetRegistry.forEach(querySet -> {
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
        });
    }

    public List<QuerySet> getQuerySets() {
        return ImmutableList.copyOf(querySetRegistry);
    }

    public Map<String, Query> getQueries() {
        return ImmutableMap.copyOf(queries);
    }

    public void reload(String querySetPath) {
        // todo implement this method
    }

    private QuerySet loadQuerySet(String path) {
        QuerySet querySet = QUERY_PARSER.parse(getAsInputStream(path));
        querySet.setPath(path);
        URI uri = null;
        try {
            uri = QueryManager.class.getResource("/xml/testQuerySet.xml" ).toURI();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        Path dir = Paths.get(uri).getParent();
        querySetWatchService.regiser(dir);
        return querySet;
    }

    public void shutdown(){
        querySetWatchService.shutdown();
    }

    /**
     * Gets query set by path.
     *
     * @param path path
     * @return {@link QuerySet}
     */
    public QuerySet getQuerySetByPath(String path) {
        Validate.notBlank(path, "query set path cannot be null.");
        return Iterables.find(querySetRegistry, input -> StringUtils.equalsIgnoreCase(input.getPath(), path));
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
