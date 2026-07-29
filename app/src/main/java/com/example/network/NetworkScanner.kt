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
    private const val MAX_CONCURRENCY = 30

    suspend fun scanSubnet(
        subnetPrefix: String,
        localIp: String?,
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

                        val discovered = checkDevice(targetIp, DEFAULT_PORT, isLocal)
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

    private fun checkDevice(ip: String, port: Int, isLocal: Boolean): DiscoveredDevice? {
        val startTime = System.currentTimeMillis()
        try {
            Socket().use { socket ->
                socket.connect(InetSocketAddress(ip, port), SOCKET_TIMEOUT_MS)
                socket.soTimeout = SOCKET_TIMEOUT_MS

                val writer = PrintWriter(socket.getOutputStream(), true)
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                writer.println("PING")
                val response = reader.readLine()
                val responseTime = System.currentTimeMillis() - startTime

                if (response == "PONG" || response != null) {
                    return DiscoveredDevice(
                        ip = ip,
                        port = port,
                        responseTimeMs = responseTime,
                        isLocalDevice = isLocal
                    )
                }
            }
        } catch (_: Exception) {
            // Socket timeout or refused connection - not a LinkPush server
        }
        return null
    }
}
