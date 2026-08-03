package com.example.privacy

import android.content.Context
import android.content.SharedPreferences
import com.example.BuildConfig

class PrivacyConsentManager(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("app_privacy_prefs", Context.MODE_PRIVATE)

    companion object {
        private const val KEY_PRIVACY_GIVEN = "privacy_consent_given"
        private const val KEY_PRIVACY_VERSION = "privacy_consent_version"
    }

    /**
     * Checks whether privacy consent needs to be granted or re-granted.
     * Returns true if consent is REQUIRED (either not given yet or given for an older version code).
     */
    fun isConsentRequired(): Boolean {
        val consentGiven = prefs.getBoolean(KEY_PRIVACY_GIVEN, false)
        val consentVersion = prefs.getInt(KEY_PRIVACY_VERSION, -1)
        val currentVersion = BuildConfig.VERSION_CODE

        return !consentGiven || consentVersion < currentVersion
    }

    /**
     * Saves that privacy consent has been granted for the current VERSION_CODE.
     */
    fun saveConsent() {
        prefs.edit()
            .putBoolean(KEY_PRIVACY_GIVEN, true)
            .putInt(KEY_PRIVACY_VERSION, BuildConfig.VERSION_CODE)
            .apply()
    }
}
