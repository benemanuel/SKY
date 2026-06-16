package com.sky.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sky.app.domain.CelestialCalculations

@Composable
fun SeasonCard(seasonInfo: CelestialCalculations.SeasonInfo) {
    val seasonEmoji = when (seasonInfo.name) {
        "Spring" -> "🌱"
        "Summer" -> "☀️"
        "Autumn" -> "🍂"
        else -> "❄️"
    }

    val totalDays = seasonInfo.daysElapsed + seasonInfo.daysRemaining
    val progress = if (totalDays > 0) seasonInfo.daysElapsed.toFloat() / totalDays else 0f

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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "$seasonEmoji ${seasonInfo.name}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Days Elapsed: ${seasonInfo.daysElapsed}",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    "Days Remaining: ${seasonInfo.daysRemaining}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp),
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}
