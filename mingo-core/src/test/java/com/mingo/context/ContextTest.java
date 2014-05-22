package com.mingo.context;


import com.mongodb.Mongo;
import com.mongodb.WriteConcern;
import org.testng.annotations.Test;

import static org.testng.AssertJUnit.assertEquals;

public class ContextTest {

    @Test
    public void testContext() {
        Context context = Context.create("/xml/context.xml");
        Mongo mongo = context.getMongoDBFactory().getMongo();
        assertEquals(WriteConcern.ACKNOWLEDGED, mongo.getWriteConcern());
        assertEquals(100, mongo.getMongoOptions().getConnectTimeout());
        context.shutdown();
    }

    public static void main(String[] args) {
        Context context = Context.create("/xml/context.xml");
        context.shutdown();
    }
}
