package com.sky.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun LocationInputCard(
    onConfirm: (latitude: Double, longitude: Double) -> Unit,
    onDismiss: () -> Unit
) {
    var latitude by remember { mutableStateOf("31.7683") }
    var longitude by remember { mutableStateOf("35.2137") }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                "Set Custom Location",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text("Latitude") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                singleLine = true
            )

            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = { Text("Longitude") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true
            )

            if (errorMessage != null) {
                Text(
                    errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = { onDismiss() },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.outlinedButtonColors()
                ) {
                    Text("Cancel")
                }

                Button(
                    onClick = {
                        try {
                            val lat = latitude.toDouble()
                            val lon = longitude.toDouble()

                            if (lat < -90 || lat > 90) {
                                errorMessage = "Latitude must be between -90 and 90"
                                return@Button
                            }
                            if (lon < -180 || lon > 180) {
                                errorMessage = "Longitude must be between -180 and 180"
                                return@Button
                            }

                            onConfirm(lat, lon)
                        } catch (e: NumberFormatException) {
                            errorMessage = "Invalid number format"
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Confirm")
                }
            }
        }
    }
}
