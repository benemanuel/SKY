package com.sky.app.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Brightness4
import androidx.compose.material.icons.filled.Brightness7
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sky.app.ui.components.*
import com.sky.app.viewmodel.SkyViewModel
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun SkyApp(viewModel: SkyViewModel, isDarkMode: Boolean) {
    val currentDateTime by viewModel.currentDateTime.collectAsState()
    val lunarInfo by viewModel.lunarInfo.collectAsState()
    val seasonInfo by viewModel.seasonInfo.collectAsState()
    val sunTimes by viewModel.sunTimes.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()

    var showLocationInput by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header with title and theme toggle
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "SKY",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                IconButton(onClick = { showLocationInput = !showLocationInput }) {
                    Icon(
                        Icons.Filled.LocationOn,
                        contentDescription = "Set location",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(onClick = { viewModel.toggleDarkMode() }) {
                    Icon(
                        if (isDarkMode) Icons.Filled.Brightness7 else Icons.Filled.Brightness4,
                        contentDescription = "Toggle theme",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }

        if (showLocationInput) {
            LocationInputCard(
                onConfirm = { lat, lon ->
                    viewModel.setCustomLocation(lat, lon)
                    showLocationInput = false
                },
                onDismiss = { showLocationInput = false }
            )
        }

        // Current date and time
        DateTimeCard(currentDateTime)

        Spacer(modifier = Modifier.height(16.dp))

        // Week progress
        WeekProgressCard(currentDateTime)

        Spacer(modifier = Modifier.height(16.dp))

        // Lunar information
        LunarCard(lunarInfo)

        Spacer(modifier = Modifier.height(16.dp))

        // Season information
        SeasonCard(seasonInfo)

        Spacer(modifier = Modifier.height(16.dp))

        // Sun times
        if (currentLocation != null) {
            SunTimesCard(sunTimes, currentLocation!!)
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
}
