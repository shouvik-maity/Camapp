package com.yourapp.hiddencam.scan

import com.yourapp.hiddencam.fusion.Finding
import kotlinx.coroutines.flow.Flow

interface WifiScanner {
    suspend fun scan(): ScanOutcome<List<ApInfo>>
}

interface BleScanner {
    fun scan(): Flow<BleHit>
}

interface LanScanner {
    fun sweep(): Flow<LanDevice>
}

interface CameraSweepScanner {
    fun sweep(): CameraSweepResult
}

interface RiskScorer {
    fun score(findings: List<Finding>): Pair<Int, com.yourapp.hiddencam.fusion.RiskLevel>
}

sealed interface ScanOutcome<out T> {
    data class Success<T>(val data: T, val fromCache: Boolean = false) : ScanOutcome<T>
    data class Unavailable(val reason: String) : ScanOutcome<Nothing>
}

data class ApInfo(
    val ssid: String,
    val bssid: String,
    val rssi: Int,
    val vendor: String? = null,
    val hidden: Boolean = false,
    val flaggedByPattern: Boolean = false,
    val flaggedByVendor: Boolean = false,
)

data class BleHit(
    val address: String,
    val name: String?,
    val rssi: Int,
    val manufacturerId: Int? = null,
    val timestamp: Long = 0L,
)

data class LanDevice(
    val host: String,
    val port: Int,
    val title: String? = null,
    val serverHeader: String? = null,
    val rtspBanner: String? = null,
    val onvif: Boolean = false,
    val score: Int = 0,
)
