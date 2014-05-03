package com.mingo.mapping.marshall.mongo.callback;


import com.mongodb.BasicDBObject;
import com.mongodb.util.JSONCallback;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;

public class EscapeEmptyCallback extends JSONCallback {

    @Override
    public void gotString(String name, String v) {
        // exclude fields with empty parameters
        if(StringUtils.isNotEmpty(v)) {
            super.gotString(name, v);
        }
    }

    @Override
    protected void _put(String name, Object val) {
        if(isElementNotEmpty(val)) {
            super._put(name, val);
        }
    }

    @Override
    public Object objectDone() {
        String name = curName();
        Object done = super.objectDone();

        if(name != null && !isElementNotEmpty(done)) {
            return cur().removeField(name);
        }
        return done;
    }

    private boolean isElementNotEmpty(Object value) {
        if(value instanceof BasicDBObject) {
            BasicDBObject dbObjVal = (BasicDBObject) value;
            if(dbObjVal.isEmpty()) {
                return false;
            }
            for(Map.Entry<String, Object> entry : dbObjVal.entrySet()) {
                boolean tmp = isElementNotEmpty(entry.getValue());
                if(!tmp) {
                    return false;
                }
            }
        }
        return true;
    }

}


