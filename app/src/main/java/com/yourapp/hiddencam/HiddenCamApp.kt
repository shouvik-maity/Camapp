package com.yourapp.hiddencam

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.yourapp.hiddencam.ui.home.HomeScreen
import com.yourapp.hiddencam.ui.permissions.PermissionsScreen
import com.yourapp.hiddencam.ui.screens.ScanPlaceholderScreen

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
            ScanPlaceholderScreen(
                title = "Wi‑Fi scan",
                subtitle = "Enumerating nearby SSIDs and camera-like network signatures.",
                navController = navController,
            )
        }
        composable("bluetooth") {
            ScanPlaceholderScreen(
                title = "Bluetooth hunt",
                subtitle = "Scanning for BLE beacons, names, and manufacturer patterns common to hidden camera devices.",
                navController = navController,
            )
        }
        composable("network") {
            ScanPlaceholderScreen(
                title = "Network sweep",
                subtitle = "Inspecting LAN devices, ports, and suspicious HTTP/RTSP endpoints.",
                navController = navController,
            )
        }
        composable("camera") {
            ScanPlaceholderScreen(
                title = "Camera sweep",
                subtitle = "Testing the camera and torch path for glints and IR reflections.",
                navController = navController,
            )
        }
    }
}
