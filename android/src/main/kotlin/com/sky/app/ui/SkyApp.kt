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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sky.app.ui.components.*
import com.sky.app.ui.theme.BackgroundDark
import com.sky.app.viewmodel.SkyViewModel

@Composable
fun SkyApp(viewModel: SkyViewModel, isDarkMode: Boolean) {
    val currentDateTime by viewModel.currentDateTime.collectAsState()
    val lunarInfo by viewModel.lunarInfo.collectAsState()
    val seasonInfo by viewModel.seasonInfo.collectAsState()
    val sunTimes by viewModel.sunTimes.collectAsState()
    val currentLocation by viewModel.currentLocation.collectAsState()

    var showLocationInput by remember { mutableStateOf(false) }

    // Sky gradient: deep indigo to warmer tones (suggests horizon)
    val skyGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF0f1a3d),      // Deep indigo at top
            Color(0xFF1a2a4e),      // Slightly lighter
            Color(0xFF254068),      // Warmer indigo toward horizon
        )
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(skyGradient)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header: minimal, sophisticated
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp, bottom = 32.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "SKY",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFe8e8f0)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    IconButton(onClick = { showLocationInput = !showLocationInput }) {
                        Icon(
                            Icons.Filled.LocationOn,
                            contentDescription = "Set location",
                            tint = Color(0xFFFFC107)
                        )
                    }

                    IconButton(onClick = { viewModel.toggleDarkMode() }) {
                        Icon(
                            if (isDarkMode) Icons.Filled.Brightness7 else Icons.Filled.Brightness4,
                            contentDescription = "Toggle theme",
                            tint = Color(0xFFFFC107)
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
                Spacer(modifier = Modifier.height(16.dp))
            }

            // PRIMARY: Current moment (date/time/location) - hero section
            DateTimeCard(currentDateTime)
            Spacer(modifier = Modifier.height(8.dp))
            if (currentLocation != null) {
                LocationCard(currentLocation!!)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // CELESTIAL: Sun & Moon (gold accent theme)
            Text(
                "CELESTIAL",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFFFC107),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            LunarCard(lunarInfo)
            Spacer(modifier = Modifier.height(12.dp))

            if (currentLocation != null) {
                SunTimesCard(sunTimes, currentLocation!!)
            }

            Spacer(modifier = Modifier.height(32.dp))

            // CYCLES: Week, Season (cyan accent theme)
            Text(
                "CYCLES",
                style = MaterialTheme.typography.labelMedium,
                color = Color(0xFFA8D8FF),
                modifier = Modifier
                    .align(Alignment.Start)
                    .padding(horizontal = 8.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            WeekProgressCard(currentDateTime)
            Spacer(modifier = Modifier.height(12.dp))

            SeasonCard(seasonInfo)

            Spacer(modifier = Modifier.height(40.dp))
        }
    }
}
