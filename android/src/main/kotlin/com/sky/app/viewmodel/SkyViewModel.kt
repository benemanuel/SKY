package com.sky.app.viewmodel

import android.app.Application
import android.location.Location
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sky.app.data.LocationRepository
import com.sky.app.data.PreferencesRepository
import com.sky.app.domain.CelestialCalculations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime

class SkyViewModel(application: Application) : AndroidViewModel(application) {
    private val locationRepository = LocationRepository(application)
    private val preferencesRepository = PreferencesRepository(application)

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location?>(null)
    val currentLocation: StateFlow<Location?> = _currentLocation.asStateFlow()

    private val _lunarInfo = MutableStateFlow(CelestialCalculations.LunarInfo(0, "", 0))
    val lunarInfo: StateFlow<CelestialCalculations.LunarInfo> = _lunarInfo.asStateFlow()

    private val _seasonInfo = MutableStateFlow(CelestialCalculations.SeasonInfo("", 0, 0))
    val seasonInfo: StateFlow<CelestialCalculations.SeasonInfo> = _seasonInfo.asStateFlow()

    private val _sunTimes = MutableStateFlow(CelestialCalculations.SunTimes(0L, 0L, 0L))
    val sunTimes: StateFlow<CelestialCalculations.SunTimes> = _sunTimes.asStateFlow()

    private val _currentDateTime = MutableStateFlow(LocalDateTime.now())
    val currentDateTime: StateFlow<LocalDateTime> = _currentDateTime.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.isDarkMode.collect { isDark ->
                _isDarkMode.value = isDark
            }
        }

        startUpdatingTime()
        requestLocationPermission()
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            preferencesRepository.setDarkMode(!_isDarkMode.value)
        }
    }

    fun requestLocationPermission() {
        viewModelScope.launch {
            locationRepository.getCurrentLocation { location ->
                _currentLocation.value = location
                updateCelestialInfo()
            }
        }
    }

    fun setCustomLocation(latitude: Double, longitude: Double) {
        val location = Location("custom").apply {
            this.latitude = latitude
            this.longitude = longitude
        }
        _currentLocation.value = location
        updateCelestialInfo()
    }

    private fun updateCelestialInfo() {
        val location = _currentLocation.value ?: return
        val now = LocalDateTime.now()

        _lunarInfo.value = CelestialCalculations.calculateLunarInfo(now)
        _seasonInfo.value = CelestialCalculations.calculateSeasonInfo(now)
        _sunTimes.value = CelestialCalculations.calculateSunTimes(now, location.latitude, location.longitude)
    }

    private fun startUpdatingTime() {
        viewModelScope.launch {
            while (true) {
                _currentDateTime.value = LocalDateTime.now()
                kotlinx.coroutines.delay(1000)
            }
        }
    }
}
