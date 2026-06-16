package com.sky.app.ui.components

import android.location.Location
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
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
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            Text(
                "☀️ Sun Times",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                "Location: ${String.format("%.4f, %.4f", location.latitude, location.longitude)}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        "Sunrise",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        String.format("%02d:%02d", sunriseTime.hour, sunriseTime.minute),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column {
                    Text(
                        "Sunset",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        String.format("%02d:%02d", sunsetTime.hour, sunsetTime.minute),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Column {
                    Text(
                        "Day Length",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        String.format("%02dh %02dm", hours, minutes),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
