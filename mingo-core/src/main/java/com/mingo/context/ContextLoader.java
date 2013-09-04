package com.mingo.context;

import static com.mingo.parser.xml.dom.ParserFactory.ParseComponent.CONTEXT;
import static com.mingo.parser.xml.dom.ParserFactory.ParseComponent.QUERY;
import static com.mingo.query.util.QueryUtils.createCompositeIdForQueries;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mingo.context.conf.ContextConfiguration;
import com.mingo.convert.Converter;
import com.mingo.convert.DefaultConverter;
import com.mingo.exceptions.ContextInitializationException;
import com.mingo.exceptions.ParserException;
import com.mingo.parser.Parser;
import com.mingo.parser.xml.dom.ParserFactory;
import com.mingo.query.Query;
import com.mingo.query.QuerySet;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.helpers.MessageFormatter;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;

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
 * <p/>
 * Load and initialize context. This process splits on next steps:
 * 1. loading meta-data. see {@link ContextConfiguration}
 * 2. initialization context (loading and validating query sets)
 */
public class ContextLoader {

    private ContextLoader() {

    }

    private static final String CONTEXT_PATH_ERROR = "path to context configuration cannot be empty or null";
    private static final String CONVERTER_LOAD_ERROR = "error load converter: '{}'";
    private static final String CONVERTER_METHOD_DEFINITION_ERROR = "if converter class was defined then " +
        "converter method must be defined too. see query set : '{}', query: '{}'";
    private static final String DUPLICATED_COMPOSITE_ID_ERROR =
        "duplicated query composite id: '{}'. please check: '{}' and '{}' query sets.";
    private static final String BAD_CONVERTER =
        "converter: '{}' doesn't implement com.mingo.convert.Converter interface.";

    /**
     * Returns an instance of {@link ContextLoader} class.
     *
     * @return an instance of {@link ContextLoader} class
     */
    public static ContextLoader getInstance() {
        return Singleton.INSTANCE.getContextLoader();
    }

    // register parsers
    private static final Parser<ContextConfiguration> CONTEXT_CONFIGURATION_PARSER =
        ParserFactory.createParser(CONTEXT);

    private static final Parser<QuerySet> QUERY_PARSER =
        ParserFactory.createParser(QUERY);

    /**
     * Loads context configuration.
     *
     * @param contextPath path to xml file with context configuration
     * @return loaded and initialized and {@link Context}
     * @throws ContextInitializationException {@link ContextInitializationException}
     */
    public Context load(String contextPath) throws ContextInitializationException {
        Context context;
        try {
            if (StringUtils.isBlank(contextPath)) {
                throw new IllegalArgumentException(CONTEXT_PATH_ERROR);
            }
            // load context configuration from xml file
            ContextConfiguration contextConfiguration = loadContextConfiguration(contextPath);
            // initialize context
            context = initialize(contextConfiguration);
        } catch (IOException e) {
            throw new ContextInitializationException(e);
        } catch (ParserException e) {
            throw new ContextInitializationException(e);
        }
        return context;
    }

    /**
     * Create and initialize context.
     *
     * @param contextConfiguration context configuration metadata
     * @return initialized context
     * @throws IOException     {@link IOException}
     * @throws ParserException {@link ParserException}
     */
    private Context initialize(ContextConfiguration contextConfiguration) throws IOException, ParserException,
        ContextInitializationException {
        // init query sets
        Set<QuerySet> querySets = Sets.newHashSet();
        Map<String, String> queryIds = Maps.newHashMap();
        for (String querySetPath : contextConfiguration.getQuerySetConfiguration().getQuerySets()) {
            QuerySet querySet = loadQuerySet(contextConfiguration, querySetPath);
            validateQuerySet(queryIds, querySet);
            querySets.add(querySet);
        }

        // build context
        Context context = new Context.Builder()
            .querySets(querySets)
            .queryExecutorType(contextConfiguration.getQueryExecutorType())
            .queryAnalyzerType(contextConfiguration.getQueryAnalyzerType())
            .connection(contextConfiguration.getDatabaseHost(), contextConfiguration.getDatabasePort())
            .converters(contextConfiguration.getConverterPackageScan())
            .defaultConverter(initializeDefaultConverter(contextConfiguration))
            .build();
        return context;
    }

    /**
     * Parses xml file which managing by schema: context.xsd and creates {@link ContextConfiguration} object.
     */
    private ContextConfiguration loadContextConfiguration(String contextPath) throws IOException, ParserException {
        return CONTEXT_CONFIGURATION_PARSER.parse(getAsInputStream(contextPath));
    }

    private QuerySet loadQuerySet(ContextConfiguration contextConfiguration, String querySetPath) throws IOException,
        ParserException, ContextInitializationException {
        QuerySet querySet = QUERY_PARSER.parse(getAsInputStream(querySetPath));
        querySet.setPath(querySetPath);
        return postInit(contextConfiguration, querySet);
    }

    private QuerySet postInit(ContextConfiguration contextConfiguration, QuerySet querySet)
        throws ContextInitializationException {
        if (StringUtils.isEmpty(querySet.getDbName()) &&
            StringUtils.isNotEmpty(contextConfiguration.getQuerySetConfiguration().getDatabaseName())) {
            querySet.setDbName(contextConfiguration.getQuerySetConfiguration().getDatabaseName());
        } else if (StringUtils.isEmpty(querySet.getDbName()) &&
            StringUtils.isEmpty(contextConfiguration.getQuerySetConfiguration().getDatabaseName())) {
            throw new ContextInitializationException(MessageFormatter.format("undefined db name for query set: '{}'",
                querySet).getMessage());
        }
        createCompositeIdForQueries(querySet);
        return querySet;
    }

    /**
     * This method checks next points:
     * 1. In context cannot be exist two query with equivalent composite id [dbName.collectionName.id]
     * 2. Definition of converter method.
     *
     * @param querySet {@link QuerySet}
     */
    private void validateQuerySet(Map<String, String> queryIds, QuerySet querySet)
        throws ContextInitializationException {
        Validate.notNull(querySet, "querySet cannot be null");
        Validate.notBlank(querySet.getDbName(), "dbName cannot be null");
        Validate.notBlank(querySet.getCollectionName(), "collectionName cannot be null");

        for (Map.Entry<String, Query> queryEntry : querySet.getQueries().entrySet()) {
            if (!queryIds.containsKey(queryEntry.getValue().getCompositeId())) {
                queryIds.put(queryEntry.getValue().getCompositeId(), querySet.getPath());
            } else {
                throw new ContextInitializationException(
                    MessageFormatter.arrayFormat(DUPLICATED_COMPOSITE_ID_ERROR, new Object[]{
                        queryEntry.getValue().getCompositeId(), querySet.getPath(),
                        queryIds.get(queryEntry.getValue().getCompositeId())}).getMessage());
            }
            if (StringUtils.isNotBlank(queryEntry.getValue().getConverter()) &&
                StringUtils.isBlank(queryEntry.getValue().getConverterMethod())) {
                throw new ContextInitializationException(MessageFormatter.arrayFormat(CONVERTER_METHOD_DEFINITION_ERROR,
                    new Object[]{querySet.getPath(), queryEntry.getValue().getId()}).getMessage());
            }
        }
    }

    private Converter initializeDefaultConverter(ContextConfiguration contextConfiguration)
        throws ContextInitializationException {
        Converter defaultConverter = null;
        if (StringUtils.isNotBlank(contextConfiguration.getDefaultConverter())) {
            defaultConverter = initializeConverter(contextConfiguration.getDefaultConverter());
        } else {
            defaultConverter = new DefaultConverter();
        }
        return defaultConverter;
    }

    private Converter initializeConverter(String converterClass) throws ContextInitializationException {
        try {
            Class converter = Class.forName(converterClass);
            if (Converter.class.isAssignableFrom(converter)) {
                return (Converter) converter.newInstance();
            } else {
                throw new ContextInitializationException(
                    MessageFormatter.format(BAD_CONVERTER, converterClass).getMessage());
            }
        } catch (ClassNotFoundException | IllegalAccessException | InstantiationException ex) {
            throw new ContextInitializationException(MessageFormatter.format(CONVERTER_LOAD_ERROR,
                converterClass).getMessage(), ex);
        }
    }

    /**
     * Try to get stream of file by specified path.
     * If file doesn't exist in classpath then try to load from other place.
     *
     * @param filePath path to file
     * @return InputStream {@link InputStream}
     * @throws IOException
     */
    private InputStream getAsInputStream(String filePath) throws IOException {
        InputStream is = getClass().getResourceAsStream(filePath);
        if (is != null) {
            return is;
        }
        return new FileInputStream(filePath);
    }

    /**
     * Singleton implementation with enum.
     */
    private enum Singleton {

        INSTANCE;

        private static final ContextLoader CONTEXT_LOADER = new ContextLoader();

        /**
         * Gets context loader instance.
         *
         * @return {@link ContextLoader}
         */
        ContextLoader getContextLoader() {
            return CONTEXT_LOADER;
        }
    }

}
