package com.sky.app.ui.components

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
import java.time.LocalDateTime

@Composable
fun WeekProgressCard(currentDateTime: LocalDateTime) {
    val dayOfWeek = currentDateTime.dayOfWeek.value
    val daysInWeek = 7

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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(daysInWeek) { day ->
                    val dayNumber = day + 1
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .background(
                                if (dayNumber <= dayOfWeek)
                                    Color(0xFFA8D8FF)
                                else
                                    Color(0xFF3a4a6a),
                                shape = androidx.compose.foundation.shape.RoundedCornerShape(10.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            dayNumber.toString(),
                            fontWeight = FontWeight.Bold,
                            fontSize = androidx.compose.material3.MaterialTheme.typography.labelMedium.fontSize,
                            color = if (dayNumber <= dayOfWeek)
                                Color(0xFF0f1a3d)
                            else
                                Color(0xFF3a4a6a)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                "Day $dayOfWeek of $daysInWeek",
                style = MaterialTheme.typography.labelSmall,
                color = Color(0xFFA8D8FF)
            )
        }
    }
}
