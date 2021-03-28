package com.grubhub.persistence.repository.impl

import android.content.ContentValues
import android.content.Context
import android.database.ContentObserver
import android.net.Uri
import android.os.Handler
import android.os.Looper
import com.grubhub.persistence.entity.IEntityReadable
import com.grubhub.persistence.entity.IEntityWritable
import com.grubhub.persistence.repository.ISetObserver
import com.grubhub.persistence.provider.BaseContract
import com.grubhub.persistence.repository.IBaseRepository
import kotlin.concurrent.thread

abstract class BaseRepository<T : IEntityWritable, Tid : Any>(protected val contract: BaseContract, protected val mapper: IEntityReadable<T>) : IBaseRepository<T, Tid> {
    override fun insert(context: Context, entity: T): Uri? {
        val values = entity.toContentValues()
        if (contract.PRIMARY_KEY_AUTO_GEN) {
            // Entity has an auto generated id - do not pass the provided value
            values.remove(contract.PRIMARY_KEY_COLUMN)
        }
        return context.contentResolver.insert(contract.CONTENT_URI, values)
    }

    override fun sync(context: Context, vararg entities: T): Int {
        val count = context.contentResolver.bulkInsert(contract.BASE_SYNC_URI, entities.map { it.toContentValues() }.toTypedArray())
        context.contentResolver.notifyChange(contract.BASE_SYNC_URI, null)
        return count
    }

    override fun update(
        context: Context,
        id: Tid,
        values: ContentValues,
        selection: String?,
        args: Array<String>?
    ): Int {
        return context.contentResolver.update(contract.CONTENT_URI, values, selection, args)
    }

    override fun delete(
        context: Context,
        selection: String?,
        args: Array<String>?,
        vararg ids: Tid
    ): Int {
        val where = ids.map { "${contract.TABLE}.${contract.PRIMARY_KEY_COLUMN} = ?" }.joinToString(" or ")
        val whereArgs = ids.map { it.toString() }.toTypedArray()
        return context.contentResolver.delete(contract.CONTENT_URI, where, whereArgs)
    }

    override fun fetchById(context: Context, id: Tid): T? {
        var entity: T? = null
        context.contentResolver.query(contract.getContentUriWithId(id), contract.TABLE_PROJECTION, null, null, null)?.use { cursor ->
            if (cursor.count == 1 && cursor.moveToFirst()) {
                entity = mapper.fromCursor(cursor)
            }
        }
        return entity
    }

    override fun fetchAll(
        context: Context,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?
    ): List<T> {
        val entities = mutableListOf<T>()
        context.contentResolver.query(contract.CONTENT_URI, contract.TABLE_PROJECTION, selection, selectionArgs, sortOrder)?.use { cursor ->
            while (cursor.moveToNext() && !cursor.isAfterLast) {
                entities.add(mapper.fromCursor(cursor))
            }
        }
        return entities
    }

    override fun purge(context: Context): Int {
        return context.contentResolver.delete(contract.CONTENT_URI, null, null)
    }

    override fun observe(
        context: Context,
        callback: ISetObserver<T>,
        customWatchUri: Uri?,
        notifyDescendents: Boolean,
        selection: String?,
        selectionArgs: Array<String>?,
        sortOrder: String?,
        skipInitialLookup: Boolean
    ): ContentObserver {
        val observer = object : ContentObserver(null) {
            private val handler = Handler(Looper.getMainLooper())

            override fun onChange(selfChange: Boolean) {
                super.onChange(selfChange)
                thread {
                    val entities = fetchAll(context, selection, selectionArgs, sortOrder)
                    handler.post { callback.onEntitiesChanged(entities) }
                }
            }
        }
        val uri = customWatchUri ?: contract.CONTENT_URI
        context.contentResolver.registerContentObserver(uri, notifyDescendents, observer)
        if (!skipInitialLookup) observer.onChange(true)
        return observer
    }

    override fun unregister(context: Context, contentObserver: ContentObserver) {
        context.contentResolver.unregisterContentObserver(contentObserver)
    }
}