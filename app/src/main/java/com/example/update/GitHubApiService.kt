package com.example.update

import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path

interface GitHubApiService {
    @GET("repos/{owner}/{repo}/releases/latest")
    suspend fun getLatestRelease(
        @Path("owner") owner: String,
        @Path("repo") repo: String,
        @Header("User-Agent") userAgent: String = "AndroidApp-UpdateChecker"
    ): GitHubRelease
}
