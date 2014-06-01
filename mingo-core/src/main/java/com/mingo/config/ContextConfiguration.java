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

/**
 * Mingo context configurations contains necessary information to create and initialize context.
 */
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

    /**
     * Gets mingo context config.
     *
     * @return the mingo context config
     */
    public MingoContextConfig getMingoContextConfig() {
        return mingoContextConfig;
    }

    /**
     * Sets mingo context config.
     *
     * @param mingoContextConfig the mingo context config {@link MingoContextConfig}
     */
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
     * Gets database host. also see {@link #getMongoConfig()}.
     *
     * @return database host
     */
    public String getDatabaseHost() {
        return mongoConfig.getDatabaseHost();
    }

    /**
     * Gets database port. also see {@link #getMongoConfig()}.
     *
     * @return database port
     */
    @Deprecated
    public int getDatabasePort() {
        return mongoConfig.getDatabasePort();
    }


    /**
     * Sets default converter.
     *
     * @param fullClassName full converter class name
     */
    public void setDefaultConverter(String fullClassName) {
        this.defaultConverter = fullClassName;
    }

    /**
     * Gets default converter.
     *
     * @return converter class
     */
    public String getDefaultConverter() {
        return defaultConverter;
    }

    /**
     * Gets converter package scan.
     *
     * @return converter package scan
     */
    public String getConverterPackageScan() {
        return converterPackageScan;
    }

    /**
     * Sets converter package scan.
     *
     * @param converterPackageScan the converter package scan
     */
    public void setConverterPackageScan(String converterPackageScan) {
        this.converterPackageScan = converterPackageScan;
    }

    /**
     * Gets mongo config.
     *
     * @return mongo config
     */
    public MongoConfig getMongoConfig() {
        return mongoConfig;
    }

    /**
     * Sets mongo config.
     *
     * @param mongoConfig the mongo config
     */
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
