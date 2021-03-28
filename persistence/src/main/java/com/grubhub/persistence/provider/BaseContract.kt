package com.grubhub.persistence.provider

import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.net.Uri

/**
 * The data contract required by all entities. This is what enables the functionality of the base
 * repository. The details provided by this contract are used to create uri's and column mapping
 * logic for Android's ContentProviders.
 */
abstract class BaseContract {
    companion object {
        const val SYNC_PATH = "sync"
        private const val ITEM_PREFIX = "vnd.android.cursor.item"
        private const val LIST_PREFIX = "vnd.android.cursor.dir"
    }

    abstract val AUTHORITY: String
    abstract val TABLE: String
    abstract val PRIMARY_KEY_COLUMN: String
    abstract val PRIMARY_KEY_AUTO_GEN: Boolean
    protected abstract val COLUMNS: List<String>

    fun isEntity(contentValues: ContentValues): Boolean {
        if (contentValues.size() != COLUMNS.size) return false
        COLUMNS.forEach { column ->
            if (!contentValues.containsKey(column)) return false
        }
        return true
    }

    open val CONFLICT_STRATEGY = SQLiteDatabase.CONFLICT_REPLACE

    open val URI_PATH
        get() = TABLE

    open val CONTENT_URI
        get() = getBaseUri().buildUpon().appendPath(URI_PATH).build()

    val BASE_SYNC_URI
        get() = CONTENT_URI.buildUpon().appendPath(SYNC_PATH).build()

    open val TABLE_PROJECTION
        get() = COLUMNS.map { "$TABLE.$it AS ${TABLE}_$it" }.toTypedArray()

    open fun <T : Any> getContentUriWithId(id: T) = CONTENT_URI.buildUpon().appendPath(id.toString()).build()!!

    fun getTableSelection(prefix: String? = null): String {
        var projection = TABLE_PROJECTION.joinToString(", ")
        prefix?.let {
            projection = projection.replace("${TABLE}.", "${it}.")
        }
        return projection
    }

    fun getType(hasId: Boolean) = if (hasId) "$ITEM_PREFIX/$TABLE" else "$LIST_PREFIX/$TABLE"

    inline fun <reified T> getIdFromUri(uri: Uri): T {
        val id = uri.lastPathSegment!!
        return when (T::class) {
            Long::class -> id.toLong() as T
            String::class -> id as T
            Double::class -> id.toDouble() as T
            Float::class -> id.toFloat() as T
            else -> throw UnsupportedOperationException("Cannot cast uri segment to unsupported type ${T::class}")
        }
    }

    protected fun getBaseUri() = Uri.parse("content://$AUTHORITY")
}