package com.sky.app.ui.components

import android.location.Location
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sky.app.domain.CelestialCalculations
import java.time.Instant
import java.time.LocalTime
import java.time.ZoneId
import java.util.concurrent.TimeUnit

@Composable
fun SunTimesCard(sunTimes: CelestialCalculations.SunTimes, location: Location) {
    val sunriseTime = Instant.ofEpochMilli(sunTimes.sunrise)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()

    val sunsetTime = Instant.ofEpochMilli(sunTimes.sunset)
        .atZone(ZoneId.systemDefault())
        .toLocalTime()

    val hours = TimeUnit.MILLISECONDS.toHours(sunTimes.dayLength)
    val minutes = (sunTimes.dayLength % TimeUnit.HOURS.toMillis(1)) / TimeUnit.MINUTES.toMillis(1)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1a1a2e).copy(alpha = 0.7f)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                "☀️ Sun",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFFFC107),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SunInfoBox(
                    label = "Sunrise",
                    value = String.format("%02d:%02d", sunriseTime.hour, sunriseTime.minute),
                    modifier = Modifier.weight(1f)
                )

                SunInfoBox(
                    label = "Sunset",
                    value = String.format("%02d:%02d", sunsetTime.hour, sunsetTime.minute),
                    modifier = Modifier.weight(1f)
                )

                SunInfoBox(
                    label = "Day",
                    value = String.format("%dh %02dm", hours, minutes),
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun SunInfoBox(label: String, value: String, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .background(
                Color(0xFF262d4a),
                shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = Color(0xFFa8d8ff),
            fontSize = 10.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFC107)
        )
    }
}
