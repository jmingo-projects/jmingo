package com.mingo.query.watch;


import org.apache.commons.lang3.builder.ToStringBuilder;

import java.nio.file.Path;

public class QuerySetUpdateEvent {

    private final Path path;

    public QuerySetUpdateEvent(Path path) {
        this.path = path;
    }

    public Path getPath() {
        return path;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).
                append("path", path).
                toString();
    }
}
