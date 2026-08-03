package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.sync.Semaphore
import kotlinx.coroutines.sync.withPermit
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket

object NetworkScanner {

    private const val DEFAULT_PORT = 8888
    private const val SOCKET_TIMEOUT_MS = 250
    private const val MAX_CONCURRENCY = 35

    suspend fun scanSubnet(
        subnetPrefix: String,
        localIp: String?,
        gatewayIp: String? = null,
        onDeviceFound: (DiscoveredDevice) -> Unit,
        onProgressUpdate: (scannedCount: Int, total: Int) -> Unit
    ): List<DiscoveredDevice> = withContext(Dispatchers.IO) {
        val discoveredList = mutableListOf<DiscoveredDevice>()
        val semaphore = Semaphore(MAX_CONCURRENCY)
        var scannedCounter = 0
        val totalHostCount = 254

        coroutineScope {
            val deferreds = (1..totalHostCount).map { hostIndex ->
                async {
                    semaphore.withPermit {
                        val targetIp = "$subnetPrefix$hostIndex"
                        val isLocal = targetIp == localIp
                        val isGateway = targetIp == gatewayIp

                        val discovered = checkDevice(targetIp, isLocal, isGateway)
                        synchronized(this@NetworkScanner) {
                            scannedCounter++
                            onProgressUpdate(scannedCounter, totalHostCount)
                            if (discovered != null) {
                                discoveredList.add(discovered)
                                onDeviceFound(discovered)
                            }
                        }
                    }
                }
            }
            deferreds.awaitAll()
        }

        discoveredList.sortedBy { it.ip }
    }

    private fun checkDevice(ip: String, isLocal: Boolean, isGateway: Boolean): DiscoveredDevice? {
        if (isLocal || isGateway) return null
        val startTime = System.currentTimeMillis()
        val portsToCheck = listOf(DEFAULT_PORT)

        for (port in portsToCheck) {
            try {
                Socket().use { socket ->
                    socket.connect(InetSocketAddress(ip, port), SOCKET_TIMEOUT_MS)
                    socket.soTimeout = SOCKET_TIMEOUT_MS

                    val responseTime = System.currentTimeMillis() - startTime

                    var isLinkPushApp = false
                    if (port == DEFAULT_PORT) {
                        runCatching {
                            val writer = PrintWriter(socket.getOutputStream(), true)
                            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                            writer.println("PING")
                            val resp = reader.readLine()
                            if (resp == "PONG") isLinkPushApp = true
                        }
                    }

                    val defaultName = when {
                        isGateway -> "الراوتر / Gateway ($ip)"
                        isLinkPushApp -> "جهاز LinkPush ($ip)"
                        else -> "جهاز متصل ($ip)"
                    }

                    val defaultUrl = when {
                        isGateway -> "http://$ip"
                        port == 80 -> "http://$ip"
                        port == 8080 -> "http://$ip:8080"
                        else -> ""
                    }

                    return DiscoveredDevice(
                        ip = ip,
                        port = port,
                        responseTimeMs = responseTime,
                        isLocalDevice = isLocal,
                        savedName = defaultName,
                        savedLinkUrl = defaultUrl
                    )
                }
            } catch (_: Exception) {
                // Connection failed on this port
            }
        }
        return null
    }
}
