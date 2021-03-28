package com.grubhub.persistence.entity

import android.database.Cursor
import java.lang.IllegalStateException
import java.lang.UnsupportedOperationException

/**
 * A returned cursor can be cast to this class to quickly and easily map column names to the
 * appropriate type.
 *
 * Created by mewers on 2019-07-17
 */
class MappingCursor(val cursor: Cursor) {
    /**
     * Set this on the cursor to ensure all subsequent usages of [getColumnNullable] and
     * [getColumn] are resolved using the table prefix.
     */
    var mappedTable: String? = null

    /**
     * Attempts to retrieve a column of given name from the cursor object. If the column name is not found within
     * the cursor row, an [IllegalStateException] is thrown. Also if the requested type is not supported an
     * [UnsupportedOperationException] is thrown. This method may return null.
     */
    inline fun <reified T> getColumnNullable(name: String, prefix: String? = mappedTable): T? {
        var index = cursor.getColumnIndex(name)
        if (index < 0 && prefix != null) index = cursor.getColumnIndex("${prefix}_$name")
        if (index < 0) throw IllegalStateException("Could not resolve column $name")
        if (cursor.isNull(index)) return null
        return when (T::class) {
            Int::class -> cursor.getInt(index) as? T
            String::class -> cursor.getString(index) as? T
            Boolean::class -> (cursor.getInt(index) == 1) as? T
            Long::class -> cursor.getLong(index) as? T
            Short::class -> cursor.getShort(index) as? T
            Float::class -> cursor.getFloat(index) as? T
            Double::class -> cursor.getDouble(index) as? T
            ByteArray::class -> cursor.getBlob(index) as? T
            else -> throw UnsupportedOperationException("Cannot map column $name to unsupported type ${T::class}")
        }
    }


    /**
     * Attempts to retrieve a column of given name from the cursor object. If the column name is not found within
     * the cursor row, an [IllegalStateException] is thrown. Also if the requested type is not supported an
     * [UnsupportedOperationException] is thrown.
     */
    inline fun <reified T> getColumn(name: String, prefix: String? = mappedTable) = this.getColumnNullable<T>(name, prefix)!!
}