package com.example.data

import kotlinx.coroutines.flow.Flow

class SavedDeviceRepository(private val dao: SavedDeviceDao) {
    val allSavedDevices: Flow<List<SavedDevice>> = dao.getAllSavedDevices()

    suspend fun getDeviceByIp(ip: String): SavedDevice? {
        return dao.getDeviceByIp(ip)
    }

    suspend fun saveDevice(device: SavedDevice): Long {
        return dao.insertDevice(device)
    }

    suspend fun deleteDevice(device: SavedDevice) {
        dao.deleteDevice(device)
    }

    suspend fun deleteDeviceById(id: Long) {
        dao.deleteDeviceById(id)
    }

    suspend fun deleteAllSavedDevices() {
        dao.deleteAllSavedDevices()
    }
}
