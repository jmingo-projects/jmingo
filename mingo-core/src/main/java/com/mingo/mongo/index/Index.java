package com.mingo.mongo.index;


import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import java.util.Map;

public class Index {

    // Options for All Index Types
    public static final String BACKGROUND = "background";
    public static final String UNIQUE = "unique";
    public static final String NAME = "name";
    public static final String DROP_DUPS = "dropDups";
    public static final String SPARSE = "sparse";
    public static final String EXPIRE_AFTER_SECONDS = "expireAfterSeconds";
    public static final String VERSION = "v";

    // Options for text Indexes
    public static final String WEIGHTS = "weights";
    public static final String DEFAULT_LANGUAGE = "default_language";
    public static final String LANGUAGE_OVERRIDE = "language_override";
    public static final String TEXT_INDEX_VERSION = "textIndexVersion";

    // Options for 2dsphere Indexes
    public static final String TWO_D_SPHERE_INDEX_VERSION = "2dsphereIndexVersion";

    //Options for 2d Indexes
    public static final String BITS = "bits";
    public static final String MIN = "min";
    public static final String MAX = "max";

    // Options for geoHaystack Indexes
    public static final String BUCKET_SIZE = "bucketSize";

    private Map<String, Object> keys = Maps.newHashMap();
    private Map<String, Object> options = Maps.newHashMap();

    public Index(Builder builder) {
        this.keys = builder.keys;
        this.options = builder.options;
    }

    public static Builder builder() {
        return new Builder();
    }

    public Map<String, Object> getKeys() {
        return ImmutableMap.copyOf(keys);
    }

    public Map<String, Object> getOptions() {
        return ImmutableMap.copyOf(options);
    }

    public static class Builder {
        private Map<String, Object> keys = Maps.newHashMap();
        private Map<String, Object> options = Maps.newHashMap();

        public Builder background(boolean background) {
            options.put(BACKGROUND, background);
            return this;
        }

        public Builder unique(boolean unique) {
            options.put(UNIQUE, unique);
            return this;
        }

        public Builder name(String name) {
            options.put(NAME, name);
            return this;
        }

        public Builder dropDups(boolean dropDups) {
            options.put(DROP_DUPS, dropDups);
            return this;
        }

        public Builder sparse(boolean sparse) {
            options.put(SPARSE, sparse);
            return this;
        }

        public Builder expireAfterSeconds(int expireAfterSeconds) {
            options.put(EXPIRE_AFTER_SECONDS, expireAfterSeconds);
            return this;
        }

        public Builder version(int version) {
            options.put(VERSION, version);
            return this;
        }

        public Builder option(String name, Object val) {
            options.put(name, val);
            return this;
        }

        public Builder key(String name, IndexDirection direction) {
            keys.put(name, direction.getVal());
            return this;
        }

        public Builder key(String name) {
            keys.put(name, IndexDirection.ASC.getVal());
            return this;
        }

        public Builder keys(String... names) {
            if (names != null && names.length > 0) {
                for (int i = 0; i < names.length; i++) {
                    key(names[i]);
                }
            }
            return this;
        }

        public Index build() {
            return new Index(this);
        }
    }
}
