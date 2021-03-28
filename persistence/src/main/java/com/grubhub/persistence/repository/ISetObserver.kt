package com.grubhub.persistence.repository

/**
 * A callback interface for a recordset of type T
 */
interface ISetObserver<T> {
    /**
     * When a change in the underlying store is detected via uri notification, this method is invoked
     * passing the updated record set.
     */
    fun onEntitiesChanged(entities: List<T>)
}