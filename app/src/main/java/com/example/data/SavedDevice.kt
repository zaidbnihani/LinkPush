package com.example.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_devices")
data class SavedDevice(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    @ColumnInfo(name = "device_ip")
    val deviceIp: String,

    @ColumnInfo(name = "device_name")
    val deviceName: String,

    @ColumnInfo(name = "link_url")
    val linkUrl: String
)
