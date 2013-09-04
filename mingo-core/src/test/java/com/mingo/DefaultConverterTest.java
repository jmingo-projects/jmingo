package com.mingo;

import com.mingo.convert.ConversionUtils;
import com.mingo.convert.DefaultConverter;
import com.mingo.domain.SimpleDomain;
import com.mongodb.BasicDBList;
import com.mongodb.DBObject;
import com.mongodb.util.JSON;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * Class description.
 *
 * @author Raman_Pliashkou
 */
public class DefaultConverterTest {

    private static final String TEST = "test";
    private static final String ID = "123456";
    private static final String JSON_VAL = "{'_id':'" + ID + "', 'name':'" + TEST + "'}";
    private DefaultConverter defaultConverter = new DefaultConverter();

    private SimpleDomain simpleDomain;

    @BeforeMethod
    private void setUp() {
        simpleDomain = new SimpleDomain();
        simpleDomain.setName(TEST);
        simpleDomain.setId(ID);
    }

    @Test
    public void testConvertOne() {
        DBObject dbObject = (DBObject) JSON.parse(JSON_VAL);
        SimpleDomain newSimpleDomain = (SimpleDomain) defaultConverter.convert(SimpleDomain.class, dbObject);
        Assert.assertEquals(newSimpleDomain, simpleDomain);
    }

    @Test
    public void testConvertList() {
        DBObject dbObject = (DBObject) JSON.parse(JSON_VAL);
        BasicDBList basicDBList = new BasicDBList();
        basicDBList.add(dbObject);
        List<SimpleDomain> simpleDomainList = ConversionUtils.convertList(SimpleDomain.class,
            basicDBList, defaultConverter);
        Assert.assertEquals(simpleDomainList.get(0), simpleDomain);
    }

    @Test
    public void testConvertNullOne() {
        DBObject dbObject = null;
        SimpleDomain newSimpleDomain = (SimpleDomain) defaultConverter.convert(SimpleDomain.class, dbObject);
        Assert.assertEquals(newSimpleDomain, null);
    }

    @Test(expectedExceptions = NullPointerException.class)
    public void testConvertNullList() {
        ConversionUtils.convertList(SimpleDomain.class, null, defaultConverter);
    }

    @Test
    public void testConvertOneToList() {
        DBObject dbObject = (DBObject) JSON.parse(JSON_VAL);
        List<SimpleDomain> simpleDomainList = ConversionUtils.convertList(SimpleDomain.class, dbObject, defaultConverter);
        Assert.assertEquals(simpleDomainList.get(0), simpleDomain);
    }

}
