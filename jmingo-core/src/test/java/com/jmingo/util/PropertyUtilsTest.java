package com.jmingo.util;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.Map;

import static com.jmingo.util.PropertyUtils.transform;
import static org.testng.AssertJUnit.assertTrue;

/**
 * Unit test for {@link PropertyUtils}.
 */
public class PropertyUtilsTest {

    @DataProvider(name = "testTransformProvider")
    public Object[][] testTransformProvider() {
        return new Object[][]{
                {Integer.class, "5"},
                {Short.class, "6"},
                {Long.class, "7"},
                {Float.class, "8.0"},
                {Double.class, "9.0"},
                {Boolean.class, "true"},
                {String.class, "str"},
        };
    }

    @Test(dataProvider = "testTransformProvider")
    public void testTransform(Class<?> type, String val) {
        Object result = transform(type, val);
        assertTrue(type.isAssignableFrom(result.getClass()));
    }

    @Test(expectedExceptions = IllegalArgumentException.class)
    public void testUnsupportedTypeTransform() {
        transform(Map.class, "");
    }
}
