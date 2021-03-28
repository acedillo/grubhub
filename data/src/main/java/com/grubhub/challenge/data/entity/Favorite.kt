package com.grubhub.challenge.data.entity

import android.content.ContentValues
import android.database.Cursor
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.grubhub.challenge.data.provider.ProviderContract.Favorites.TABLE_NAME
import com.grubhub.challenge.data.provider.ProviderContract.Favorites.AUTO_GEN
import com.grubhub.challenge.data.provider.ProviderContract.Favorites.Columns
import com.grubhub.persistence.entity.IEntityReadable
import com.grubhub.persistence.entity.IEntityWritable
import com.grubhub.persistence.entity.MappingCursor

/**
 * The database schema for the Favorite entity.
 */
@Entity(tableName = TABLE_NAME)
data class Favorite(
    @PrimaryKey(autoGenerate = AUTO_GEN) @ColumnInfo(name = Columns.ID) val id: Long,
    @ColumnInfo(name = Columns.IMAGE_URL) val imageUrl: String,
    @ColumnInfo(name = Columns.EPISODE_TITLE) val episodeTitle: String
) : IEntityWritable {

    companion object : IEntityReadable<Favorite> {
        override fun fromCursor(cursor: Cursor) = with(MappingCursor(cursor)) {
            mappedTable = TABLE_NAME

            Favorite(
                id = getColumn(Columns.ID),
                imageUrl = getColumn(Columns.IMAGE_URL),
                episodeTitle = getColumn(Columns.EPISODE_TITLE)
            )
        }
    }

    override fun toContentValues() = ContentValues().apply {
        put(Columns.ID, id)
        put(Columns.IMAGE_URL, imageUrl)
        put(Columns.EPISODE_TITLE, episodeTitle)
    }

}