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
package com.mingo.config;

import com.mingo.query.el.ELEngineType;
import org.apache.commons.lang3.builder.ToStringBuilder;


public class ContextConfiguration {

    private MingoContextConfig mingoContextConfig;

    private QuerySetConfiguration querySetConfiguration;

    private MongoConfig mongoConfig;

    private ELEngineType queryAnalyzerType = ELEngineType.JEXL;

    private String databaseHost;

    private int databasePort;

    /* default converter class */
    private String defaultConverter;

    private String converterPackageScan;

    public MingoContextConfig getMingoContextConfig() {
        return mingoContextConfig;
    }

    public void setMingoContextConfig(MingoContextConfig mingoContextConfig) {
        this.mingoContextConfig = mingoContextConfig;
    }

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

    public MongoConfig getMongoConfig() {
        return mongoConfig;
    }

    public void setMongoConfig(MongoConfig mongoConfig) {
        this.mongoConfig = mongoConfig;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("mingoContextConfig", mingoContextConfig)
                .append("querySetConfiguration", querySetConfiguration)
                .append("queryAnalyzerType", queryAnalyzerType)
                .append("databaseHost", databaseHost)
                .append("databasePort", databasePort)
                .append("defaultConverter", defaultConverter)
                .append("converterPackageScan", converterPackageScan)
                .append("mongoConfig", mongoConfig)
                .toString();
    }
}
