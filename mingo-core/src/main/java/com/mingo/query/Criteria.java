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
    private JsonToDBObjectMarshaller jsonToDBObjectMarshaller = MongoBsonMarshallingFactory.getInstance().createJsonToDbObjectMarshaller();

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
       return jsonToDBObjectMarshaller.marshall(queryTemplate, parameters);
    }

}
