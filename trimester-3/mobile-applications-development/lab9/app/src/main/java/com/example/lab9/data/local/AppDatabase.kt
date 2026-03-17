package com.example.lab9.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.lab9.data.local.dao.AnnouncementDao
import com.example.lab9.data.local.dao.CourseDao
import com.example.lab9.data.local.dao.SyncMetaDao
import com.example.lab9.data.local.entity.AnnouncementEntity
import com.example.lab9.data.local.entity.CourseEntity
import com.example.lab9.data.local.entity.SyncMetaEntity

@Database(
    entities = [CourseEntity::class, AnnouncementEntity::class, SyncMetaEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun courseDao(): CourseDao
    abstract fun announcementDao(): AnnouncementDao
    abstract fun syncMetaDao(): SyncMetaDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "portal_database"
                ).build().also { INSTANCE = it }
            }
        }
    }
}
