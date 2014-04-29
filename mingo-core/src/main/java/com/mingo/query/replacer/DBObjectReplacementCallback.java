package com.mingo.query.replacer;

import com.mongodb.DBObject;

import java.util.Map;

public class DBObjectReplacementCallback implements ReplacementCallback<DBObject> {

    private ReplacementCallback<Map> replacementCallback;

    public DBObjectReplacementCallback(Map<String, Object> replacements) {
        replacementCallback = new MapReplacementCallback(replacements);
    }

    @Override
    public void doReplace(DBObject dbObject) {
        replacementCallback.doReplace(dbObject.toMap());
    }
}
