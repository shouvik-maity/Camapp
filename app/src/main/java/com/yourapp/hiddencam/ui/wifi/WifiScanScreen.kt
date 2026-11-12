package com.yourapp.hiddencam.ui.wifi

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yourapp.hiddencam.data.OuiDatabase
import com.yourapp.hiddencam.data.Signatures
import com.yourapp.hiddencam.scan.ApInfo
import com.yourapp.hiddencam.viewmodel.WifiScanViewModel

@Composable
fun WifiScanScreen(viewModel: WifiScanViewModel) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.scan()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Wi‑Fi scan",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        val message = state.message
        if (message != null) {
            Text(text = message)
        }

        Button(onClick = { viewModel.scan() }, modifier = Modifier.fillMaxWidth()) {
            Text(if (state.loading) "Scanning…" else "Refresh")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            items(state.aps) { ap ->
                WifiAccessPointRow(ap = ap)
            }
        }
    }
}

@Composable
private fun WifiAccessPointRow(ap: ApInfo) {
    val signalWeight = (if (ap.flaggedByPattern) 25 else 0) +
        (if (ap.flaggedByVendor) 15 else 0) +
        (if (ap.hidden) 10 else 0)
    val vendor = ap.vendor ?: OuiDatabase.lookupVendor(ap.bssid)
    val badge = if (ap.flaggedByPattern || ap.flaggedByVendor || ap.hidden) "Flagged" else "Normal"

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text(text = ap.ssid.ifBlank { "Hidden SSID" }, fontWeight = FontWeight.Bold)
                Text(text = "$signalWeight pts")
            }
            Text(text = "BSSID: ${ap.bssid.ifBlank { "unknown" }}")
            Text(text = "Signal: ${ap.rssi} dBm")
            if (vendor != null) {
                Text(text = "Vendor: $vendor")
            }
            if (ap.flaggedByPattern) {
                Text(text = "Pattern match: ${Signatures.defaultPatterns().firstOrNull { p -> ap.ssid.contains(Regex(p, RegexOption.IGNORE_CASE)) } ?: "camera-like"}")
            }
            Text(text = "Badge: $badge")
            if (ap.hidden) {
                Text(text = "Hidden SSID")
            }
        }
    }
}
