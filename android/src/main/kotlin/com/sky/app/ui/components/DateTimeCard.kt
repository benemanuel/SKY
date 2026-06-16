package com.sky.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDateTime
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun DateTimeCard(currentDateTime: LocalDateTime) {
    val dayOfWeek = currentDateTime.dayOfWeek.getDisplayName(TextStyle.FULL, Locale("he", "IL"))
    val time = String.format("%02d:%02d:%02d", currentDateTime.hour, currentDateTime.minute, currentDateTime.second)
    val date = "${currentDateTime.dayOfMonth}/${currentDateTime.monthValue}/${currentDateTime.year}"

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1a1a2e).copy(alpha = 0.8f)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                dayOfWeek.replaceFirstChar { it.uppercase() },
                style = androidx.compose.material3.MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Light,
                color = Color(0xFFa8d8ff),
                letterSpacing = 2.sp
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                time,
                fontWeight = FontWeight.Bold,
                fontSize = 56.sp,
                color = Color(0xFFFFC107),
                letterSpacing = (-1).sp
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                date,
                style = androidx.compose.material3.MaterialTheme.typography.bodyLarge,
                color = Color(0xFFe8e8f0)
            )
        }
    }
}

@Composable
fun LocationCard(location: android.location.Location) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1a1a2e).copy(alpha = 0.6f)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Latitude",
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    color = Color(0xFFa8d8ff)
                )
                Text(
                    String.format("%.4f°", location.latitude),
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFe8e8f0),
                    fontWeight = FontWeight.SemiBold
                )
            }

            Text("•", color = Color(0xFF3a4a6a), fontSize = 20.sp)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Longitude",
                    style = androidx.compose.material3.MaterialTheme.typography.labelSmall,
                    color = Color(0xFFa8d8ff)
                )
                Text(
                    String.format("%.4f°", location.longitude),
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = Color(0xFFe8e8f0),
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}
