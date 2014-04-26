package com.mingo.marshall;


import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.testng.AssertJUnit.assertNotNull;
import static org.testng.AssertJUnit.assertNull;

public class IdFieldModifierTest {

    @Test
    public void testGenerateId() {
        IdFieldModifier idFieldModifier = new IdFieldModifier();
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

}
