package com.yourapp.hiddencam.ui.permissions

import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.yourapp.hiddencam.util.PermissionHelper
import com.yourapp.hiddencam.util.PermissionMeta

@Composable
fun PermissionsScreen(
    onContinue: () -> Unit,
) {
    val context = LocalContext.current
    val permissions = remember { PermissionHelper.requiredPermissions(Build.VERSION.SDK_INT) }
    var grantedMap by remember { mutableStateOf(permissions.associate { it.permission to PermissionHelper.isGranted(context, it.permission) }) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions(),
        onResult = { result ->
            val next = grantedMap.toMutableMap()
            result.forEach { (permission, isGranted) -> next[permission] = isGranted }
            grantedMap = next
        }
    )

    val locationEnabled = PermissionHelper.isLocationEnabled(context)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "Permissions needed for a reliable room scan",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "This app only estimates risk locally with phone sensors. It does not upload scan data."
        )

        permissions.forEach { permission ->
            PermissionCard(
                meta = permission,
                granted = grantedMap[permission.permission] == true,
            )
        }

        Card(
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(
                    text = "Location is required for radio validation",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = if (locationEnabled) {
                        "Location services are enabled on this device."
                    } else {
                        "Location services are off. Open settings to continue."
                    }
                )
                if (!locationEnabled) {
                    Button(
                        onClick = { PermissionHelper.openLocationSettings(context) },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text("Open location settings")
                    }
                }
            }
        }

        Button(
            onClick = {
                val requestList = permissions.mapNotNull { meta ->
                    if (!PermissionHelper.isGranted(context, meta.permission)) meta.permission else null
                }.toTypedArray()
                if (requestList.isNotEmpty()) {
                    launcher.launch(requestList)
                }
            },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Request permissions")
        }

        Button(
            onClick = onContinue,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text("Continue to home")
        }
    }
}

@Composable
private fun PermissionCard(meta: PermissionMeta, granted: Boolean) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (granted) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                text = meta.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold,
            )
            Text(text = meta.rationale)
            Text(
                text = if (granted) {
                    "Status: granted"
                } else {
                    "Status: not granted"
                },
                style = MaterialTheme.typography.bodyMedium,
                color = if (granted) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Text(text = "If denied: ${meta.ifDenied}")
            if (!granted) {
                Icon(
                    imageVector = Icons.Outlined.WarningAmber,
                    contentDescription = "Permission not granted",
                )
            }
        }
    }
}
