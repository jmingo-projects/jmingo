package com.mingo.mapping.marshall;

import com.mingo.document.annotation.GeneratedValue;
import com.mingo.document.annotation.Document;
import com.mingo.document.annotation.Id;
import com.mingo.document.id.generator.IdGeneratorStrategy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Document
public class Folder {

    @Id
    @GeneratedValue(strategy = IdGeneratorStrategy.UUID)
    private String id;

    private String name;

    private List<File> files = new ArrayList<>();

    private Folder subFolder;

    private Property[]properties;

    public Folder() {
    }

    public Folder(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Folder getSubFolder() {
        return subFolder;
    }

    public void setSubFolder(Folder subFolder) {
        this.subFolder = subFolder;
    }

    public Property[] getProperties() {
        return properties;
    }

    public void setProperties(Property[] properties) {
        this.properties = properties;
    }

    public void addFiles(File pFile) {
        files.add(pFile);
    }

    public List<File> getFiles() {
        return files;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Folder folder = (Folder) o;

        if (files != null ? !files.equals(folder.files) : folder.files != null) return false;
        if (id != null ? !id.equals(folder.id) : folder.id != null) return false;
        if (name != null ? !name.equals(folder.name) : folder.name != null) return false;
        if (!Arrays.equals(properties, folder.properties)) return false;
        if (subFolder != null ? !subFolder.equals(folder.subFolder) : folder.subFolder != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        result = 31 * result + (files != null ? files.hashCode() : 0);
        result = 31 * result + (subFolder != null ? subFolder.hashCode() : 0);
        result = 31 * result + (properties != null ? Arrays.hashCode(properties) : 0);
        return result;
    }
}
