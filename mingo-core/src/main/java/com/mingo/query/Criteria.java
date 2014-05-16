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


import com.google.common.collect.Maps;
import com.mingo.mapping.marshall.JsonToDBObjectMarshaller;
import com.mingo.mapping.marshall.mongo.MongoBsonMarshallingFactory;
import com.mongodb.DBObject;

import java.util.Map;

public class Criteria {

    private String queryTemplate;
    private boolean multi;
    private boolean upsert;
    private Map<String, Object> parameters = Maps.newHashMap();
    private static final JsonToDBObjectMarshaller JSON_TO_DB_OBJECT_MARSHALLER = new MongoBsonMarshallingFactory()
            .createJsonToDbObjectMarshaller();

    public Criteria(String queryTemplate) {
        this.queryTemplate = queryTemplate;
    }

    public static Criteria where(String queryTemplate) {
        return new Criteria(queryTemplate);
    }

    public static Criteria whereId(Object id) {
        return Criteria.where("{'_id' : '#_id'}").with("_id", id);
    }

    public Criteria with(String paramName, Object paramValue) {
        parameters.put(paramName, paramValue);
        return this;
    }

    public  Criteria updateMulti() {
        multi = true;
        return this;
    }

    public  Criteria updateFirst() {
        multi = false;
        return this;
    }

    private Criteria upsert(boolean u) {
        upsert = u;
        return this;
    }

    public boolean isMulti() {
        return multi;
    }

    public boolean isUpsert() {
        return upsert;
    }

    public DBObject query(){
       return JSON_TO_DB_OBJECT_MARSHALLER.marshall(queryTemplate, parameters);
    }

}
