package com.grubhub.challenge.data.provider

import android.content.ContentValues
import android.database.Cursor
import android.net.Uri
import androidx.sqlite.db.SupportSQLiteDatabase
import com.grubhub.challenge.data.ChallengeDb
import com.grubhub.persistence.provider.BaseContract
import com.grubhub.persistence.provider.BaseContentProvider

class Provider : BaseContentProvider() {

    /**
     * Here we provide a list of entity contracts for which the data layer is responsible for routing.
     */
    override val entities: List<BaseContract> = listOf(
        ProviderContract.Favorites
    )

    private lateinit var db: ChallengeDb

    override fun onCreate(): Boolean {
        db = ChallengeDb.getInstance(context!!)
        return super.onCreate()
    }

    override fun routeBulkInsert(uri: Uri, values: Array<ContentValues>): Int {
        return when (uriMatcher.match(uri)) {
            else -> 0
        }
    }

    override fun routeUpdate(uri: Uri, value: ContentValues): Int {
        return when (uriMatcher.match(uri)) {
            else -> 0
        }
    }

    override fun routeQuery(uri: Uri, selection: String?, selectionArgs: Array<String>?): Cursor? {
        return when (uriMatcher.match(uri)) {
            else -> null
        }
    }

    override fun getDbAccessor(writable: Boolean): SupportSQLiteDatabase {
        return if (writable) db.openHelper.writableDatabase else db.openHelper.readableDatabase
    }

}