package com.yourapp.hiddencam.fusion

enum class RiskLevel {
    LOW,
    MEDIUM,
    HIGH
}

enum class SignalType {
    SSID,
    LAN_DEVICE,
    BLE,
    GLINT,
    IR
}

data class Finding(
    val id: String,
    val type: SignalType,
    val title: String,
    val detail: String,
    val weight: Int,
    val rssi: Int? = null,
    val photoPath: String? = null,
    val dismissed: Boolean = false
)

data class ScanCoverage(
    val wifiChecked: Boolean,
    val bleChecked: Boolean,
    val lanChecked: Boolean,
    val glintChecked: Boolean,
    val irChecked: Boolean,
)

data class ReportData(
    val score: Int,
    val level: RiskLevel,
    val findings: List<Finding>,
    val coverage: ScanCoverage,
    val timestamp: Long,
)
