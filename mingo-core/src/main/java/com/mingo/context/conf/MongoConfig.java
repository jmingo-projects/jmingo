
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
package com.mingo.context.conf;

public class MongoConfig {

    public static String DEF_HOST = "localhost";
    public static int DEF_PORT = 27017;

    private String databaseHost;
    private int databasePort;
    private String dbName;

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

    public MongoConfig(Builder builder) {
        this.databaseHost = builder.databaseHost;
        this.databasePort = builder.databasePort;
        this.dbName = builder.dbName;
    }

    public static class Builder {
        private String databaseHost = DEF_HOST;
        private int databasePort = DEF_PORT;
        private String dbName;

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

        public MongoConfig build() {
            return new MongoConfig(this);
        }
    }

}
