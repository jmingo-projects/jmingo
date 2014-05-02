package com.mingo.config;

import com.mingo.query.ELEngineType;
import com.mingo.query.QueryExecutorType;

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
 */
public class ContextConfiguration {

    private QuerySetConfiguration querySetConfiguration;

    private QueryExecutorType queryExecutorType = QueryExecutorType.MONGO_DRIVER;

    private ELEngineType queryAnalyzerType = ELEngineType.JEXL;

    private String databaseHost;

    private int databasePort;

    /* default converter class*/
    private String defaultConverter;

    private String converterPackageScan;

    // TODO add to shema, add parsing and so on
    private String parameterPrefix;

    private MongoConfig mongoConfig;

    /**
     * Gets query set configuration.
     *
     * @return {@link QuerySetConfiguration}
     */
    public QuerySetConfiguration getQuerySetConfiguration() {
        return querySetConfiguration;
    }

    /**
     * Sets query set configuration.
     *
     * @param querySetConfiguration {@link QuerySetConfiguration}
     */
    public void setQuerySetConfiguration(QuerySetConfiguration querySetConfiguration) {
        this.querySetConfiguration = querySetConfiguration;
    }

    /**
     * Gets query executor type.
     * Since was proposed another approach to define executor hence this method returns only MONGO_DRIVER.
     *
     * @return query executor type
     */
    public QueryExecutorType getQueryExecutorType() {
        return QueryExecutorType.MONGO_DRIVER;
    }

    /**
     * Sets query executor type.
     *
     * @param queryExecutorType query executor type
     */
    public void setQueryExecutorType(QueryExecutorType queryExecutorType) {
        if (queryExecutorType != null) {
            this.queryExecutorType = queryExecutorType;
        }
    }

    /**
     * Gets query analyzer type.
     *
     * @return query analyzer type
     */
    public ELEngineType getQueryAnalyzerType() {
        return queryAnalyzerType;
    }

    /**
     * Sets query analyzer type.
     *
     * @param queryAnalyzerType query analyzer type
     */
    public void setQueryAnalyzerType(ELEngineType queryAnalyzerType) {
        if (queryAnalyzerType != null) {
            this.queryAnalyzerType = queryAnalyzerType;
        }
    }

    /**
     * Gets database host.
     *
     * @return database host
     */
    @Deprecated
    public String getDatabaseHost() {
        return mongoConfig.getDatabaseHost();
    }

    /**
     * Sets database host.
     *
     * @param databaseHost database host
     */
    @Deprecated
    public void setDatabaseHost(String databaseHost) {
        this.databaseHost = databaseHost;
    }

    /**
     * Gets database port.
     *
     * @return database port
     */
    @Deprecated
    public int getDatabasePort() {
        return mongoConfig.getDatabasePort();
    }

    /**
     * Sets database port.
     *
     * @param databasePort database port
     */
    @Deprecated
    public void setDatabasePort(int databasePort) {
        this.databasePort = databasePort;
    }

    /**
     * Sets default converter.
     *
     * @param cClass converter class
     */
    public void setDefaultConverter(String cClass) {
        //Validate.notBlank(cClass, "converter class cannot be null or empty");
        this.defaultConverter = cClass;
    }

    /**
     * Gets default converter.
     *
     * @return converter class
     */
    public String getDefaultConverter() {
        return defaultConverter;
    }

    public String getConverterPackageScan() {
        return converterPackageScan;
    }

    public void setConverterPackageScan(String converterPackageScan) {
        this.converterPackageScan = converterPackageScan;
    }

    public String getParameterPrefix() {
        return parameterPrefix;
    }

    public void setParameterPrefix(String parameterPrefix) {
        this.parameterPrefix = parameterPrefix;
    }

    public MongoConfig getMongoConfig() {
        return mongoConfig;
    }

    public void setMongoConfig(MongoConfig mongoConfig) {
        this.mongoConfig = mongoConfig;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "ContextConfiguration{" +
                "querySetConfiguration=" + querySetConfiguration +
                ", queryExecutorType=" + queryExecutorType +
                ", queryAnalyzerType=" + queryAnalyzerType +
                ", databaseHost='" + databaseHost + '\'' +
                ", databasePort=" + databasePort +
                ", defaultConverter='" + defaultConverter + '\'' +
                ", converterPackageScan='" + converterPackageScan + '\'' +
                '}';
    }

}
