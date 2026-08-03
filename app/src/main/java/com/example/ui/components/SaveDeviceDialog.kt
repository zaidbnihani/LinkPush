package com.example.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun EditDeviceNameDialog(
    ip: String,
    initialName: String = "",
    onDismiss: () -> Unit,
    onSave: (customName: String) -> Unit
) {
    var deviceName by remember { mutableStateOf(initialName) }
    var nameError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "تعديل اسم الجهاز",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = "عنوان IP: $ip",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = deviceName,
                    onValueChange = {
                        deviceName = it
                        nameError = false
                    },
                    label = { Text("اسم الجهاز") },
                    placeholder = { Text("مثال: هاتف أحمد، شاشة الصالة") },
                    isError = nameError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (nameError) {
                    Text(
                        text = "يرجى إدخال اسم للجهاز",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (deviceName.isBlank()) {
                        nameError = true
                        return@TextButton
                    }
                    onSave(deviceName.trim())
                    onDismiss()
                }
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}

@Composable
fun SavePresetLinkDialog(
    initialId: Long = 0,
    initialName: String = "",
    initialUrl: String = "",
    onDismiss: () -> Unit,
    onSave: (id: Long, name: String, url: String) -> Unit
) {
    var linkName by remember { mutableStateOf(initialName) }
    var linkUrl by remember { mutableStateOf(initialUrl) }

    var nameError by remember { mutableStateOf(false) }
    var urlError by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = if (initialId == 0L) "إضافة رابط جديد" else "تعديل الرابط المسجّل",
                style = MaterialTheme.typography.titleLarge
            )
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = linkName,
                    onValueChange = {
                        linkName = it
                        nameError = false
                    },
                    label = { Text("اسم الرابط / العنوان") },
                    placeholder = { Text("مثال: موقعي المفضّل، فيسبوك") },
                    isError = nameError,
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                if (nameError) {
                    Text(
                        text = "يرجى إدخال اسم للرابط",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = linkUrl,
                    onValueChange = {
                        linkUrl = it
                        urlError = false
                    },
                    label = { Text("الرابط (URL)") },
                    placeholder = { Text("https://example.com") },
                    isError = urlError,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    modifier = Modifier.fillMaxWidth()
                )
                if (urlError) {
                    Text(
                        text = "يرجى إدخال الرابط",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (linkName.isBlank()) {
                        nameError = true
                        return@TextButton
                    }
                    if (linkUrl.isBlank()) {
                        urlError = true
                        return@TextButton
                    }
                    onSave(initialId, linkName.trim(), linkUrl.trim())
                    onDismiss()
                }
            ) {
                Text("حفظ")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("إلغاء")
            }
        }
    )
}
