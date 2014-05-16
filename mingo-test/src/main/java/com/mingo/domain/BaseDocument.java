package com.mingo.domain;

import com.mingo.document.annotation.GeneratedValue;
import com.mingo.document.annotation.Document;
import com.mingo.document.annotation.Id;
import com.mingo.document.id.generator.IdGeneratorStrategy;

@Document
public class BaseDocument {

    @Id
    @GeneratedValue(strategy = IdGeneratorStrategy.UUID)
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        BaseDocument that = (BaseDocument) o;

        if (id != null ? !id.equals(that.id) : that.id != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
