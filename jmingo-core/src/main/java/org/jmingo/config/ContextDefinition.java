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
package org.jmingo.config;

import org.apache.commons.lang3.builder.ToStringBuilder;

/**
 * Mingo context definition contains necessary information parsed from a xml file to create and initialize context.
 */
public class ContextDefinition {

    private QuerySetConfig querySetConfig;

    private MongoConfig mongoConfig;

    /* default converter class */
    private String defaultConverter;
    /* package to scan for converters */
    private String converterPackageScan;

    /**
     * Gets query set configuration.
     *
     * @return {@link QuerySetConfig}
     */
    public QuerySetConfig getQuerySetConfig() {
        return querySetConfig;
    }

    /**
     * Sets query set configuration.
     *
     * @param querySetConfig {@link QuerySetConfig}
     */
    public void setQuerySetConfig(QuerySetConfig querySetConfig) {
        this.querySetConfig = querySetConfig;
    }

    /**
     * Gets database host. see {@link #getMongoConfig()}.
     *
     * @return database host
     */
    public String getDatabaseHost() {
        return mongoConfig.getDatabaseHost();
    }

    /**
     * Gets database port. see {@link #getMongoConfig()}.
     *
     * @return database port
     */
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
                .append("mongoConfig", mongoConfig)
                .append("querySetConfiguration", querySetConfig)
                .append("defaultConverter", defaultConverter)
                .append("converterPackageScan", converterPackageScan)
                .toString();
    }
}
