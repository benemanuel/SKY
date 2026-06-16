package com.sky.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.font.FontWeight

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
            containerColor = Color(0xFF1a1a2e).copy(alpha = 0.9f)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                "Set Location",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFA8D8FF),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            OutlinedTextField(
                value = latitude,
                onValueChange = { latitude = it },
                label = { Text("Latitude", color = Color(0xFFa8d8ff)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true,
                textStyle = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFFe8e8f0)
                )
            )

            OutlinedTextField(
                value = longitude,
                onValueChange = { longitude = it },
                label = { Text("Longitude", color = Color(0xFFa8d8ff)) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                singleLine = true,
                textStyle = androidx.compose.material3.MaterialTheme.typography.bodyMedium.copy(
                    color = Color(0xFFe8e8f0)
                )
            )

            if (errorMessage != null) {
                Text(
                    errorMessage!!,
                    color = Color(0xFFEF9A9A),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onDismiss() },
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancel", color = Color(0xFFA8D8FF))
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
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFFC107)
                    )
                ) {
                    Text("Confirm", color = Color(0xFF1a1a2e), fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
