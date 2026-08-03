package com.example.update

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.Settings
import android.widget.Toast
import androidx.core.content.FileProvider
import java.io.File

object ApkDownloaderAndInstaller {

    fun checkInstallPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.packageManager.canRequestPackageInstalls()
        } else {
            true
        }
    }

    fun openInstallPermissionSettings(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            try {
                val intent = Intent(
                    Settings.ACTION_MANAGE_UNKNOWN_APP_SOURCES,
                    Uri.parse("package:${context.packageName}")
                ).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                context.startActivity(intent)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun downloadAndInstallApk(
        context: Context,
        downloadUrl: String,
        onStart: () -> Unit = {},
        onError: (String) -> Unit = {}
    ) {
        try {
            // Check permission before starting or warn user
            if (!checkInstallPermission(context)) {
                Toast.makeText(
                    context,
                    "يرجى السماح بتثبيت التطبيقات الخارجية لتحديث التطبيق",
                    Toast.LENGTH_LONG
                ).show()
                openInstallPermissionSettings(context)
            }

            val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as? DownloadManager
                ?: run {
                    onError("خدمة DownloadManager غير متوفرة")
                    return
                }

            val fileName = "LinkPush_update.apk"
            val file = File(context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS), fileName)
            if (file.exists()) {
                file.delete()
            }

            val request = DownloadManager.Request(Uri.parse(downloadUrl)).apply {
                setTitle("تحديث LinkPush")
                setDescription("جاري تنزيل التحديث...")
                setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                setDestinationInExternalFilesDir(context, Environment.DIRECTORY_DOWNLOADS, fileName)
                setMimeType("application/vnd.android.package-archive")
            }

            val downloadId = downloadManager.enqueue(request)
            onStart()

            val onCompleteReceiver = object : BroadcastReceiver() {
                override fun onReceive(c: Context, intent: Intent) {
                    val id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
                    if (id == downloadId) {
                        try {
                            context.applicationContext.unregisterReceiver(this)
                        } catch (_: Exception) {}

                        if (file.exists()) {
                            installApk(context, file)
                        } else {
                            onError("لم يتم العثور على ملف التحديث التلقائي")
                        }
                    }
                }
            }

            val filter = IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                context.applicationContext.registerReceiver(
                    onCompleteReceiver,
                    filter,
                    Context.RECEIVER_EXPORTED
                )
            } else {
                context.applicationContext.registerReceiver(
                    onCompleteReceiver,
                    filter
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
            onError(e.localizedMessage ?: "حدث خطأ أثناء تنزيل التحديث")
        }
    }

    fun installApk(context: Context, file: File) {
        try {
            if (!checkInstallPermission(context)) {
                Toast.makeText(
                    context,
                    "يرجى السماح بتثبيت التطبيقات من مصادر غير معروفة لإكمال التثبيت",
                    Toast.LENGTH_LONG
                ).show()
                openInstallPermissionSettings(context)
                return
            }

            val apkUri: Uri = FileProvider.getUriForFile(
                context,
                "${context.packageName}.fileprovider",
                file
            )

            val installIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(apkUri, "application/vnd.android.package-archive")
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

            context.startActivity(installIntent)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "فشل فتح شاشة التثبيت: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
        }
    }
}
