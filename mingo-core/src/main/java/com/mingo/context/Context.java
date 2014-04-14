package com.mingo.context;

import static com.mingo.query.util.QueryUtils.validateCompositeId;

import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import com.mingo.convert.Converter;
import com.mingo.convert.ConverterService;
import com.mingo.exceptions.ContextInitializationException;
import com.mingo.context.conf.MongoConfig;
import com.mingo.query.Query;
import com.mingo.query.QueryAnalyzerType;
import com.mingo.query.QueryExecutorType;
import com.mingo.query.QuerySet;
import com.mingo.query.analyzer.QueryAnalyzer;
import com.mingo.query.analyzer.QueryAnalyzerFactory;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.Validate;
import org.slf4j.helpers.MessageFormatter;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.Set;

/**
 * Copyright 2012-2013 The Mingo Team
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
 * <p>
 * It's cornerstone of framework.
 */
public class Context {

    private static final String QUERY_NOT_FOUND_ERROR_MSG = "not found query with composite id: '{}'";

    public static Context create(String contextPath) {
        try {
            return ContextLoader.getInstance().load(contextPath);
        } catch (ContextInitializationException e) {
            throw Throwables.propagate(e);
        }
    }

    private Context(Builder builder) {
        this.querySets = ImmutableSet.copyOf(builder.querySets);
        this.queryExecutorType = builder.queryExecutorType;
        this.queryAnalyzerType = builder.queryAnalyzerType;
        this.databaseHost = builder.databaseHost;
        this.databasePort = builder.databasePort;
        if (builder.mongoConfig != null) {
            this.mongoConfig = builder.mongoConfig;
        } else {
            this.mongoConfig = MongoConfig.builder().dbHost(databaseHost).dbPort(databasePort).build();
        }

        //init mongo instance
        if (builder.mongo != null) {
            this.mongo = builder.mongo;
        } else {
            try {
                this.mongo = new MongoClient(mongoConfig.getDatabaseHost(), mongoConfig.getDatabasePort());
            } catch (UnknownHostException e) {
                throw Throwables.propagate(e);
            }
        }

        this.defaultConverter = builder.defaultConverter;
        this.converterService = builder.converterService;
        postConstruct();
    }

    private Set<QuerySet> querySets;

    private QueryExecutorType queryExecutorType;

    private QueryAnalyzerType queryAnalyzerType;

    private QueryAnalyzer queryAnalyzer;

    private ConverterService converterService;

    @Deprecated
    private String databaseHost;

    @Deprecated
    private int databasePort;

    private MongoConfig mongoConfig;

    private Mongo mongo;

    private Converter defaultConverter;

    private void postConstruct() {
        queryAnalyzer = QueryAnalyzerFactory.createQueryAnalyzer(queryAnalyzerType);
    }

    /**
     * Gets set of {@link QuerySet} objects.
     *
     * @return set of {@link QuerySet} objects.
     */
    public Set<QuerySet> getQuerySets() {
        return querySets;
    }

    /**
     * Gets query executor type.
     *
     * @return {@link QueryExecutorType}
     */
    public QueryExecutorType getQueryExecutorType() {
        return queryExecutorType;
    }

    /**
     * Gets database host.
     *
     * @return database host
     */
    public String getDatabaseHost() {
        return mongoConfig.getDatabaseHost();
    }

    /**
     * Gets database port.
     *
     * @return database port
     */
    public int getDatabasePort() {
        return mongoConfig.getDatabasePort();
    }

    public MongoConfig getMongoConfig() {
        return mongoConfig;
    }

    public Mongo getMongo() {
        return mongo;
    }

    /**
     * Gets query analyzer type.
     *
     * @return query analyzer type
     */
    public QueryAnalyzerType getQueryAnalyzerType() {
        return queryAnalyzerType;
    }

    /**
     * Gets query analyzer.
     *
     * @return query analyzer
     */
    public QueryAnalyzer getQueryAnalyzer() {
        return queryAnalyzer;
    }

    /**
     * Gets default converter.
     *
     * @param <T> the type of the class modeled by this {@code Class} object
     * @return default converter
     */
    public <T> Converter<T> getDefaultConverter() {
        return (Converter<T>) defaultConverter;
    }

    /**
     * Gets converter for specified type.
     *
     * @param aClass class
     * @param <T>    the type of the class modeled by this {@code Class} object
     * @return converter for specified type
     */
    public <T> Converter<T> lookupConverter(Class<T> aClass) {
        Validate.notNull(aClass, "class cannot be null");
        return converterService.lookup(aClass);
    }

    /**
     * Gets converter service {@link com.mingo.convert.ConverterService}.
     *
     * @return converter service
     */
    public ConverterService getConverterService() {
        return converterService;
    }

    /**
     * Gets query set by path.
     *
     * @param path path
     * @return {@link QuerySet}
     */
    public QuerySet getQuerySetByPath(String path) {
        Validate.notBlank(path, "query set path cannot be null.");
        QuerySet foundedQuerySet = null;
        if (CollectionUtils.isNotEmpty(querySets)) {
            for (QuerySet querySet : querySets) {
                if (path.equalsIgnoreCase(querySet.getPath())) {
                    foundedQuerySet = querySet;
                    break;
                }
            }
        }
        return foundedQuerySet;
    }

    /**
     * Gets query by composite id.
     *
     * @param compositeId composite id
     * @return query or null if query not found
     */
    public Query getQueryByCompositeId(String compositeId) {
        validateCompositeId(compositeId);
        Query query = null;
        if (CollectionUtils.isNotEmpty(querySets)) {
            for (QuerySet querySet : querySets) {
                query = querySet.getQueryByCompositeId(compositeId);
                if (query != null) {
                    break;
                }
            }
        }
        return query;
    }

    /**
     * Finds query by composite id in mingo context.
     * Similar with getQueryByCompositeId but throws exception if query not found.
     *
     * @param compositeId - composed path with next structure "dbName.collectionName.id"
     * @return query {@link Query}
     * @throws RuntimeException {@link RuntimeException}
     */
    public Query lookupQuery(String compositeId) throws RuntimeException {
        Query query = getQueryByCompositeId(compositeId);
        if (query == null) {
            throw new RuntimeException(MessageFormatter.format(QUERY_NOT_FOUND_ERROR_MSG,
                    compositeId).getMessage());
        }
        return query;
    }


    /**
     * Sets default converter.
     * Client can defines custom default converter in conf,
     * but possible situation when converter cannot be instantiated through reflection.
     * Necessary create factory for custom converters such as in springframework for beans.
     *
     * @param defaultConverter default converter
     */
    public void setDefaultConverter(Converter defaultConverter) {
        Validate.notNull(defaultConverter, "converter cannot be null");
        this.defaultConverter = defaultConverter;
    }

    /**
     * Builder class provides set of methods for context creation.
     */
    public static class Builder {

        private Mongo mongo;

        @Deprecated
        private String databaseHost;

        @Deprecated
        private int databasePort;

        private MongoConfig mongoConfig;

        private Set<QuerySet> querySets = Collections.emptySet();

        private QueryExecutorType queryExecutorType;

        private QueryAnalyzerType queryAnalyzerType = QueryAnalyzerType.JEXL;

        private Converter defaultConverter;

        private ConverterService converterService;

        public void mongo(Mongo pMongo) {
            this.mongo = pMongo;
        }

        /**
         * Add query set.
         *
         * @param querySet {@link QuerySet}
         * @return {@link Builder}
         */
        public Builder querySet(QuerySet querySet) {
            Validate.notNull(querySet, "query set cannot be null");
            if (CollectionUtils.isEmpty(querySets)) {
                querySets = Sets.newHashSet();
            }
            querySets.add(querySet);
            return this;
        }

        /**
         * Add set of {@link QuerySet} objects.
         *
         * @param pQuerySets set of {@link QuerySet} objects
         * @return {@link Builder}
         */
        public Builder querySets(Set<QuerySet> pQuerySets) {
            if (CollectionUtils.isNotEmpty(pQuerySets)) {
                if (CollectionUtils.isEmpty(querySets)) {
                    querySets = Sets.newHashSet(pQuerySets);
                }
                this.querySets.addAll(pQuerySets);
            }
            return this;
        }

        /**
         * Sets query executor type.
         *
         * @param pQueryExecutorType query executor type {@link QueryExecutorType}
         * @return {@link Builder}
         */
        public Builder queryExecutorType(QueryExecutorType pQueryExecutorType) {
            Validate.notNull(pQueryExecutorType, "query executor type cannot be null");
            this.queryExecutorType = pQueryExecutorType;
            return this;
        }

        /**
         * Sets query analyzer type.
         *
         * @param pQueryAnalyzerType query analyzer  type {@link QueryAnalyzerType}
         * @return {@link Builder}
         */
        public Builder queryAnalyzerType(QueryAnalyzerType pQueryAnalyzerType) {
            if (pQueryAnalyzerType != null) {
                this.queryAnalyzerType = pQueryAnalyzerType;
            }
            return this;
        }

        /**
         * Sets connection properties.
         *
         * @param pDatabaseHost database host
         * @param pDatabasePort database port
         * @return {@link Builder}
         */
        @Deprecated
        public Builder connection(String pDatabaseHost, int pDatabasePort) {
            this.databaseHost = pDatabaseHost;
            this.databasePort = pDatabasePort;
            return this;
        }

        /**
         * Sets mongo configuration.
         *
         * @param pMongoConfig mongo config
         * @return {@link Builder}
         */
        public Builder mongoConfig(MongoConfig pMongoConfig) {
            this.mongoConfig = pMongoConfig;
            return this;
        }

        /**
         * Sets default converter.
         *
         * @param pDefaultConverter default converter
         * @return {@link Builder}
         */
        public Builder defaultConverter(Converter pDefaultConverter) {
            Validate.notNull(pDefaultConverter, "default converter cannot be null");
            defaultConverter = pDefaultConverter;
            return this;
        }

        /**
         * Sets path to converters.
         *
         * @param path path to converters
         * @return {@link Builder}
         */
        public Builder converters(String path) {
            converterService = new ConverterService(path);
            return this;
        }

        /**
         * Builds mingo context.
         *
         * @return mingo context {@link Context}
         */
        public Context build() {
            return new Context(this);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Context{" +
                "querySets=" + querySets +
                ", queryExecutorType=" + queryExecutorType +
                ", queryAnalyzerType=" + queryAnalyzerType +
                ", queryAnalyzer=" + queryAnalyzer +
                ", converterService=" + converterService +
                ", databaseHost='" + databaseHost + '\'' +
                ", databasePort=" + databasePort +
                ", defaultConverter=" + defaultConverter +
                '}';
    }

}
