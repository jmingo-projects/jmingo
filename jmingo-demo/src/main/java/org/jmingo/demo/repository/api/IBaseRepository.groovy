package org.jmingo.demo.repository.api

import org.jmingo.demo.domain.BaseDocument

interface IBaseRepository<ID, T extends BaseDocument> {

    /**
     * Inserts object.
     *
     * @param object the object to insert.
     * @return generated id - ID
     */
    ID insert(T object);

    /**
     * Returns object by id.
     *
     * @param id the id of object
     * @return The object
     */
    T findById(ID id);

    /**
     * Find all documents in collection.
     *
     * @return List
     */
    List<T> findAll();

    /**
     * Updates the object.
     *
     * @param object the object to update.
     */
    void update(T object);

    /**
     * Deletes the object by id.
     *
     * @param object the object to delete.
     */
    void delete(T object);

}
