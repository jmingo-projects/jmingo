/**
 * Copyright 2012-2013 The Mingo Team
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.mingo.mapping.marshall.mongo.callback;

/**
 * Callback that's called to replace an item with a value.
 *
 * @param <T> type of replaced object
 */
@FunctionalInterface
public interface ReplacementCallback<T> {

    /**
     * Replace given item with a value.
     *
     * @param item the item to be replaced
     * @return replaced object
     */
    Object doReplace(T item);
}
