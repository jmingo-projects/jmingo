package com.mingo.mapping.marshall;


import com.mingo.document.annotation.GeneratedValue;
import com.mingo.document.annotation.Id;
import com.mingo.document.id.IdFieldGenerator;
import com.mingo.document.id.generator.IdGeneratorStrategy;
import com.mingo.document.id.generator.factory.DefaultIdGeneratorFactory;
import com.mingo.exceptions.IdGenerationException;
import org.bson.types.ObjectId;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;
import static org.testng.AssertJUnit.assertTrue;

public class IdFieldModifierTest {

    IdFieldGenerator idFieldModifier = new IdFieldGenerator(new DefaultIdGeneratorFactory());

    @Test
    public void testGenerateId() {

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
        //when
        idFieldModifier.generateId(folder);
        // then
        assertNotNull(folder.getId());
        assertNotNull(folder.getSubFolder().getId());
        assertNotNullIds(folder.getFiles());
        assertNotNullIds(folder.getSubFolder().getFiles());
        assertNullIds(Arrays.asList(folder.getProperties()));
        assertNullIds(Arrays.asList(folder.getSubFolder().getProperties()));
    }

    private void assertNotNullIds(Collection<File> files) {
        files.forEach(file -> assertNotNull(file.getId()));
    }

    private void assertNullIds(List<Property> properties) {
        properties.forEach(property -> assertNull(property.getId()));
    }

    @Test
    public void testSnowflakePrimitiveLongId() {
        PrimitiveLongIdSnowflakeStrategy longPrimitiveIdEntity = new PrimitiveLongIdSnowflakeStrategy();
        idFieldModifier.generateId(longPrimitiveIdEntity);
        assertTrue(longPrimitiveIdEntity.getId() != 0);
    }

    @Test
    public void testSnowflakeWrapperLongId() {
        WrapperLongIdSnowflakeStrategy wrapperLongIdEntity = new WrapperLongIdSnowflakeStrategy();
        idFieldModifier.generateId(wrapperLongIdEntity);
        assertNotNull(wrapperLongIdEntity.getId());
        assertTrue(wrapperLongIdEntity.getId() != 0);
    }

    @Test
    public void testSnowflakeWrapperStringId() {
        WrapperStringIdSnowflakeStrategy wrapperStringId = new WrapperStringIdSnowflakeStrategy();
        idFieldModifier.generateId(wrapperStringId);
        assertNotNull(wrapperStringId.getId());
    }

    @Test
    public void testUUIDWrapperStringId() {
        WrapperStringIdUUIDStrategy wrapperStringId = new WrapperStringIdUUIDStrategy();
        idFieldModifier.generateId(wrapperStringId);
        assertNotNull(wrapperStringId.getId());
    }

    @Test(expectedExceptions = IdGenerationException.class)
    public void testUUIDWrapperLongId() {
        WrapperLongIdUUIDStrategy wrapperStringId = new WrapperLongIdUUIDStrategy();
        idFieldModifier.generateId(wrapperStringId);
        assertNotNull(wrapperStringId.getId());
    }

    @Test
    public void testObjectId() {
        ObjectIdStrategy objectId = new ObjectIdStrategy();
        idFieldModifier.generateId(objectId);
        assertNotNull(objectId.getId());
    }

    @Test(expectedExceptions = IdGenerationException.class)
    public void testObjectIdUUIDStrategy() {
        ObjectIdUUIDStrategy objectId = new ObjectIdUUIDStrategy();
        idFieldModifier.generateId(objectId);
        assertNotNull(objectId.getId());
    }

    private static class PrimitiveLongIdSnowflakeStrategy {
        @Id
        @GeneratedValue(strategy = IdGeneratorStrategy.SNOWFLAKE)
        private long id;

        public long getId() {
            return id;
        }
    }

    private static class WrapperLongIdSnowflakeStrategy {
        @Id
        @GeneratedValue(strategy = IdGeneratorStrategy.SNOWFLAKE)
        private Long id;

        public long getId() {
            return id;
        }
    }

    private static class WrapperStringIdSnowflakeStrategy {
        @Id
        @GeneratedValue(strategy = IdGeneratorStrategy.SNOWFLAKE) // long can be converted to string
        private String id;

        public String getId() {
            return id;
        }
    }

    private static class WrapperStringIdUUIDStrategy {
        @Id
        @GeneratedValue(strategy = IdGeneratorStrategy.UUID)
        private String id;

        public String getId() {
            return id;
        }
    }

    private static class WrapperLongIdUUIDStrategy {
        @Id
        @GeneratedValue(strategy = IdGeneratorStrategy.UUID)// wrong strategy
        private Long id;

        public Long getId() {
            return id;
        }
    }

    private static class ObjectIdStrategy {
        @Id
        @GeneratedValue(strategy = IdGeneratorStrategy.OBJECT_ID)
        private ObjectId id;

        public ObjectId getId() {
            return id;
        }
    }

    private static class ObjectIdUUIDStrategy {
        @Id
        @GeneratedValue(strategy = IdGeneratorStrategy.UUID) // wrong strategy
        private ObjectId id;

        public ObjectId getId() {
            return id;
        }
    }

}
