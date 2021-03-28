package com.grubhub.challenge.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.grubhub.challenge.data.entity.Favorite
import com.grubhub.challenge.data.provider.ProviderContract

/**
 * The database store for the application.
 */
@Database(
    entities = [Favorite::class],
    version = 1,
    exportSchema = false
)
abstract class ChallengeDb : RoomDatabase() {
    companion object {
        private lateinit var instance: ChallengeDb

        fun getInstance(context: Context) : ChallengeDb {
            if (!::instance.isInitialized) {
                instance = Room.databaseBuilder(context, ChallengeDb::class.java, ProviderContract.DATABASE_NAME)
                    .fallbackToDestructiveMigration()
                    .build()
            }
            return instance
        }

        fun getTestInstance(context: Context) : ChallengeDb {
            return Room.inMemoryDatabaseBuilder(context, ChallengeDb::class.java)
                .build()
        }
    }
}