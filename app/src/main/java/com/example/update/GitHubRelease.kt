package com.example.update

import com.google.gson.annotations.SerializedName

data class GitHubRelease(
    @SerializedName("tag_name")
    val tagName: String,
    @SerializedName("name")
    val name: String? = null,
    @SerializedName("body")
    val body: String? = null,
    @SerializedName("html_url")
    val htmlUrl: String,
    @SerializedName("assets")
    val assets: List<GitHubAsset>? = emptyList()
)

data class GitHubAsset(
    @SerializedName("name")
    val name: String,
    @SerializedName("browser_download_url")
    val browserDownloadUrl: String
)
