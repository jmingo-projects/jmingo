package com.mingo.query.builder;


import com.google.common.collect.ImmutableMap;
import com.mingo.query.nq.IfElseConditionalConstruct;
import com.mingo.query.nq.Query;
import com.mingo.query.nq.QueryElement;
import com.mingo.query.nq.TextElement;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.testng.AssertJUnit.assertEquals;

public class QueryTest {

    private IfElseConditionalConstruct ifElseConstruct = new IfElseConditionalConstruct()
            .withIf("#a > #b", "a gt b")
            .elseIf("#a < #b", "a lt b")
            .withElse("a eq b");

    @DataProvider(name = "testBuildQueryProvider")
    public Object[][] testBuildQueryProvider() {
        return new Object[][]{
                {
                        Arrays.asList(new TextElement("start,"), ifElseConstruct, new TextElement(",end")),
                        ImmutableMap.<String, Object>builder()
                                .put("a", 2)
                                .put("b", 1).build(),
                        "start,a gt b,end"
                },
                {
                        Arrays.asList(new TextElement("start,"), ifElseConstruct, new TextElement(",end")),
                        ImmutableMap.<String, Object>builder()
                                .put("a", 1)
                                .put("b", 2).build(),
                        "start,a lt b,end"
                },
                {
                        Arrays.asList(new TextElement("start,"), new IfElseConditionalConstruct()
                                        .withIf("#a > #b", "a gt b")
                                        .elseIf("#a < #b", "a lt b"),
                                new TextElement(",end")
                        ),
                        ImmutableMap.<String, Object>builder()
                                .put("a", 1)
                                .put("b", 1).build(),
                        "start,,end"
                },
        };
    }

    @DataProvider(name = "testBuildQueryWithMissingParamsProvider")
    public Object[][] testBuildQueryWithMissingParamsProvider() {
        return new Object[][]{
                {
                        Arrays.asList(new TextElement("start,"), ifElseConstruct, new TextElement(",end")),
                        Collections.emptyMap(),
                        "start,a eq b,end"
                },
                {
                        Arrays.asList(new TextElement("start,"), ifElseConstruct, new TextElement(",end")),
                        Collections.emptyMap(),
                        "start,a eq b,end"
                },
                {
                        Arrays.asList(new TextElement("start,"), new IfElseConditionalConstruct()
                                        .withIf("#a > #b", "a gt b")
                                        .elseIf("#a < #b", "a lt b"),
                                new TextElement(",end")
                        ),
                        Collections.emptyMap(),
                        "start,,end"
                }

        };
    }

    @Test(dataProvider = "testBuildQueryProvider")
    public void testBuildQuery(List<QueryElement> queryElements, Map<String, Object> parameters, String expectedQuery) {
        Query query = new Query();
        query.add(queryElements);
        String result = query.build(parameters);
        assertEquals(result, expectedQuery);
    }

    @Test(dataProvider = "testBuildQueryWithMissingParamsProvider")
    public void testBuildQueryWithMissingParams(List<QueryElement> queryElements, Map<String, Object> parameters, String expectedQuery) {
        Query query = new Query();
        query.add(queryElements);
        String result = query.build(parameters);
        assertEquals(result, expectedQuery);
    }

}
