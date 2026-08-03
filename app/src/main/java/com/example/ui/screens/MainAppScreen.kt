package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.SavedDevice
import com.example.network.DiscoveredDevice
import com.example.ui.MainViewModel
import com.example.ui.SendState
import com.example.ui.components.EditDeviceNameDialog
import com.example.ui.components.SavePresetLinkDialog
import com.example.ui.components.SelectPresetLinkDialog
import com.example.update.UpdateCheckerEffect

enum class AppTab {
    DISCOVERED,
    SETTINGS
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainAppScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    var selectedTab by remember { mutableStateOf(AppTab.DISCOVERED) }

    // Automatic update check on app launch via GitHub API
    UpdateCheckerEffect(
        owner = "zaidbnihani",
        repo = "LinkPush",
        currentVersion = com.example.BuildConfig.VERSION_NAME
    )

    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val scanProgress by viewModel.scanProgress.collectAsStateWithLifecycle()
    val discoveredDevices by viewModel.discoveredDevices.collectAsStateWithLifecycle()
    val savedDevices by viewModel.savedDevices.collectAsStateWithLifecycle()
    val sendState by viewModel.sendState.collectAsStateWithLifecycle()
    val isBatteryOptimizationIgnored by viewModel.isBatteryOptimizationIgnored.collectAsStateWithLifecycle()
    val isOverlayPermissionGranted by viewModel.isOverlayPermissionGranted.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog state for adding/editing a saved link in Settings
    var showAddLinkDialog by remember { mutableStateOf(false) }
    var linkToEdit by remember { mutableStateOf<SavedDevice?>(null) }

    // Dialog state for editing device custom name on Home Screen
    var deviceToEditName by remember { mutableStateOf<DiscoveredDevice?>(null) }

    // Dialog for selecting a preset link to send to a discovered device
    var selectedDeviceForPreset by remember { mutableStateOf<DiscoveredDevice?>(null) }

    // React to send state changes
    LaunchedEffect(sendState) {
        when (val state = sendState) {
            is SendState.Success -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.clearSendState()
            }
            is SendState.Error -> {
                snackbarHostState.showSnackbar(state.message)
                viewModel.clearSendState()
            }
            else -> {}
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "LinkPush",
                        fontWeight = FontWeight.Bold
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == AppTab.DISCOVERED,
                    onClick = { selectedTab = AppTab.DISCOVERED },
                    icon = { Icon(Icons.Default.Devices, contentDescription = null) },
                    label = { Text("الأجهزة المكتشفة") },
                    modifier = Modifier.testTag("tab_discovered")
                )
                NavigationBarItem(
                    selected = selectedTab == AppTab.SETTINGS,
                    onClick = { selectedTab = AppTab.SETTINGS },
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    label = { Text("الإعدادات") },
                    modifier = Modifier.testTag("tab_settings")
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (selectedTab) {
                AppTab.DISCOVERED -> {
                    HomeScreen(
                        isScanning = isScanning,
                        scanProgress = scanProgress,
                        discoveredDevices = discoveredDevices,
                        sendState = sendState,
                        isBatteryOptimizationIgnored = isBatteryOptimizationIgnored,
                        onFixBatteryOptimization = {
                            viewModel.requestDisableBatteryOptimization()
                        },
                        isOverlayPermissionGranted = isOverlayPermissionGranted,
                        onFixOverlayPermission = {
                            viewModel.requestOverlayPermission()
                        },
                        onDeviceClick = { device ->
                            selectedDeviceForPreset = device
                        },
                        onDeviceLongClick = { device ->
                            deviceToEditName = device
                        },
                        onRescanClick = {
                            viewModel.scanNetwork()
                        },
                        onManualIpClick = { ip ->
                            selectedDeviceForPreset = DiscoveredDevice(
                                ip = ip,
                                isLocalDevice = false,
                                responseTimeMs = 0
                            )
                        }
                    )
                }
                AppTab.SETTINGS -> {
                    SettingsScreen(
                        savedDevices = savedDevices,
                        onAddLinkClick = { showAddLinkDialog = true },
                        onEditDeviceClick = { savedLink ->
                            linkToEdit = savedLink
                        },
                        onDeleteDeviceClick = { savedLink -> viewModel.deleteSavedDevice(savedLink) }
                    )
                }
            }
        }
    }

    // Dialog for adding a new link in Settings
    if (showAddLinkDialog) {
        SavePresetLinkDialog(
            initialId = 0,
            initialName = "",
            initialUrl = "",
            onDismiss = { showAddLinkDialog = false },
            onSave = { id, name, url ->
                viewModel.savePresetLink(id, name, url)
            }
        )
    }

    // Dialog for editing an existing link in Settings
    linkToEdit?.let { savedLink ->
        SavePresetLinkDialog(
            initialId = savedLink.id,
            initialName = savedLink.deviceName,
            initialUrl = savedLink.linkUrl,
            onDismiss = { linkToEdit = null },
            onSave = { id, name, url ->
                viewModel.savePresetLink(id, name, url)
                linkToEdit = null
            }
        )
    }

    // Dialog for editing device custom name on Home Screen
    deviceToEditName?.let { device ->
        EditDeviceNameDialog(
            ip = device.ip,
            initialName = device.savedName ?: "",
            onDismiss = { deviceToEditName = null },
            onSave = { customName ->
                viewModel.saveDeviceCustomName(device.ip, customName)
                deviceToEditName = null
            }
        )
    }

    // Select preset link dialog when user taps on a discovered device
    selectedDeviceForPreset?.let { device ->
        SelectPresetLinkDialog(
            device = device,
            savedDevices = savedDevices,
            onDismiss = { selectedDeviceForPreset = null },
            onSelectPreset = { linkUrl, presetName ->
                viewModel.sendLinkToDevice(targetIp = device.ip, customUrl = linkUrl, port = device.port)
            },
            onGoToSettings = {
                selectedTab = AppTab.SETTINGS
            }
        )
    }
}
