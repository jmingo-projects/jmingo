package com.jmingo.mapping.marshall;

import com.jmingo.document.annotation.Document;
import com.jmingo.document.annotation.GeneratedValue;
import com.jmingo.document.annotation.Id;
import com.jmingo.document.id.generator.IdGeneratorStrategy;
import org.bson.types.ObjectId;

@Document
public class File {

    @Id
    @GeneratedValue(strategy = IdGeneratorStrategy.OBJECT_ID)
    private ObjectId id;

    private String name;

    public File() {
    }

    public File(String name) {
        this.name = name;
    }

    public ObjectId getId() {
        return id;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        File file = (File) o;

        if (id != null ? !id.equals(file.id) : file.id != null) return false;
        if (name != null ? !name.equals(file.name) : file.name != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = id != null ? id.hashCode() : 0;
        result = 31 * result + (name != null ? name.hashCode() : 0);
        return result;
    }
}
