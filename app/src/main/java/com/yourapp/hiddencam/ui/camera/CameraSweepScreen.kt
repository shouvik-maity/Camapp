package com.yourapp.hiddencam.ui.camera

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
import com.yourapp.hiddencam.viewmodel.CameraSweepViewModel

@Composable
fun CameraSweepScreen(viewModel: CameraSweepViewModel) {
    val state by viewModel.uiState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.runSweep()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Camera sweep",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Text(
            text = if (state.checked) "Glint/IR check is available on this device." else "This device cannot reliably perform a glint/IR sweep.",
        )

        Button(
            onClick = { viewModel.runSweep() },
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(if (state.running) "Scanning…" else "Run sweep")
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Text(text = "Hardware status", fontWeight = FontWeight.SemiBold)
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Camera sensor")
                    Text(if (state.cameraAvailable) "Available" else "Not detected")
                }
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Torch/flash")
                    Text(if (state.flashAvailable) "Available" else "Not detected")
                }
                Text(text = state.glintHint)
                Text(text = state.irHint)
            }
        }

        Text(
            text = "Checked: camera sensor and torch availability. Not checked / cannot be detected by phone: offline SD-card cameras, wired cameras on other segments, or lenses hidden behind normal walls.",
            style = MaterialTheme.typography.bodySmall,
        )

        Text(
            text = "Disclaimer: This application estimates hidden-camera risk and never claims that a room is safe. Risk output is LOW, MEDIUM, or HIGH.",
            style = MaterialTheme.typography.bodySmall,
        )

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(state.findings) { finding ->
                Card(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(text = finding.title, fontWeight = FontWeight.SemiBold)
                        Text(text = finding.detail)
                        Text(text = "Weight: ${finding.weight}")
                    }
                }
            }
        }
    }
}
