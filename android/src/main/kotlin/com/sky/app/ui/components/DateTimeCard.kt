package com.sky.app.ui.components

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
                dayOfWeek.replaceFirstChar { it.uppercase() },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                time,
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                fontSize = 36.sp,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                date,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}
