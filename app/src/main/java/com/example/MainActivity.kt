package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import com.example.privacy.PrivacyConsentGate
import com.example.privacy.PrivacyConsentManager
import com.example.ui.MainViewModel
import com.example.ui.screens.MainAppScreen
import com.example.ui.theme.LinkPushTheme
import com.example.utils.BatteryOptimizationUtils

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels {
        MainViewModel.Factory(application)
    }

    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { _ ->
        checkBatteryOptimizationPrompt()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        checkPermissions()
        checkBatteryOptimizationPrompt()

        val privacyConsentManager = PrivacyConsentManager(applicationContext)

        setContent {
            LinkPushTheme {
                PrivacyConsentGate(
                    privacyConsentManager = privacyConsentManager,
                    onConsentGranted = { }
                )
                MainAppScreen(viewModel = viewModel)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.checkBatteryOptimization()
    }

    private fun checkBatteryOptimizationPrompt() {
        if (!BatteryOptimizationUtils.isIgnoringBatteryOptimizations(this)) {
            val prefs = getSharedPreferences("battery_prefs", MODE_PRIVATE)
            val hasPrompted = prefs.getBoolean("battery_prompted", false)
            if (!hasPrompted) {
                prefs.edit().putBoolean("battery_prompted", true).apply()
                BatteryOptimizationUtils.requestIgnoreBatteryOptimizations(this)
            }
        }
        if (!BatteryOptimizationUtils.isOverlayPermissionGranted(this)) {
            val prefs = getSharedPreferences("battery_prefs", MODE_PRIVATE)
            val hasPromptedOverlay = prefs.getBoolean("overlay_prompted", false)
            if (!hasPromptedOverlay) {
                prefs.edit().putBoolean("overlay_prompted", true).apply()
                BatteryOptimizationUtils.requestOverlayPermission(this)
            }
        }
    }

    private fun checkPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
            }
        }
    }
}
