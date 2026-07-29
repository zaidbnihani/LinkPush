package com.example.update

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

data class UpdateInfo(
    val newVersion: String,
    val releaseNotes: String,
    val downloadUrl: String
)

object UpdateChecker {

    private const val GITHUB_BASE_URL = "https://api.github.com/"

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(GITHUB_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    private val apiService: GitHubApiService by lazy {
        retrofit.create(GitHubApiService::class.java)
    }

    /**
     * Checks GitHub for a newer release asynchronously.
     * Returns UpdateInfo if a newer release exists, or null silently if up-to-date or on error.
     */
    suspend fun checkForUpdate(
        owner: String,
        repo: String,
        currentVersion: String
    ): UpdateInfo? = withContext(Dispatchers.IO) {
        runCatching {
            val release = apiService.getLatestRelease(owner, repo)
            val latestTag = release.tagName

            if (SemVer.isNewer(currentVersion = currentVersion, latestVersion = latestTag)) {
                // Find direct APK asset if available, otherwise fallback to release page URL
                val apkAsset = release.assets?.firstOrNull { asset ->
                    asset.name.endsWith(".apk", ignoreCase = true)
                }
                val downloadUrl = apkAsset?.browserDownloadUrl ?: release.htmlUrl

                UpdateInfo(
                    newVersion = latestTag,
                    releaseNotes = release.body.orEmpty().ifBlank { "لا توجد تفاصيل مرفقة لهذا الإصدار." },
                    downloadUrl = downloadUrl
                )
            } else {
                null
            }
        }.getOrNull()
    }
}
