package com.yourapp.hiddencam.scan

import android.Manifest
import android.bluetooth.BluetoothManager
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.bluetooth.le.ScanSettings
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

class AndroidBleScanner(
    private val context: Context,
) : BleScanner {
    override fun scan(): Flow<BleHit> = callbackFlow {
        val hasPermission = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.BLUETOOTH_SCAN) == PackageManager.PERMISSION_GRANTED
        } else {
            ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }

        if (!hasPermission) {
            close()
            return@callbackFlow
        }

        val bluetoothManager = context.getSystemService(BluetoothManager::class.java)
        val adapter = bluetoothManager?.adapter
        if (adapter == null || !adapter.isEnabled) {
            close()
            return@callbackFlow
        }

        val scanner = adapter.bluetoothLeScanner ?: run {
            close()
            return@callbackFlow
        }

        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .build()

        val callback = object : ScanCallback() {
            override fun onScanResult(callbackType: Int, result: ScanResult) {
                val device = result.device ?: return
                val record = result.scanRecord
                val manufacturerId = record?.manufacturerSpecificData?.keyAt(0)
                val hit = BleHit(
                    address = device.address ?: "unknown",
                    name = device.name ?: record?.deviceName,
                    rssi = result.rssi,
                    manufacturerId = manufacturerId,
                    timestamp = System.currentTimeMillis(),
                )
                trySend(hit)
            }
        }

        scanner.startScan(null, settings, callback)
        awaitClose {
            scanner.stopScan(callback)
        }
    }
}

class RssiSmoother(
    private val alpha: Double = 0.25,
) {
    private val recent = mutableMapOf<String, ArrayDeque<Int>>()
    private val previous = mutableMapOf<String, Double>()

    fun filterAndSmooth(address: String, rawRssi: Int): Pair<Int, Double> {
        val history = recent.getOrPut(address) { ArrayDeque() }
        history.addLast(rawRssi)
        if (history.size > 3) {
            history.removeFirst()
        }

        val filtered = medianOf3(history.toList())
        val prev = previous[address] ?: filtered.toDouble()
        val smoothed = (alpha * filtered) + ((1.0 - alpha) * prev)
        previous[address] = smoothed
        return filtered to smoothed
    }

    private fun medianOf3(values: List<Int>): Int {
        if (values.isEmpty()) return 0
        val sorted = values.sorted()
        return sorted[minOf(sorted.lastIndex, 1)]
    }
}
