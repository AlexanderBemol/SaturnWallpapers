package com.amontdevs.saturnwallpapers.android.ui.dialogs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.amontdevs.saturnwallpapers.android.SaturnTheme

@Composable
fun ConfirmDialogLoading(
    title: String = "Are you sure?",
    description: String = "This action cannot be undone.",
    onLoadingTitle: String = "Loading",
    onLoadingDescription: String = "Please wait till the action is completed...",
    confirmText: @Composable () -> Unit = {},
    dismissText: @Composable () -> Unit = {},
    onDismiss: () -> Unit = {},
    onConfirm: () -> Unit = {}
) {
    val isLoading = remember { mutableStateOf(false) }
    val iconSize = 32.dp
    AlertDialog(
        title = { Text(text = if(isLoading.value) onLoadingTitle else title) },
        text = { Text(text = if(isLoading.value) onLoadingDescription else description) },
        icon = {
            if(isLoading.value) {
                LinearProgressIndicator(
                    modifier = Modifier.fillMaxWidth()
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Warning,
                    contentDescription = "Warning",
                    modifier = Modifier.size(iconSize)
                )
            }
        },
        onDismissRequest = onDismiss,
        dismissButton = {
            TextButton(onClick = onDismiss) {
                dismissText()
            }
        },
        confirmButton = {
            if(!isLoading.value) {
                TextButton(onClick = {
                    isLoading.value = true
                    onConfirm()
                }) {
                    confirmText()
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview()
@Composable
fun ConfirmDialogPreview(){
    SaturnTheme(
        isDarkTheme = true
    ){
        ConfirmDialogLoading()
    }
}