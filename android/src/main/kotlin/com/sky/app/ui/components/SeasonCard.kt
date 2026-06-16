package com.sky.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LinearProgressIndicator
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
            containerColor = Color(0xFF1a1a2e).copy(alpha = 0.7f)
        ),
        shape = androidx.compose.foundation.shape.RoundedCornerShape(16.dp)
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
                color = Color(0xFFA8D8FF),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            Color(0xFF262d4a),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Elapsed",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFa8d8ff),
                        fontSize = 10.sp
                    )
                    Text(
                        "${seasonInfo.daysElapsed}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA8D8FF)
                    )
                }

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .background(
                            Color(0xFF262d4a),
                            shape = androidx.compose.foundation.shape.RoundedCornerShape(12.dp)
                        )
                        .padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Remaining",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color(0xFFa8d8ff),
                        fontSize = 10.sp
                    )
                    Text(
                        "${seasonInfo.daysRemaining}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFA8D8FF)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp),
                color = Color(0xFFA8D8FF),
                trackColor = Color(0xFF262d4a)
            )
        }
    }
}
