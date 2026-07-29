package com.example.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
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
import com.example.network.DiscoveredDevice
import com.example.ui.MainViewModel
import com.example.ui.SendState
import com.example.ui.components.SaveDeviceDialog
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
        owner = "example-owner", // REPLACE with your GitHub username/owner
        repo = "example-repo",   // REPLACE with your GitHub repository name
        currentVersion = com.example.BuildConfig.VERSION_NAME
    )

    val isScanning by viewModel.isScanning.collectAsStateWithLifecycle()
    val scanProgress by viewModel.scanProgress.collectAsStateWithLifecycle()
    val discoveredDevices by viewModel.discoveredDevices.collectAsStateWithLifecycle()
    val savedDevices by viewModel.savedDevices.collectAsStateWithLifecycle()
    val sendState by viewModel.sendState.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Dialog for adding/editing link settings
    var showSaveDialog by remember { mutableStateOf(false) }
    var dialogInitialIp by remember { mutableStateOf("") }
    var dialogInitialName by remember { mutableStateOf("") }
    var dialogInitialUrl by remember { mutableStateOf("https://google.com") }

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
        floatingActionButton = {
            if (selectedTab == AppTab.SETTINGS) {
                FloatingActionButton(
                    onClick = {
                        dialogInitialIp = ""
                        dialogInitialName = ""
                        dialogInitialUrl = "https://google.com"
                        showSaveDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier.testTag("add_saved_device_fab")
                ) {
                    Icon(Icons.Default.Add, contentDescription = "إضافة")
                }
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
                        onDeviceClick = { device ->
                            selectedDeviceForPreset = device
                        }
                    )
                }
                AppTab.SETTINGS -> {
                    SettingsScreen(
                        savedDevices = savedDevices,
                        onEditDeviceClick = { dev ->
                            dialogInitialIp = dev.deviceIp
                            dialogInitialName = dev.deviceName
                            dialogInitialUrl = dev.linkUrl
                            showSaveDialog = true
                        },
                        onDeleteDeviceClick = { dev -> viewModel.deleteSavedDevice(dev) }
                    )
                }
            }
        }
    }

    // Edit / Save preset link dialog (Name and Link only)
    if (showSaveDialog) {
        SaveDeviceDialog(
            initialIp = dialogInitialIp,
            initialName = dialogInitialName,
            initialUrl = dialogInitialUrl,
            onDismiss = { showSaveDialog = false },
            onSave = { ip, name, url ->
                viewModel.saveDeviceMapping(ip, name, url)
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
                viewModel.sendLinkToDevice(targetIp = device.ip, customUrl = linkUrl)
            },
            onGoToSettings = {
                selectedTab = AppTab.SETTINGS
            }
        )
    }
}
