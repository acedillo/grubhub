package com.grubhub.persistence.entity

import android.database.Cursor

interface IEntityReadable<T : IEntityWritable> {
    /**
     * Reads a cursor to inflate an entity of type T
     *
     * @return Database entity typed T
     */
    fun fromCursor(cursor: Cursor) : T
}