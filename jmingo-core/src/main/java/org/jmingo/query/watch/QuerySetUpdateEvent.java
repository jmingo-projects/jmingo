/**
 * Copyright 2013-2014 The JMingo Team
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jmingo.query.watch;


import org.apache.commons.lang3.builder.ToStringBuilder;

import java.nio.file.Path;

/**
 * This event is used to notify others that some query set was changed.
 */
public class QuerySetUpdateEvent {

    private final Path path;

    /**
     * Constructor to set path of changed query set.
     *
     * @param path the path of changed query set.
     */
    public QuerySetUpdateEvent(Path path) {
        this.path = path;
    }

    /**
     * Gets path of changed query set.
     *
     * @return path of changed query set
     */
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
