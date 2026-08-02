package com.example.ui

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.SavedDevice
import com.example.data.SavedDeviceRepository
import com.example.network.DiscoveredDevice
import com.example.network.LinkSender
import com.example.network.NetworkScanner
import com.example.network.NetworkUtils
import com.example.service.LinkReceiverService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

sealed interface SendState {
    object Idle : SendState
    data class Sending(val targetIp: String) : SendState
    data class Success(val message: String) : SendState
    data class Error(val message: String) : SendState
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: SavedDeviceRepository

    val savedDevices: StateFlow<List<SavedDevice>>

    val isServiceRunning: StateFlow<Boolean> = LinkReceiverService.isServiceRunning
    val receiverIp: StateFlow<String> = LinkReceiverService.currentIp
    val lastReceivedUrl: StateFlow<String?> = LinkReceiverService.lastReceivedUrl
    val receivedLinksCount: StateFlow<Int> = LinkReceiverService.receivedLinksCount

    private val _localIp = MutableStateFlow<String?>(null)
    val localIp: StateFlow<String?> = _localIp.asStateFlow()

    private val _isScanning = MutableStateFlow(false)
    val isScanning: StateFlow<Boolean> = _isScanning.asStateFlow()

    private val _scanProgress = MutableStateFlow(0f)
    val scanProgress: StateFlow<Float> = _scanProgress.asStateFlow()

    private val _discoveredDevices = MutableStateFlow<List<DiscoveredDevice>>(emptyList())
    val discoveredDevices: StateFlow<List<DiscoveredDevice>> = _discoveredDevices.asStateFlow()

    private val _sendState = MutableStateFlow<SendState>(SendState.Idle)
    val sendState: StateFlow<SendState> = _sendState.asStateFlow()

    private val _defaultPushUrl = MutableStateFlow("")
    val defaultPushUrl: StateFlow<String> = _defaultPushUrl.asStateFlow()

    init {
        val database = AppDatabase.getDatabase(application)
        repository = SavedDeviceRepository(database.savedDeviceDao())
        savedDevices = repository.allSavedDevices.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        refreshLocalIp()
        // Automatically start receiver service on launch
        LinkReceiverService.startService(application)
        // Automatically scan network for devices on app start
        scanNetwork()
    }

    fun refreshLocalIp() {
        _localIp.value = NetworkUtils.getLocalIpAddress(getApplication())
    }

    fun setDefaultPushUrl(url: String) {
        _defaultPushUrl.value = url
    }

    fun toggleService(enable: Boolean) {
        val context = getApplication<Application>()
        if (enable) {
            LinkReceiverService.startService(context)
        } else {
            LinkReceiverService.stopService(context)
        }
    }

    fun scanNetwork() {
        if (_isScanning.value) return
        val currentLocalIp = NetworkUtils.getLocalIpAddress(getApplication())
        val gatewayIp = NetworkUtils.getGatewayIpAddress(getApplication())
        _localIp.value = currentLocalIp

        if (currentLocalIp.isNullOrBlank() || currentLocalIp == "0.0.0.0") {
            _sendState.value = SendState.Error("برجاء الاتصال بشبكة الواي فاي للبدء")
            return
        }

        val prefix = NetworkUtils.getSubnetPrefix(currentLocalIp)
        if (prefix.isNullOrEmpty()) {
            _sendState.value = SendState.Error("تعذر تحديد نطاق IP الشبكة")
            return
        }

        viewModelScope.launch {
            _isScanning.value = true
            _scanProgress.value = 0f
            _discoveredDevices.value = emptyList()

            val tempDiscovered = mutableListOf<DiscoveredDevice>()

            NetworkScanner.scanSubnet(
                subnetPrefix = prefix,
                localIp = currentLocalIp,
                gatewayIp = gatewayIp,
                onDeviceFound = { device ->
                    // Exclude self (local device) from discovered list as requested
                    if (device.isLocalDevice) return@scanSubnet

                    // Match with saved devices to enrich name and URL
                    val saved = savedDevices.value.find { it.deviceIp == device.ip }
                    val enrichedDevice = device.copy(
                        savedName = saved?.deviceName ?: device.savedName,
                        savedLinkUrl = saved?.linkUrl ?: device.savedLinkUrl
                    )
                    tempDiscovered.add(enrichedDevice)
                    _discoveredDevices.value = tempDiscovered.toList()
                },
                onProgressUpdate = { scanned, total ->
                    _scanProgress.value = scanned.toFloat() / total.toFloat()
                }
            )

            _isScanning.value = false
        }
    }

    fun sendLinkToDevice(targetIp: String, customUrl: String? = null, port: Int = 8888) {
        val urlToSend = customUrl?.takeIf { it.isNotBlank() } ?: defaultPushUrl.value
        val normalizedUrl = NetworkUtils.normalizeUrl(urlToSend)

        viewModelScope.launch {
            _sendState.value = SendState.Sending(targetIp)
            val result = LinkSender.sendLink(targetIp, normalizedUrl, port)
            result.onSuccess { msg ->
                _sendState.value = SendState.Success(msg)
            }.onFailure { err ->
                // If port 8888 connection refused / failed (e.g. router or device without LinkPush server),
                // fallback to opening the URL or router/device web page in the browser!
                val fallbackUrl = if (normalizedUrl.isNotEmpty()) normalizedUrl else "http://$targetIp"
                try {
                    val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(fallbackUrl)).apply {
                        addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                    }
                    getApplication<Application>().startActivity(intent)
                    _sendState.value = SendState.Success("تم فتح الرابط في المتصفح بنجاح ($targetIp)")
                } catch (e: Exception) {
                    _sendState.value = SendState.Error("فشل الإرسال: ${err.localizedMessage ?: "فشل الاتصال"}")
                }
            }
        }
    }

    fun saveDeviceMapping(ip: String, customName: String, linkUrl: String) {
        viewModelScope.launch {
            val existing = repository.getDeviceByIp(ip)
            val deviceToSave = existing?.copy(
                deviceName = customName.trim(),
                linkUrl = linkUrl.trim()
            ) ?: SavedDevice(
                deviceIp = ip.trim(),
                deviceName = customName.trim(),
                linkUrl = linkUrl.trim()
            )

            repository.saveDevice(deviceToSave)
            _sendState.value = SendState.Success("تم حفظ الجهاز: $customName")

            // Update in discovered list if present
            val updatedDiscovered = _discoveredDevices.value.map { dev ->
                if (dev.ip == ip) {
                    dev.copy(savedName = customName, savedLinkUrl = linkUrl)
                } else {
                    dev
                }
            }
            _discoveredDevices.value = updatedDiscovered
        }
    }

    fun deleteSavedDevice(device: SavedDevice) {
        viewModelScope.launch {
            repository.deleteDevice(device)
            _sendState.value = SendState.Success("تم حذف الجهاز")
        }
    }

    fun clearSendState() {
        _sendState.value = SendState.Idle
    }

    class Factory(private val application: Application) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(application) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
