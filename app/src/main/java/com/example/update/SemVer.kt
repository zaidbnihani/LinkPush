package com.example.update

data class SemVer(
    val major: Int = 0,
    val minor: Int = 0,
    val patch: Int = 0
) : Comparable<SemVer> {

    override fun compareTo(other: SemVer): Int {
        if (this.major != other.major) return this.major.compareTo(other.major)
        if (this.minor != other.minor) return this.minor.compareTo(other.minor)
        return this.patch.compareTo(other.patch)
    }

    companion object {
        fun parse(versionString: String): SemVer {
            // Clean version string: remove 'v' / 'V' prefix and pre-release/build metadata
            val clean = versionString
                .trim()
                .removePrefix("v")
                .removePrefix("V")
                .split("-")[0]
                .split("+")[0]

            val parts = clean.split(".")
            val major = parts.getOrNull(0)?.toIntOrNull() ?: 0
            val minor = parts.getOrNull(1)?.toIntOrNull() ?: 0
            val patch = parts.getOrNull(2)?.toIntOrNull() ?: 0

            return SemVer(major, minor, patch)
        }

        /**
         * Returns true if [latestVersion] is strictly newer than [currentVersion]
         * using Semantic Versioning rules (major.minor.patch).
         */
        fun isNewer(currentVersion: String, latestVersion: String): Boolean {
            val current = parse(currentVersion)
            val latest = parse(latestVersion)
            return latest > current
        }
    }
}
