package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface SavedDeviceDao {
    @Query("SELECT * FROM saved_devices ORDER BY id DESC")
    fun getAllSavedDevices(): Flow<List<SavedDevice>>

    @Query("SELECT * FROM saved_devices WHERE device_ip = :ip LIMIT 1")
    suspend fun getDeviceByIp(ip: String): SavedDevice?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDevice(device: SavedDevice): Long

    @Update
    suspend fun updateDevice(device: SavedDevice)

    @Delete
    suspend fun deleteDevice(device: SavedDevice)

    @Query("DELETE FROM saved_devices WHERE id = :id")
    suspend fun deleteDeviceById(id: Long)

    @Query("DELETE FROM saved_devices")
    suspend fun deleteAllSavedDevices()
}
