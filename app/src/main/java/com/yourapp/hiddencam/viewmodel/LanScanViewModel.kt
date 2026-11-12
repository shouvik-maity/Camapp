package com.yourapp.hiddencam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourapp.hiddencam.scan.LanDevice
import com.yourapp.hiddencam.scan.LanScanner
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

data class LanUiState(
    val devices: List<LanDevice> = emptyList(),
    val running: Boolean = false,
    val message: String? = null,
)

class LanScanViewModel(
    private val lanScanner: LanScanner,
) : ViewModel() {
    private val _state = MutableStateFlow(LanUiState())
    val uiState: StateFlow<LanUiState> = _state.asStateFlow()
    private var job: Job? = null

    fun start() {
        if (job != null) return
        job = viewModelScope.launch {
            _state.value = _state.value.copy(running = true, message = "Scanning local subnet…")
            val devices = mutableListOf<LanDevice>()
            lanScanner.sweep()
                .catch { error ->
                    _state.value = _state.value.copy(running = false, message = error.message ?: "LAN scan failed")
                }
                .collectLatest { device ->
                    if (device.host == "network-isolated") {
                        _state.value = _state.value.copy(
                            devices = devices,
                            running = false,
                            message = "Network may isolate devices (hotel/guest Wi‑Fi)",
                        )
                        return@collectLatest
                    }
                    devices += device
                    _state.value = _state.value.copy(devices = devices, running = true)
                }
            _state.value = _state.value.copy(
                devices = devices,
                running = false,
                message = if (devices.isEmpty()) "No LAN devices responded." else null,
            )
            job = null
        }
    }
}
