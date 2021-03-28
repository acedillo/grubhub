package com.grubhub.persistence.repository

import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import com.grubhub.persistence.entity.IEntityWritable

interface IBaseRepository<T : IEntityWritable, Tid> {
    /**
     * Inserts a single entity of type T to the underlying data store. This method may return null
     *
     * @param context A context reference
     * @param entity The entity to write to the db
     * @return A uri representing the inserted record or null if the operation fails
     */
    fun insert(context: Context, entity: T) : Uri?

    /**
     * Used to synchronize the underlying data store with an upstream source of truth. This will
     * calculate deltas bewteen the provided data set and records currently persisted to the store.
     * Net-new entities will be inserted, existing records will be updated, and records found in the
     * store but not in the provided set will be deleted.
     *
     * @param context A context reference
     * @param entities A set of entities of type T to be synchronized with the store
     * @return The number of rows affected by the operation
     */
    fun sync(context: Context, vararg entities: T) : Int

    /**
     * Used to update an existing entity within the underlying store.
     *
     * @param context A context reference
     * @param id The id of the entity of type Tid
     * @param values The values to update on the stored entity
     * @param selection A "where" clause to further refine the scope of the operation
     * @param args An array of arguments to bind to the selection parameter if provided
     * @return The number of rows affected by the operation
     */
    fun update(context: Context, id: Tid, values: ContentValues, selection: String? = null, args: Array<String>? = null) : Int

    /**
     * Deletes the specified record(s) from the underlying store.
     *
     * @param context A context reference
     * @param selection A "where" clause to further refine the scope of the operation
     * @param args An array of arguments to bind to the selection parameter if provided
     * @param ids The ids of type Tid to remove from the store
     * @return The number of rows affected by the operation
     */
    fun delete(context: Context, selection: String? = null, args: Array<String>? = null, vararg ids: Tid): Int

    /**
     * Fetches a single record of type T with the provided id of type Tid from the underlying store.
     * This method may return null.
     *
     * @param context A context reference
     * @param id The id of the entity to return
     * @return An entity of type T if found, otherwise false.
     */
    fun fetchById(context: Context, id: Tid) : T?

    /**
     * Fetches all records of type T from the underlying store.
     *
     * @param context A context reference
     * @param selection A "where" clause to further refine the scope of the operation
     * @param selectionArgs An array of arguments to bind to the selection parameter if provided
     * @param sortOrder An "order by" clause to sort the returned records
     * @return A list of entities of type T
     */
    fun fetchAll(context: Context, selection: String? = null, selectionArgs: Array<String>? = null, sortOrder: String? = null) : List<T>

    /**
     * Removes all entities of type T from the underlying store.
     *
     * @param context A context reference
     * @return The number of rows affected by the operation
     */
    fun purge(context: Context) : Int

    /**
     * Observes changes to the data set of entities of type T and emits a new record set through the
     * provided callback when the store is changed. This method returns a content observer that is
     * registered to watch the content uri of entities of type T by default, however, can register to
     * observe the [customWatchUri] argument if provided. By default the callback will be invoked as
     * soon as the observe method is invoked, unless [skipInitialLookup] is specified as true.
     *
     * @param context A context reference
     * @param callback An implemntation of the [ISetObserver] interface to handle the new record sets
     *                 emitted when a change is detected.
     * @param customWatchUri A custom uri to watch for changes
     * @param notifyDescendents Causes callback to be invoked on uris with additional data if true
     * @param selection A "where" clause to further refine the scope of the query operation
     * @param selectionArgs An array of arguments to bind to the selection parameter if provided
     * @param sortOrder An "order by" clause to sort the returned records
     * @param skipInitialLookup Will bypass the initial query if true otherwise the lookup is performed immediately
     * @return A content observer registered to either the entity's uri or the [customWatchUri] if provided
     */
    fun observe(context: Context, callback: ISetObserver<T>, customWatchUri: Uri? = null, notifyDescendents: Boolean = true, selection: String? = null, selectionArgs: Array<String>? = null, sortOrder: String? = null, skipInitialLookup: Boolean = false): ContentObserver

    /**
     * Unregisters a content observer from the uri to which it was listening.
     *
     * @param context A context reference
     * @param contentObserver The content observer to unregister
     */
    fun unregister(context: Context, contentObserver: ContentObserver)
}