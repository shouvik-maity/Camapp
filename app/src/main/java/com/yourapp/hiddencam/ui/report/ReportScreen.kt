package com.yourapp.hiddencam.ui.report

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.yourapp.hiddencam.fusion.ReportData

@Composable
fun ReportScreen(report: ReportData) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Text(
            text = "Final risk report",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(text = "Risk score: ${report.score}/100")
                Text(text = "Level: ${report.level}")
                Text(
                    text = "Checked: Wi‑Fi, BLE, LAN, camera/torch. Not checked / cannot be detected by phone: offline SD-card cameras, cellular devices, wired cameras on other segments.",
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = "Disclaimer: This application estimates hidden-camera risk and never claims that a room is safe. Risk output is LOW, MEDIUM, or HIGH.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(report.findings) { finding ->
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
