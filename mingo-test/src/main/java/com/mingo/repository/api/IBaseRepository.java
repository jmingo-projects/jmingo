package com.mingo.repository.api;

import java.util.List;


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
 * <p/>
 *
 * @param <ID> The type of unique identifier.
 * @param <T>  The type of objects managed by repository.
 */
public interface IBaseRepository<ID, T> {

    /**
     * Inserts object.
     *
     * @param object Object to be insert.
     * @return generated id - ID
     */
    ID insert(T object);

    /**
     * Returns object by id.
     *
     * @param id Id of object
     * @return The object
     */
    T findById(ID id);

    /**
     * Find all entities in database.
     *
     * @return List
     */
    List<T> findAll();

    /**
     * Updates the object.
     *
     * @param object Object to be update.
     */
    void update(T object);

    /**
     * Deletes the object by id.
     *
     * @param object Object to be delete.
     */
    void delete(T object);

}
