package com.yourapp.hiddencam

import android.net.wifi.WifiManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourapp.hiddencam.fusion.Finding
import com.yourapp.hiddencam.fusion.ReportData
import com.yourapp.hiddencam.fusion.ScanCoverage
import com.yourapp.hiddencam.fusion.SignalType
import com.yourapp.hiddencam.scan.AndroidCameraSweepScanner
import com.yourapp.hiddencam.scan.AndroidWifiScanner
import com.yourapp.hiddencam.scan.DefaultRiskScorer
import com.yourapp.hiddencam.ui.camera.CameraSweepScreen
import com.yourapp.hiddencam.ui.home.HomeScreen
import com.yourapp.hiddencam.ui.permissions.PermissionsScreen
import com.yourapp.hiddencam.ui.report.ReportScreen
import com.yourapp.hiddencam.ui.screens.ScanPlaceholderScreen
import com.yourapp.hiddencam.ui.wifi.WifiScanScreen
import com.yourapp.hiddencam.viewmodel.CameraSweepViewModel
import com.yourapp.hiddencam.viewmodel.WifiScanViewModel

@Composable
fun HiddenCamApp() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "permissions") {
        composable("permissions") {
            PermissionsScreen(onContinue = { navController.navigate("home") })
        }
        composable("home") {
            HomeScreen(navController)
        }
        composable("wifi") {
            val context = LocalContext.current
            val wifiManager = remember(context) {
                context.getSystemService(WifiManager::class.java)
            }
            val viewModel = remember(wifiManager) {
                wifiManager?.let { WifiScanViewModel(AndroidWifiScanner(it)) }
            }
            if (viewModel != null) {
                WifiScanScreen(viewModel)
            } else {
                ScanPlaceholderScreen(
                    title = "Wi‑Fi scan",
                    subtitle = "Wi‑Fi is unavailable on this device or the required permissions are missing.",
                    navController = navController,
                )
            }
        }
        composable("bluetooth") {
            val context = LocalContext.current
            val viewModel = remember(context) { com.yourapp.hiddencam.viewmodel.BleHuntViewModel() }
            com.yourapp.hiddencam.ui.bluetooth.HuntScreen(viewModel)
        }
        composable("network") {
            val context = LocalContext.current
            val viewModel = remember(context) {
                com.yourapp.hiddencam.viewmodel.LanScanViewModel(com.yourapp.hiddencam.scan.AndroidLanScanner(context))
            }
            com.yourapp.hiddencam.ui.network.NetworkScanScreen(viewModel)
        }
        composable("camera") {
            val context = LocalContext.current
            val viewModel = remember(context) {
                CameraSweepViewModel(AndroidCameraSweepScanner(context))
            }
            CameraSweepScreen(viewModel)
        }
        composable("report") {
            val coverage = ScanCoverage(
                wifiChecked = true,
                bleChecked = true,
                lanChecked = true,
                glintChecked = true,
                irChecked = true,
            )
            val findings = listOf(
                Finding(
                    id = "wifi-report",
                    type = SignalType.SSID,
                    title = "Wi‑Fi scan",
                    detail = "Nearby camera-like SSIDs and vendor matches were checked.",
                    weight = 26,
                ),
                Finding(
                    id = "ble-report",
                    type = SignalType.BLE,
                    title = "BLE hunt",
                    detail = "Advertising beacons and nearby devices were checked for suspicious camera-like identifiers.",
                    weight = 28,
                ),
                Finding(
                    id = "lan-report",
                    type = SignalType.LAN_DEVICE,
                    title = "LAN sweep",
                    detail = "Local subnet and common camera ports were probed.",
                    weight = 24,
                ),
                Finding(
                    id = "camera-report",
                    type = SignalType.GLINT,
                    title = "Camera/torch check",
                    detail = "Camera sensor and flash availability were checked; actual lens reflection analysis remains limited to phone hardware.",
                    weight = 18,
                ),
            )
            val scorer = DefaultRiskScorer()
            val report = remember(findings) {
                val score = scorer.score(findings).first
                val level = scorer.score(findings).second
                ReportData(
                    score = score,
                    level = level,
                    findings = findings,
                    coverage = coverage,
                    timestamp = System.currentTimeMillis(),
                )
            }
            ReportScreen(report)
        }
    }
}
