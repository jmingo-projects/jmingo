package com.mingo.marshall;

import com.mingo.marshall.jackson.JacksonMongoBsonMarshallingFactory;
import com.mongodb.BasicDBObject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

public class JacksonMongoMarshallingTest {

    private MongoBsonMarshaller mongoBsonMarshaller = JacksonMongoBsonMarshallingFactory.getInstance().createMarshaller();

    private MongoBsonUnmarshaller mongoBsonUnmarshaller = JacksonMongoBsonMarshallingFactory.getInstance().createUnmarshaller();

    @Test
    public void testMarshall() {
        //given
        Folder folder = new Folder("root");
        Property[] rootFolderProperties = new Property[]{new Property("readOnly", "true")};
        Property[] subFolderProperties = new Property[]{new Property("readOnly", "false")};
        folder.addFiles(new File("file1"));
        folder.addFiles(new File("file2"));
        folder.setProperties(rootFolderProperties);
        Folder subFolder = new Folder("sub_folder");
        subFolder.setProperties(subFolderProperties);
        subFolder.addFiles(new File("sub_file1"));
        subFolder.addFiles(new File("sub_file2"));
        folder.setSubFolder(subFolder);

        BasicDBObject basicDBObject = mongoBsonMarshaller.marshall(BasicDBObject.class, folder);
        Folder unmarshalledFolder = mongoBsonUnmarshaller.unmarshall(Folder.class, basicDBObject);
        assertEquals(unmarshalledFolder, folder);
    }
}
