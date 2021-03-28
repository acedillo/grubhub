package com.grubhub.persistence.entity

import android.content.ContentValues

interface IEntityWritable {
    /**
     * Serializes an entity to content values for db write operations
     *
     * @return A [ContentValues] object containing the fields of the entity
     */
    fun toContentValues(): ContentValues
}