package com.mingo;

import com.mingo.context.Context;
import com.mingo.context.ContextLoader;
import com.mingo.exceptions.ContextInitializationException;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test for {@link com.mingo.context.ContextLoader}
 */
public class InvalidContextLoaderTest {

    @DataProvider(name = "contextPaths")
    public Object[][] contextPaths() {
        return new Object[][]{
            {"/xml/invalid_a/context.xml"},
            {"/xml/invalid_b/context.xml"},
            {"/xml/invalid_c/context.xml"},
            {"/xml/invalid_d/context.xml"},
            {"/xml/invalid_e/context.xml"},
        };
    }


    @Test(dataProvider = "contextPaths", groups = "unit", expectedExceptions = ContextInitializationException.class)
    public void testLoadInvalidContext(String contextPath) throws ContextInitializationException {
        Context context = ContextLoader.getInstance().load(contextPath);
    }

}
