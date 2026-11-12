package com.yourapp.hiddencam.viewmodel

import androidx.lifecycle.ViewModel
import com.yourapp.hiddencam.fusion.Finding
import com.yourapp.hiddencam.scan.AndroidCameraSweepScanner
import com.yourapp.hiddencam.scan.CameraSweepResult
import com.yourapp.hiddencam.scan.CameraSweepScanner
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class CameraUiState(
    val checked: Boolean = false,
    val flashAvailable: Boolean = false,
    val cameraAvailable: Boolean = false,
    val glintHint: String = "Camera sweep is not ready yet.",
    val irHint: String = "IR sweep is not ready yet.",
    val findings: List<Finding> = emptyList(),
    val running: Boolean = false,
)

class CameraSweepViewModel(
    private val cameraSweepScanner: CameraSweepScanner,
) : ViewModel() {
    private val _uiState = MutableStateFlow(CameraUiState())
    val uiState: StateFlow<CameraUiState> = _uiState.asStateFlow()

    fun runSweep() {
        _uiState.value = _uiState.value.copy(running = true)

        val result: CameraSweepResult = cameraSweepScanner.sweep()
        _uiState.value = CameraUiState(
            checked = result.checked,
            flashAvailable = result.flashAvailable,
            cameraAvailable = result.cameraAvailable,
            glintHint = result.glintHint,
            irHint = result.irHint,
            findings = result.findings,
            running = false,
        )
    }
}

class DefaultCameraSweepViewModel(
    private val androidCameraSweepScanner: AndroidCameraSweepScanner,
) : ViewModel() {
    private val delegate = CameraSweepViewModel(androidCameraSweepScanner)
    val uiState: StateFlow<CameraUiState> get() = delegate.uiState
    fun runSweep() = delegate.runSweep()
}
