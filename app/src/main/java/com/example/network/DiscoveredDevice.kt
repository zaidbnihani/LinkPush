package com.example.network

data class DiscoveredDevice(
    val ip: String,
    val port: Int = 8888,
    val responseTimeMs: Long = 0,
    val isLocalDevice: Boolean = false,
    val savedName: String? = null,
    val savedLinkUrl: String? = null
)
