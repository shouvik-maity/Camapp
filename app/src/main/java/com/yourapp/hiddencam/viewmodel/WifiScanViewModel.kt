package com.yourapp.hiddencam.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yourapp.hiddencam.scan.AndroidWifiScanner
import com.yourapp.hiddencam.scan.ApInfo
import com.yourapp.hiddencam.scan.ScanOutcome
import com.yourapp.hiddencam.scan.WifiScanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class WifiUiState(
    val aps: List<ApInfo> = emptyList(),
    val loading: Boolean = false,
    val message: String? = null,
)

class WifiScanViewModel(
    private val wifiScanner: WifiScanner,
) : ViewModel() {
    private val _uiState = MutableStateFlow(WifiUiState())
    val uiState: StateFlow<WifiUiState> = _uiState.asStateFlow()

    fun scan() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(loading = true, message = null)
            when (val outcome = wifiScanner.scan()) {
                is ScanOutcome.Success -> {
                    _uiState.value = _uiState.value.copy(
                        aps = outcome.data.sortedByDescending { ap ->
                            val weight = ((if (ap.flaggedByPattern) 25 else 0) +
                                (if (ap.flaggedByVendor) 15 else 0) +
                                (if (ap.hidden) 10 else 0))
                            weight + ap.rssi.coerceAtLeast(-100).coerceAtMost(0)
                        },
                        loading = false,
                        message = if (outcome.fromCache) "Using cached scan results; Wi‑Fi scan was throttled." else null,
                    )
                }
                is ScanOutcome.Unavailable -> {
                    _uiState.value = _uiState.value.copy(
                        loading = false,
                        message = outcome.reason,
                    )
                }
            }
        }
    }
}

class DefaultWifiScanViewModel(
    private val androidWifiScanner: AndroidWifiScanner,
) : ViewModel() {
    private val delegate = WifiScanViewModel(androidWifiScanner)
    val uiState: StateFlow<WifiUiState> get() = delegate.uiState
    fun scan() = delegate.scan()
}
