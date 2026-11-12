package com.yourapp.hiddencam.ui.network

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
import com.yourapp.hiddencam.scan.LanDevice
import com.yourapp.hiddencam.viewmodel.LanScanViewModel

@Composable
fun NetworkScanScreen(viewModel: LanScanViewModel) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.start()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Network sweep",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Text(text = "Connected network devices, ports, and suspicious camera endpoints.")
        val message = state.message
        if (message != null) {
            Text(text = message)
        }
        Button(
            onClick = { viewModel.start() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.running,
        ) {
            Text(if (state.running) "Scanning…" else "Scan again")
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.devices) { device ->
                LanDeviceCard(device)
            }
        }
    }
}

@Composable
private fun LanDeviceCard(device: LanDevice) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Text(text = device.host, fontWeight = FontWeight.Bold)
                Text(text = "score ${device.score}")
            }
            if (device.port != 0) {
                Text(text = "Port: ${device.port}")
            }
            if (device.title != null) {
                Text(text = "Title: ${device.title}")
            }
            if (device.serverHeader != null) {
                Text(text = "Server: ${device.serverHeader}")
            }
            if (device.rtspBanner != null) {
                Text(text = "RTSP banner: ${device.rtspBanner}")
            }
            if (device.onvif) {
                Text(text = "ONVIF response detected")
            }
        }
    }
}
