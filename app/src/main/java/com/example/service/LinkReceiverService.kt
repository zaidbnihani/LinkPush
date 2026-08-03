package com.example.service

import android.app.ActivityOptions
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.os.PowerManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.network.NetworkUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket

class LinkReceiverService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)
    private var serverSocket: ServerSocket? = null
    private var isListening = false

    private var wakeLock: PowerManager.WakeLock? = null
    private var wifiLock: android.net.wifi.WifiManager.WifiLock? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        acquireLocks()
    }

    private fun acquireLocks() {
        try {
            if (wakeLock == null || !wakeLock!!.isHeld) {
                val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager
                wakeLock = pm?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "LinkPush::ReceiverWakeLock")
                wakeLock?.acquire() // Indefinite lock to keep CPU active in background
            }
            if (wifiLock == null || !wifiLock!!.isHeld) {
                val wm = applicationContext.getSystemService(Context.WIFI_SERVICE) as? android.net.wifi.WifiManager
                @Suppress("DEPRECATION")
                wifiLock = wm?.createWifiLock(android.net.wifi.WifiManager.WIFI_MODE_FULL_HIGH_PERF, "LinkPush::WifiLock")
                wifiLock?.acquire()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun releaseLocks() {
        try {
            wakeLock?.let { if (it.isHeld) it.release() }
            wakeLock = null
            wifiLock?.let { if (it.isHeld) it.release() }
            wifiLock = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val action = intent?.action
        if (action == ACTION_STOP_SERVICE) {
            stopReceiver()
            stopSelf()
            return START_NOT_STICKY
        }

        val localIp = NetworkUtils.getLocalIpAddress(this) ?: "0.0.0.0"
        _currentIp.value = localIp

        val notification = createServiceNotification(localIp)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val serviceType = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                ServiceInfo.FOREGROUND_SERVICE_TYPE_CONNECTED_DEVICE
            } else {
                0
            }
            startForeground(NOTIFICATION_ID, notification, serviceType)
        } else {
            startForeground(NOTIFICATION_ID, notification)
        }

        startServerLoop()
        startHeartbeatLoop()
        _isServiceRunning.value = true

        return START_STICKY
    }

    private fun startHeartbeatLoop() {
        serviceScope.launch {
            while (isListening && !serviceJob.isCancelled) {
                try {
                    // Re-acquire locks if needed to persuade OS process manager every 1s
                    acquireLocks()
                    _heartbeatTicks.value += 1
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                delay(1000L) // Continuous 1-second system keep-alive
            }
        }
    }

    private fun startServerLoop() {
        if (isListening) return
        isListening = true

        serviceScope.launch {
            try {
                serverSocket = ServerSocket(PORT)
                while (isListening && !serviceJob.isCancelled) {
                    val clientSocket = try {
                        serverSocket?.accept()
                    } catch (e: Exception) {
                        null
                    }

                    clientSocket?.let { socket ->
                        launch { handleClientSocket(socket) }
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isListening = false
                _isServiceRunning.value = false
            }
        }
    }

    private fun handleClientSocket(socket: Socket) {
        try {
            socket.soTimeout = 4000
            val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
            val writer = PrintWriter(socket.getOutputStream(), true)

            val inputLine = reader.readLine()
            if (inputLine != null) {
                val trimmed = inputLine.trim()
                if (trimmed == "PING") {
                    writer.println("PONG")
                } else {
                    val urlToOpen = if (trimmed.startsWith("LINK:")) {
                        trimmed.removePrefix("LINK:")
                    } else {
                        trimmed
                    }

                    if (urlToOpen.isNotBlank()) {
                        writer.println("OK")
                        val normalizedUrl = NetworkUtils.normalizeUrl(urlToOpen)
                        _lastReceivedUrl.value = normalizedUrl
                        _receivedLinksCount.value += 1

                        wakeScreen()

                        // Always display high priority heads-up notification so screen wakes / banner appears
                        showLinkReceivedNotification(normalizedUrl)

                        // Attempt direct browser / activity launch from background with Android 14/15 BAL options
                        openUrlInBrowser(normalizedUrl)
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            try {
                socket.close()
            } catch (_: Exception) {}
        }
    }

    private fun wakeScreen() {
        try {
            val pm = getSystemService(Context.POWER_SERVICE) as? PowerManager
            @Suppress("DEPRECATION")
            val screenWakeLock = pm?.newWakeLock(
                PowerManager.FULL_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP or PowerManager.ON_AFTER_RELEASE,
                "LinkPush::ScreenWakeLock"
            )
            screenWakeLock?.acquire(3000L)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun openUrlInBrowser(url: String) {
        var launched = false

        // Method 1: Launch via PendingIntent with MODE_BACKGROUND_ACTIVITY_START_ALLOWED (Android 14 & 15 BAL fix)
        try {
            val launcherIntent = Intent(this, com.example.UrlLauncherActivity::class.java).apply {
                putExtra("url", url)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
            }
            val pendingIntent = PendingIntent.getActivity(
                this,
                (System.currentTimeMillis() % 10000).toInt(),
                launcherIntent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) { // Android 14 (34) & 15 (35)
                val options = ActivityOptions.makeBasic().apply {
                    setPendingIntentBackgroundActivityStartMode(ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED)
                }
                pendingIntent.send(this, 0, null, null, null, null, options.toBundle())
            } else {
                pendingIntent.send()
            }
            launched = true
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Method 2: Fallback to direct startActivity with BAL options
        if (!launched) {
            try {
                val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                }
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                    val options = ActivityOptions.makeBasic().apply {
                        setPendingIntentBackgroundActivityStartMode(ActivityOptions.MODE_BACKGROUND_ACTIVITY_START_ALLOWED)
                    }
                    startActivity(browserIntent, options.toBundle())
                } else {
                    startActivity(browserIntent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "خدمة استقبال الروابط LinkPush",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "خدمة خلفية تعمل على تلقي الروابط المرسلة من الأجهزة الأخرى على الشبكة"
            }

            val alertChannel = NotificationChannel(
                ALERT_CHANNEL_ID,
                "إشعارات الروابط المستلمة",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "إشعارات تنبيهية تظهر فورا عند استقبال رابط جديد"
                enableVibration(true)
                setShowBadge(true)
            }

            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
            manager.createNotificationChannel(alertChannel)
        }
    }

    private fun createServiceNotification(ip: String) = NotificationCompat.Builder(this, CHANNEL_ID)
        .setContentTitle("LinkPush - خدمة الاستقبال تعمل في الخلفية")
        .setContentText("جاهز لاستقبال الروابط تلقائياً على $ip:$PORT")
        .setSmallIcon(android.R.drawable.stat_sys_download_done)
        .setOngoing(true)
        .setPriority(NotificationCompat.PRIORITY_LOW)
        .setContentIntent(getMainActivityPendingIntent())
        .build()

    private fun showLinkReceivedNotification(url: String) {
        val launcherIntent = Intent(this, com.example.UrlLauncherActivity::class.java).apply {
            putExtra("url", url)
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP)
        }
        val pendingIntent = PendingIntent.getActivity(
            this,
            (System.currentTimeMillis() % 10000).toInt(),
            launcherIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, ALERT_CHANNEL_ID)
            .setContentTitle("🔗 تم استقبال رابط جديد!")
            .setContentText(url)
            .setSmallIcon(android.R.drawable.ic_menu_send)
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setCategory(NotificationCompat.CATEGORY_CALL)
            .setDefaults(NotificationCompat.DEFAULT_ALL)
            .setFullScreenIntent(pendingIntent, true)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .addAction(android.R.drawable.ic_menu_view, "فتح في المتصفح", pendingIntent)
            .build()

        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(RECEIVED_NOTIFICATION_ID, notification)
    }

    private fun getMainActivityPendingIntent(): PendingIntent {
        val intent = Intent(this, MainActivity::class.java)
        return PendingIntent.getActivity(
            this,
            0,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)
        // Automatically restart service if task is swiped away from Recents
        val restartServiceIntent = Intent(applicationContext, LinkReceiverService::class.java).apply {
            action = ACTION_START_SERVICE
        }
        val pendingIntent = PendingIntent.getService(
            applicationContext, 1, restartServiceIntent,
            PendingIntent.FLAG_ONE_SHOT or PendingIntent.FLAG_IMMUTABLE
        )
        val alarmManager = getSystemService(Context.ALARM_SERVICE) as? AlarmManager
        alarmManager?.set(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + 1000, pendingIntent)
    }

    private fun stopReceiver() {
        isListening = false
        releaseLocks()
        try {
            serverSocket?.close()
        } catch (_: Exception) {}
        serviceJob.cancel()
        _isServiceRunning.value = false
    }

    override fun onDestroy() {
        stopReceiver()
        super.onDestroy()
    }

    override fun onBind(intent: Intent?): IBinder? = null

    companion object {
        const val PORT = 8888
        const val CHANNEL_ID = "linkpush_foreground_channel"
        const val ALERT_CHANNEL_ID = "linkpush_alert_channel"
        const val NOTIFICATION_ID = 1001
        const val RECEIVED_NOTIFICATION_ID = 1002

        const val ACTION_START_SERVICE = "com.example.service.START"
        const val ACTION_STOP_SERVICE = "com.example.service.STOP"

        private val _isServiceRunning = MutableStateFlow(false)
        val isServiceRunning: StateFlow<Boolean> = _isServiceRunning.asStateFlow()

        private val _currentIp = MutableStateFlow("0.0.0.0")
        val currentIp: StateFlow<String> = _currentIp.asStateFlow()

        private val _lastReceivedUrl = MutableStateFlow<String?>(null)
        val lastReceivedUrl: StateFlow<String?> = _lastReceivedUrl.asStateFlow()

        private val _receivedLinksCount = MutableStateFlow(0)
        val receivedLinksCount: StateFlow<Int> = _receivedLinksCount.asStateFlow()

        private val _heartbeatTicks = MutableStateFlow(0L)
        val heartbeatTicks: StateFlow<Long> = _heartbeatTicks.asStateFlow()

        fun startService(context: Context) {
            val intent = Intent(context, LinkReceiverService::class.java).apply {
                action = ACTION_START_SERVICE
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, LinkReceiverService::class.java).apply {
                action = ACTION_STOP_SERVICE
            }
            context.startService(intent)
        }
    }
}
