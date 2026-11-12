package com.yourapp.hiddencam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourapp.hiddencam.scan.BleHit
import com.yourapp.hiddencam.scan.BleScanner
import com.yourapp.hiddencam.scan.RssiSmoother
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private val cameraKeywords = listOf(
    "camera",
    "ipcam",
    "dvr",
    "nvr",
    "ezviz",
    "hikvision",
    "dahua",
    "reolink",
    "tapo",
    "yi",
)

data class BleDeviceUi(
    val address: String,
    val name: String,
    val rssi: Int,
    val smoothedRssi: Double,
    val heat: Float,
    val manufacturerId: Int?,
    val mayChangeAddress: Boolean = true,
)

data class BleUiState(
    val devices: List<BleDeviceUi> = emptyList(),
    val message: String? = null,
)

class BleHuntViewModel : ViewModel() {
    private val smoother = RssiSmoother()
    private val latest = linkedMapOf<String, BleDeviceUi>()
    private var scanJob: Job? = null

    private val _uiState = MutableStateFlow(BleUiState())
    val uiState: StateFlow<BleUiState> = _uiState.asStateFlow()

    fun startScan(scanner: BleScanner) {
        if (scanJob != null) return
        scanJob = viewModelScope.launch {
            scanner.scan().collectLatest { hit ->
                updateHit(hit)
            }
        }
    }

    fun stopScan() {
        scanJob?.cancel()
        scanJob = null
    }

    private fun updateHit(hit: BleHit) {
        val filtered = smoother.filterAndSmooth(hit.address, hit.rssi)
        val name = hit.name?.takeIf { it.isNotBlank() } ?: "Unnamed BLE device"
        val keywordMatch = cameraKeywords.any { keyword -> name.lowercase().contains(keyword) }
        val heat = ((filtered.second + 100.0) / 60.0).coerceIn(0.0, 1.0).toFloat()
        val next = BleDeviceUi(
            address = hit.address,
            name = name,
            rssi = filtered.first,
            smoothedRssi = filtered.second,
            heat = heat,
            manufacturerId = hit.manufacturerId,
            mayChangeAddress = true,
        )
        latest[hit.address] = next
        val prioritized = latest.values
            .sortedByDescending { it.heat }
        _uiState.value = BleUiState(
            devices = prioritized,
            message = if (keywordMatch) "Possible camera-like BLE signature detected." else "Scanning for nearby BLE adverts; names and IDs may rotate."
        )
    }
}
