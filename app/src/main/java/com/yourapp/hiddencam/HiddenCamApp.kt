package com.yourapp.hiddencam

import android.net.wifi.WifiManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourapp.hiddencam.scan.AndroidCameraSweepScanner
import com.yourapp.hiddencam.scan.AndroidWifiScanner
import com.yourapp.hiddencam.ui.camera.CameraSweepScreen
import com.yourapp.hiddencam.ui.home.HomeScreen
import com.yourapp.hiddencam.ui.permissions.PermissionsScreen
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
    }
}
