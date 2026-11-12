package com.yourapp.hiddencam.util

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.LocationManager
import android.provider.Settings
import androidx.core.content.ContextCompat

private const val PERMISSION_DENIED_MESSAGE = "Permission denied; sensor cannot be used reliably."

data class PermissionMeta(
    val permission: String,
    val title: String,
    val rationale: String,
    val ifDenied: String,
)

object PermissionHelper {
    fun requiredPermissions(apiLevel: Int): List<PermissionMeta> = buildList {
        add(
            PermissionMeta(
                permission = android.Manifest.permission.CAMERA,
                title = "Camera sweep",
                rationale = "Needed to look for glints or IR reflections that may indicate a hidden lens.",
                ifDenied = "The camera sweep stays off and can miss reflective lenses or IR LEDs.",
            )
        )
        add(
            PermissionMeta(
                permission = android.Manifest.permission.ACCESS_FINE_LOCATION,
                title = "Location and Wi‑Fi context",
                rationale = "This is used to understand your network and enable radio checks for nearby devices.",
                ifDenied = "Local network and BLE scanning are limited or unavailable.",
            )
        )
        add(
            PermissionMeta(
                permission = android.Manifest.permission.ACCESS_WIFI_STATE,
                title = "Wi‑Fi state",
                rationale = "Read current Wi‑Fi details and scan nearby access points.",
                ifDenied = "The app cannot inspect nearby SSIDs or cached network information.",
            )
        )
        add(
            PermissionMeta(
                permission = android.Manifest.permission.CHANGE_WIFI_STATE,
                title = "Wi‑Fi radio control",
                rationale = "Helps trigger scans and refresh access points for a faster room survey.",
                ifDenied = "Current Wi‑Fi scan results may be stale and throttled.",
            )
        )
        add(
            PermissionMeta(
                permission = android.Manifest.permission.INTERNET,
                title = "LAN probe access",
                rationale = "Required for network discovery checks that inspect local devices on your Wi‑Fi network.",
                ifDenied = "The LAN sweep cannot confirm any HTTP, RTSP, or ONVIF endpoints.",
            )
        )
        if (apiLevel >= 31) {
            add(
                PermissionMeta(
                    permission = android.Manifest.permission.BLUETOOTH_SCAN,
                    title = "Bluetooth scan",
                    rationale = "Looks for Bluetooth beacons and nearby advertising devices that may be hidden cameras.",
                    ifDenied = "Nearby BLE devices cannot be evaluated for suspicious behavior.",
                )
            )
        }
        if (apiLevel >= 33) {
            add(
                PermissionMeta(
                    permission = android.Manifest.permission.NEARBY_WIFI_DEVICES,
                    title = "Nearby Wi‑Fi devices",
                    rationale = "Lets the app gather precise Wi‑Fi and network details needed for a local room scan.",
                    ifDenied = "Nearby device detection and SSID checks are incomplete.",
                )
            )
        }
    }

    fun isGranted(context: Context, permission: String): Boolean =
        ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED

    fun isLocationEnabled(context: Context): Boolean {
        val locationManager = context.getSystemService(LocationManager::class.java)
        return if (locationManager is LocationManager) {
            locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } else {
            false
        }
    }

    fun openLocationSettings(context: Context) {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        context.startActivity(intent)
    }

    fun runtimeStatus(context: Context, apiLevel: Int): Map<String, Boolean> =
        requiredPermissions(apiLevel).associate { it.permission to isGranted(context, it.permission) }

    fun requireMessageFor(permission: String): String = PERMISSION_DENIED_MESSAGE
}
