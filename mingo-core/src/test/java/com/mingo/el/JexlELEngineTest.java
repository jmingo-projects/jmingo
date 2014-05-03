package com.mingo.el;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.mingo.query.el.ELEngine;
import com.mingo.query.el.JexlELEngine;
import org.apache.commons.lang3.time.DateUtils;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Date;
import java.util.Map;

import static org.testng.AssertJUnit.assertEquals;

/**
 * Test for {@link com.mingo.query.el.JexlELEngine}.
 */
public class JexlELEngineTest {

    private ELEngine jexlELEngine = new JexlELEngine();

    @DataProvider(name = "testExpressions")
    public Object[][] testExpressions() {
        return new Object[][]{
            //  collection
            {"not empty(collection)", new ParameterBuilder().add("collection", Lists.newArrayList("val")).build(), true},
            {"empty(collection)", new ParameterBuilder().add("collection", Lists.newArrayList("val")).build(), false},
            {"empty(collection)", new ParameterBuilder().add("collection", Lists.newArrayList()).build(), true},
            {"empty(collection)", new ParameterBuilder().add("collection", null).build(), true},
            {"collection.size() > 0", new ParameterBuilder().add("collection", Lists.newArrayList("val")).build(), true},
            {"collection.get(0).equals('val')", new ParameterBuilder().add("collection", Lists.newArrayList("val")).build(), true},
            {"collection.get(0).equals('mgs')", new ParameterBuilder().add("collection", Lists.newArrayList("val")).build(), false},
            {"collection.get(0).equals(expect)", new ParameterBuilder().add("collection", Lists.newArrayList("val")).add("expect", "val").build(), true},

            // string
            {"not empty(string)", new ParameterBuilder().add("string", "val").build(), true},
            {"empty(string)", new ParameterBuilder().add("string", null).build(), true},
            {"empty(string)", new ParameterBuilder().add("string", "val").build(), false},
            {"size(string) == exLength", new ParameterBuilder().add("string", "val").add("exLength", "val".length()).build(), true},
            {"size(string) < exLength", new ParameterBuilder().add("string", "val").add("exLength", "val".length()).build(), false},

            // number
            {"not empty(num)", new ParameterBuilder().add("num", Integer.valueOf(5)).build(), true},
            {"not empty(num)", new ParameterBuilder().add("num", null).build(), false},
            {"num == ex", new ParameterBuilder().add("num", Integer.valueOf(0)).add("ex", 0).build(), true},

            //date
            {"(not empty(start) && not empty(end) && start < end)",
                new ParameterBuilder()
                    .add("start", createDateAndAddYear(-1000))
                    .add("end", createDateAndAddYear(1000)).build(), true},

            // mix
            {"collection.get(index).equals(val) && size(collection.get(index)) == valSize && size(val) == valSize",
                new ParameterBuilder()
                    .add("collection", Lists.newArrayList("test"))
                    .add("val", "test")
                    .add("index", 0)
                    .add("valSize", "test".length()).build(), true},
        };
    }

    @Test(dataProvider = "testExpressions")
    public void testVerify(String exp, Map<String, Object> parameters, boolean result) {
        assertEquals(result, jexlELEngine.evaluate(exp, parameters));
    }

    /**
     * Creates date.
     *
     * @param year the amount to add, may be negative
     * @return the new date object with the amount added
     */
    private Date createDateAndAddYear(int year) {
        return DateUtils.addYears(new Date(), year);
    }

    private class ParameterBuilder {
        private Map<String, Object> parameters = Maps.newHashMap();

        public ParameterBuilder add(String key, Object val) {
            parameters.put(key, val);
            return this;
        }

        public Map<String, Object> build() {
            return parameters;
        }
    }

}
