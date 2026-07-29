package com.example.network

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetSocketAddress
import java.net.Socket

object LinkSender {

    private const val DEFAULT_PORT = 8888
    private const val CONNECT_TIMEOUT_MS = 3000

    suspend fun sendLink(
        targetIp: String,
        url: String,
        port: Int = DEFAULT_PORT
    ): Result<String> = withContext(Dispatchers.IO) {
        try {
            val normalizedUrl = NetworkUtils.normalizeUrl(url)
            if (normalizedUrl.isEmpty()) {
                return@withContext Result.failure(IllegalArgumentException("الرابط فارغ"))
            }

            Socket().use { socket ->
                socket.connect(InetSocketAddress(targetIp, port), CONNECT_TIMEOUT_MS)
                socket.soTimeout = CONNECT_TIMEOUT_MS

                val writer = PrintWriter(socket.getOutputStream(), true)
                val reader = BufferedReader(InputStreamReader(socket.getInputStream()))

                writer.println("LINK:$normalizedUrl")

                val response = reader.readLine()
                if (response == "OK") {
                    Result.success("تم إرسال الرابط بنجاح إلى $targetIp")
                } else {
                    Result.success("تم إرسال الرابط إلى $targetIp")
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
