package com.yourapp.hiddencam.scan

import android.annotation.SuppressLint
import android.net.wifi.ScanResult
import android.net.wifi.WifiManager
import android.os.Build
import com.yourapp.hiddencam.data.OuiDatabase
import com.yourapp.hiddencam.data.Signatures
import com.yourapp.hiddencam.fusion.Finding
import com.yourapp.hiddencam.fusion.SignalType

class AndroidWifiScanner(
    private val wifiManager: WifiManager,
    private val cachedResults: List<ApInfo> = emptyList(),
) : WifiScanner {
    override suspend fun scan(): ScanOutcome<List<ApInfo>> {
        return try {
            val started = wifiManager.startScan()
            val current = wifiManager.scanResults
            if (!started || current.isNullOrEmpty()) {
                if (cachedResults.isNotEmpty()) {
                    ScanOutcome.Success(cachedResults, fromCache = true)
                } else {
                    ScanOutcome.Unavailable("Wi‑Fi scan throttled or unavailable")
                }
            } else {
                ScanOutcome.Success(current.map { it.toApInfo() })
            }
        } catch (_: SecurityException) {
            if (cachedResults.isNotEmpty()) {
                ScanOutcome.Success(cachedResults, fromCache = true)
            } else {
                ScanOutcome.Unavailable("Missing Wi‑Fi permission")
            }
        } catch (_: Exception) {
            if (cachedResults.isNotEmpty()) {
                ScanOutcome.Success(cachedResults, fromCache = true)
            } else {
                ScanOutcome.Unavailable("Wi‑Fi scan failed")
            }
        }
    }

    fun findingsFrom(result: List<ApInfo>): List<Finding> = result.mapNotNull { ap ->
        val patternWeight = Signatures.patternWeight(ap.ssid)
        val vendor = OuiDatabase.lookupVendor(ap.bssid)
        val hidden = ap.ssid.isBlank() || ap.ssid == "<unknown ssid>"
        val weight = minOf(40, patternWeight + if (vendor != null) 15 else 0 + if (hidden) 10 else 0)
        if (weight <= 0) return@mapNotNull null
        Finding(
            id = "wifi-${ap.bssid}-${ap.ssid.ifBlank { "hidden" }}",
            type = SignalType.SSID,
            title = "SSID '${ap.ssid.ifBlank { "hidden" }}' looks like a camera hotspot",
            detail = buildString {
                append("Signal weight ${weight} based on camera-like SSID name")
                if (vendor != null) append(", vendor $vendor")
                if (hidden) append(", hidden SSID")
            },
            weight = weight,
            rssi = ap.rssi,
        )
    }

    @SuppressLint("MissingPermission")
    private fun ScanResult.toApInfo(): ApInfo {
        val ssid = when {
            Build.VERSION.SDK_INT >= 33 -> wifiSsid?.toString() ?: SSID ?: ""
            else -> SSID ?: ""
        }
        return ApInfo(
            ssid = ssid,
            bssid = BSSID ?: "",
            rssi = level,
            vendor = OuiDatabase.lookupVendor(BSSID),
            hidden = ssid.isBlank() || ssid == "<unknown ssid>",
            flaggedByPattern = Signatures.matchesCameraPattern(ssid),
            flaggedByVendor = OuiDatabase.lookupVendor(BSSID) != null,
        )
    }
}
