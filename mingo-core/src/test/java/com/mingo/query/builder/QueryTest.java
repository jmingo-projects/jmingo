package com.mingo.query.builder;


import com.google.common.collect.ImmutableMap;
import com.mingo.query.IfElseConditionalConstruct;
import com.mingo.query.Query;
import com.mingo.query.QueryElement;
import com.mingo.query.TextElement;
import com.mingo.query.el.ELEngine;
import com.mingo.query.el.SpringELEngine;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.testng.Assert.assertEquals;

public class QueryTest {

    private ELEngine elEngine = new SpringELEngine();

    private IfElseConditionalConstruct ifElseConstruct = IfElseConditionalConstruct.builder()
            .withIf("#a > #b", "a gt b")
            .elseIf("#a < #b", "a lt b")
            .withElse("a eq b").build();

    @DataProvider(name = "testBuildQueryProvider")
    public Object[][] testBuildQueryProvider() {
        return new Object[][]{
                {
                        Arrays.asList(new TextElement("start,"), ifElseConstruct, new TextElement("end")),
                        ImmutableMap.<String, Object>builder()
                                .put("a", 2)
                                .put("b", 1).build(),
                        "{start,a gt b,end}"
                },
                {
                        Arrays.asList(new TextElement("start,"), ifElseConstruct, new TextElement("end")),
                        ImmutableMap.<String, Object>builder()
                                .put("a", 1)
                                .put("b", 2).build(),
                        "{start,a lt b,end}"
                },
                {
                        Arrays.asList(new TextElement("start,"), IfElseConditionalConstruct.builder()
                                        .withIf("#a > #b", "a gt b")
                                        .elseIf("#a < #b", "a lt b").build(),
                                new TextElement("end")
                        ),
                        ImmutableMap.<String, Object>builder()
                                .put("a", 1)
                                .put("b", 1).build(),
                        "{start,end}"
                },
        };
    }

    @DataProvider(name = "testBuildQueryWithMissingParamsProvider")
    public Object[][] testBuildQueryWithMissingParamsProvider() {
        return new Object[][]{
                {
                        Arrays.asList(new TextElement("start,"), ifElseConstruct, new TextElement("end")),
                        Collections.emptyMap(),
                        "{start,a eq b,end}"
                },
                {
                        Arrays.asList(new TextElement("start,"), IfElseConditionalConstruct.builder()
                                        .withIf("#a > #b", "a gt b")
                                        .elseIf("#a < #b", "a lt b").build(),
                                new TextElement("end")
                        ),
                        Collections.emptyMap(),
                        "{start,end}"
                }

        };
    }

    @Test(dataProvider = "testBuildQueryProvider")
    public void testBuildQuery(List<QueryElement> queryElements, Map<String, Object> parameters, String expectedQuery) {
        Query query = Query.builder().id("test").collectionName("test").add(queryElements).build();
        String result = query.build(elEngine, parameters);
        assertEquals(result, expectedQuery);
    }

    @Test(dataProvider = "testBuildQueryWithMissingParamsProvider")
    public void testBuildQueryWithMissingParams(List<QueryElement> queryElements, Map<String, Object> parameters, String expectedQuery) {
        Query query = Query.builder().id("test").collectionName("test").add(queryElements).build();
        String result = query.build(elEngine, parameters);
        assertEquals(result, expectedQuery);
    }

}
