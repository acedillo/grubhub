package com.grubhub.persistence.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.net.Uri
import android.util.Log
import androidx.annotation.CallSuper
import androidx.sqlite.db.SupportSQLiteDatabase
import androidx.sqlite.db.SupportSQLiteQueryBuilder
import java.lang.Exception

abstract class BaseContentProvider : ContentProvider() {

    companion object {
        const val ID_ROUTE_OFFSET = 9999
    }

    abstract fun routeBulkInsert(uri: Uri, values: Array<ContentValues>): Int

    abstract fun routeUpdate(uri: Uri, value: ContentValues): Int

    abstract fun routeQuery(uri: Uri, selection: String?, selectionArgs: Array<String>?) : Cursor?

    abstract fun getDbAccessor(writable: Boolean) : SupportSQLiteDatabase

    abstract val entities: List<BaseContract>

    protected val uriMatcher = UriMatcher(UriMatcher.NO_MATCH)

    @CallSuper
    override fun onCreate(): Boolean {
        prepareRoutes()
        return true
    }

    /**
     * Automatically scaffolds routes based on the supplied entities
     */
    fun prepareRoutes() {
        for ((index, contract) in entities.withIndex()) {
            uriMatcher.addURI(contract.AUTHORITY, contract.URI_PATH, index)
            uriMatcher.addURI(contract.AUTHORITY, "${contract.URI_PATH}/*", ID_ROUTE_OFFSET + index)
        }
    }

    override fun bulkInsert(uri: Uri, values: Array<ContentValues>): Int {
        if (isCustomRoute(uri)) return routeBulkInsert(uri, values)
        if (uri.lastPathSegment == BaseContract.SYNC_PATH) return sync(uri, values)

        try {
            val rowsAffected = super.bulkInsert(uri, values)
            context?.contentResolver?.notifyChange(uri, null)
            return rowsAffected
        } catch (e: Exception) {
            Log.e("SQL", "Failed bulk insert!", e)
        }
        return 0
    }

    private fun sync(uri: Uri, values: Array<ContentValues>): Int {
        var changes = 0
        getContract(uri)?.let { contract ->
            val newIds = values.map { it[contract.PRIMARY_KEY_COLUMN] }
            val currentIds = mutableListOf<Any>()
            query(contract.CONTENT_URI, arrayOf(contract.PRIMARY_KEY_COLUMN), null, null, null)?.use { cursor ->
                while (cursor.moveToNext() && !cursor.isAfterLast) {
                    val index = cursor.getColumnIndex(contract.PRIMARY_KEY_COLUMN)
                    when (cursor.getType(0)) {
                        Cursor.FIELD_TYPE_STRING -> currentIds.add(cursor.getString(index))
                        Cursor.FIELD_TYPE_INTEGER -> currentIds.add(cursor.getLong(index))
                    }
                }
            }

            val netNewEntities = values.filterNot {
                currentIds.contains(it[contract.PRIMARY_KEY_COLUMN]) }.toTypedArray()
            val updatedEntities = values.filter { currentIds.contains(it[contract.PRIMARY_KEY_COLUMN]) }.toTypedArray()
            val deletedIds = currentIds.filterNot { newIds.contains(it) }.toTypedArray()

            with (getDbAccessor(true)) {
                try {
                    beginTransaction()
                    changes += bulkInsert(contract.CONTENT_URI, netNewEntities)
                    updatedEntities.forEach {
                        val updateUri = contract.getContentUriWithId(it[contract.PRIMARY_KEY_COLUMN])
                        changes += update(updateUri, it, null, null)
                    }
                    deletedIds.forEach {
                        val deleteUri = contract.getContentUriWithId(it)
                        changes += delete(deleteUri, null, null)
                    }
                    setTransactionSuccessful()
                } catch (e: Exception) {
                    changes = 0
                } finally {
                    endTransaction()
                }
            }
        }
        return changes
    }

    override fun insert(uri: Uri, contentValues: ContentValues?): Uri? {
        contentValues?.let { values ->
            getContract(uri)?.let { contract ->
                val id = getDbAccessor( true).insert(contract.TABLE, contract.CONFLICT_STRATEGY, values)
                val newUri = contract.getContentUriWithId(
                    if (!contract.PRIMARY_KEY_AUTO_GEN) {
                        // The table does not automatically generate the primary id, therefore
                        // it had to have been passed in - return it
                        values.get(contract.PRIMARY_KEY_COLUMN)
                    } else {
                        id
                    }
                )

                context?.contentResolver?.notifyChange(newUri, null)

                return newUri
            }
        }

        return null
    }

    override fun update(uri: Uri, values: ContentValues?, selection: String?, selectionArgs: Array<String>?): Int {
        if (isCustomRoute(uri)) return if (values != null) routeUpdate(uri, values) else 0

        var rowsAffected = 0
        getContract(uri)?.let { contract ->
            val selectionDetails = prepareSelection(uri, contract, selection, selectionArgs)
            rowsAffected = getDbAccessor( true).update(contract.TABLE, contract.CONFLICT_STRATEGY, values, selectionDetails.first, selectionDetails.second)

            if (rowsAffected > 0) context?.contentResolver?.notifyChange(uri, null)
        }

        return rowsAffected
    }

    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        var rowsAffected = 0
        getContract(uri)?.let { contract ->
            val selectionDetails = prepareSelection(uri, contract, selection, selectionArgs)
            rowsAffected = getDbAccessor( true).delete(contract.TABLE, selectionDetails.first, selectionDetails.second)

            context?.contentResolver?.notifyChange(uri, null)
        }

        return rowsAffected
    }

    override fun query(uri: Uri, projection: Array<String>?, selection: String?, selectionArgs: Array<String>?, sortOrder: String?): Cursor? {
        if (isCustomRoute(uri)) return routeQuery(uri, selection, selectionArgs)

        getContract(uri)?.let { contract ->
            val selectionDetails = prepareSelection(uri, contract, selection, selectionArgs)
            val columns = if (projection == null || !projection.any()) arrayOf("*") else projection
            val query = SupportSQLiteQueryBuilder.builder(contract.TABLE)
                .columns(columns)
                .selection(selectionDetails.first, selectionDetails.second)
                .orderBy(sortOrder)
                .create()

            return getDbAccessor( false).query(query)
        }

        return null
    }

    override fun getType(uri: Uri): String? {
        getContract(uri)?.let { contract ->
            val hasId = uri.pathSegments.size > 2
            return contract.getType(hasId)
        }

        return null
    }

    private fun prepareSelection(uri: Uri, contract: BaseContract, selection: String?, selectionArgs: Array<String>?): Pair<String?, Array<String>?> {
        val matchCode = uriMatcher.match(uri)
        var where = selection
        val argList = selectionArgs?.toMutableList() ?: mutableListOf()
        if (matchCode >= ID_ROUTE_OFFSET) {
            val id = uri.lastPathSegment!!
            where = if (where.isNullOrEmpty()) {
                "${contract.PRIMARY_KEY_COLUMN} = ?"
            } else {
                "${contract.PRIMARY_KEY_COLUMN} = ? and ($where)"
            }
            argList.add(0, id)
        }

        return Pair(where, argList.toTypedArray())
    }

    protected fun getContract(uri: Uri) : BaseContract? {
        val match = uriMatcher.match(uri)
        if (match == UriMatcher.NO_MATCH) return null

        val index = if (match >= ID_ROUTE_OFFSET) match - ID_ROUTE_OFFSET else match

        return entities[index]
    }

    private fun isCustomRoute(uri: Uri): Boolean {
        val match = uriMatcher.match(uri)
        return match != UriMatcher.NO_MATCH && match >= 1000 && match < 9000
    }

}