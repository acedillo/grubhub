package com.grubhub.challenge.data.provider

import android.provider.BaseColumns
import com.grubhub.persistence.provider.BaseContract

sealed class ProviderContract : BaseContract() {
    companion object {
        const val DATABASE_NAME = "challenge.db"
    }

    override val AUTHORITY = "com.grubhub.challenge"

    /**
     * Contract definition for entities of type Favorite
     */
    object Favorites : ProviderContract() {
        const val TABLE_NAME = "favorites"
        const val AUTO_GEN = true

        override val TABLE = TABLE_NAME
        override val PRIMARY_KEY_COLUMN = Columns.ID
        override val PRIMARY_KEY_AUTO_GEN = AUTO_GEN
        override val COLUMNS = with(Columns) { listOf(
            ID, IMAGE_URL, EPISODE_TITLE
        ) }

        object Columns {
            const val ID = BaseColumns._ID
            const val IMAGE_URL = "image_url"
            const val EPISODE_TITLE = "title"
        }
    }
}