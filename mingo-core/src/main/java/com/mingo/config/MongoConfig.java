
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
package com.mingo.config;

import com.mongodb.ServerAddress;

import java.util.HashMap;
import java.util.Map;

public class MongoConfig {

    public static String DEF_HOST = ServerAddress.defaultHost();
    public static int DEF_PORT = ServerAddress.defaultPort();

    private String databaseHost;
    private int databasePort;
    private String dbName;
    private String writeConcern;

    private Map<String, String> options;

    private MongoConfig() {
        throw new UnsupportedOperationException("private constructor, use builder");
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getDatabaseHost() {
        return databaseHost;
    }

    public int getDatabasePort() {
        return databasePort;
    }

    public String getDbName() {
        return dbName;
    }

    public String getWriteConcern() {
        return writeConcern;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public MongoConfig(Builder builder) {
        this.databaseHost = builder.databaseHost;
        this.databasePort = builder.databasePort;
        this.dbName = builder.dbName;
        this.options = builder.options;
        this.writeConcern = builder.writeConcern;
    }

    public static class Builder {
        private String databaseHost = DEF_HOST;
        private int databasePort = DEF_PORT;
        private String dbName;
        private String writeConcern;
        private Map<String, String> options = new HashMap<>();

        public Builder dbHost(String host) {
            this.databaseHost = host;
            return this;
        }

        public Builder dbPort(int port) {
            this.databasePort = port;
            return this;
        }

        public Builder dbName(String db) {
            this.dbName = db;
            return this;
        }

        public Builder writeConcern(String writeConcern) {
            this.writeConcern = writeConcern;
            return this;
        }

        public void options(Map<String, String> options) {
            this.options = options;
        }

        public void option(String name, String val) {
            options.put(name, val);
        }

        public MongoConfig build() {
            return new MongoConfig(this);
        }
    }

}
