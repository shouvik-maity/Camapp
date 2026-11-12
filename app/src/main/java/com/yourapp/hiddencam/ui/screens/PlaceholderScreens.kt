package com.yourapp.hiddencam.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ScanPlaceholderScreen(
    title: String,
    subtitle: String,
    navController: NavController,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
        )
        Card(
            modifier = Modifier.padding(vertical = 8.dp),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(text = subtitle)
                Text(
                    text = "Checked: room sensor scan. Not checked / cannot be detected by phone: cellular cameras, offline SD-card cameras without IR LEDs, wired cameras on other segments.",
                    style = MaterialTheme.typography.bodySmall,
                )
                Text(
                    text = "Disclaimer: This application estimates hidden-camera risk and never claims that a room is safe. Risk output is LOW, MEDIUM, or HIGH.",
                    style = MaterialTheme.typography.bodySmall,
                )
            }
        }
        Button(onClick = { navController.navigateUp() }) {
            Text("Back")
        }
    }
}
