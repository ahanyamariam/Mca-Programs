package com.example.lab9.data.local.dao

import androidx.room.*
import com.example.lab9.data.local.entity.SyncMetaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface SyncMetaDao {
    @Query("SELECT * FROM sync_meta WHERE id = 1")
    fun getSyncMeta(): Flow<SyncMetaEntity?>

    @Query("SELECT * FROM sync_meta WHERE id = 1")
    suspend fun getSyncMetaOnce(): SyncMetaEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(meta: SyncMetaEntity)
}
