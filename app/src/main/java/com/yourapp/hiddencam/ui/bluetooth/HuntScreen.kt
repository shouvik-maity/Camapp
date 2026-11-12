package com.yourapp.hiddencam.ui.bluetooth

import android.Manifest
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.yourapp.hiddencam.scan.AndroidBleScanner
import com.yourapp.hiddencam.viewmodel.BleDeviceUi
import com.yourapp.hiddencam.viewmodel.BleHuntViewModel

@Composable
fun HuntScreen(viewModel: BleHuntViewModel) {
    val state by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    LaunchedEffect(context) {
        val blePermissionOk = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        val adapter = bluetoothManager?.adapter
        if (blePermissionOk && adapter != null && adapter.isEnabled) {
            viewModel.startScan(AndroidBleScanner(context))
        } else {
            viewModel.stopScan()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "BLE hunt",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = "Names can rotate, so use name + manufacturer ID + service hints together."
        )

        val message = state.message
        if (message != null) {
            Text(text = message)
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            items(state.devices) { device ->
                BleDeviceCard(device = device)
            }
        }
    }
}

@Composable
private fun BleDeviceCard(device: BleDeviceUi) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = device.name, fontWeight = FontWeight.Bold)
                Text(text = "${device.rssi} dBm")
            }
            Text(text = "Address: ${device.address}")
            Text(text = "Smoothed: ${"%.1f".format(device.smoothedRssi)} dBm")
            Text(text = "Heat: ${"%.2f".format(device.heat)}")
            if (device.manufacturerId != null) {
                Text(text = "Manufacturer ID: ${device.manufacturerId}")
            }
            Text(text = if (device.mayChangeAddress) "May change address" else "Stable address")
        }
    }
}
