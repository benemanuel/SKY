package com.sky.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.sky.app.BuildConfig
import com.sky.app.data.LocationRepository
import com.sky.app.data.PreferencesRepository
import com.sky.app.data.UpdateInfo
import com.sky.app.data.WatchSettingsRepository
import com.sky.app.data.checkForUpdate
import com.sky.app.domain.CelestialCalculations
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDateTime

class SkyViewModel(application: Application) : AndroidViewModel(application) {
    private val preferencesRepository = PreferencesRepository(application)
    private val locationRepository = LocationRepository(application)
    private val watchSettingsRepository = WatchSettingsRepository(application)

    private val _updateInfo = MutableStateFlow<UpdateInfo?>(null)
    val updateInfo: StateFlow<UpdateInfo?> = _updateInfo.asStateFlow()

    /** Current watch face "Center" choice, synced over the Wearable Data Layer. */
    val watchCenterOption: StateFlow<String> = watchSettingsRepository.centerOption

    /** Whether a watch with the SKY app is currently connected. */
    val watchConnected: StateFlow<Boolean> = watchSettingsRepository.watchConnected

    // Default coordinates: Jerusalem, Israel (matches the web app default).
    private val _latitude = MutableStateFlow(31.7781)
    private val _longitude = MutableStateFlow(35.2360)
    val latitude: StateFlow<Double> = _latitude.asStateFlow()
    val longitude: StateFlow<Double> = _longitude.asStateFlow()

    private val _isCustomLocation = MutableStateFlow(false)
    val isCustomLocation: StateFlow<Boolean> = _isCustomLocation.asStateFlow()

    private val _isDarkMode = MutableStateFlow(false)
    val isDarkMode: StateFlow<Boolean> = _isDarkMode.asStateFlow()

    private val _currentDateTime = MutableStateFlow(LocalDateTime.now())
    val currentDateTime: StateFlow<LocalDateTime> = _currentDateTime.asStateFlow()

    private val _lunarInfo = MutableStateFlow(CelestialCalculations.calculateLunarInfo(LocalDateTime.now()))
    val lunarInfo: StateFlow<CelestialCalculations.LunarInfo> = _lunarInfo.asStateFlow()

    private val _seasonInfo = MutableStateFlow(CelestialCalculations.calculateSeason(LocalDateTime.now()))
    val seasonInfo: StateFlow<CelestialCalculations.SeasonInfo> = _seasonInfo.asStateFlow()

    private val _sunTimes = MutableStateFlow(
        CelestialCalculations.calculateSunTimes(LocalDateTime.now(), _latitude.value, _longitude.value)
    )
    val sunTimes: StateFlow<CelestialCalculations.SunTimes> = _sunTimes.asStateFlow()

    private val _seasonalHour = MutableStateFlow(
        CelestialCalculations.calculateSeasonalHour(LocalDateTime.now(), _sunTimes.value)
    )
    val seasonalHour: StateFlow<CelestialCalculations.SeasonalHour> = _seasonalHour.asStateFlow()

    private val _tides = MutableStateFlow(
        CelestialCalculations.calculateTides(LocalDateTime.now(), _longitude.value)
    )
    val tides: StateFlow<CelestialCalculations.TideTimes> = _tides.asStateFlow()

    init {
        viewModelScope.launch {
            preferencesRepository.isDarkMode.collect { isDark ->
                _isDarkMode.value = isDark
            }
        }
        startUpdating()
        watchSettingsRepository.start()
        viewModelScope.launch {
            _updateInfo.value = checkForUpdate(BuildConfig.VERSION_CODE)
        }
    }

    override fun onCleared() {
        super.onCleared()
        watchSettingsRepository.stop()
    }

    fun dismissUpdate() { _updateInfo.value = null }

    /** Push a new watch face Center choice to the connected watch. */
    fun setWatchCenterOption(id: String) {
        watchSettingsRepository.setCenterOption(id)
    }

    fun refreshWatchConnected() {
        watchSettingsRepository.refreshWatchConnected()
    }

    fun toggleDarkMode() {
        viewModelScope.launch {
            preferencesRepository.setDarkMode(!_isDarkMode.value)
        }
    }

    /** Fetches the device GPS fix and updates coordinates (like the web geolocation button). */
    fun useDeviceLocation() {
        locationRepository.getCurrentLocation { location ->
            if (location != null) {
                setCustomLocation(location.latitude, location.longitude)
            }
        }
    }

    fun setCustomLocation(latitude: Double, longitude: Double) {
        _latitude.value = latitude
        _longitude.value = longitude
        _isCustomLocation.value = true
        updateCelestialInfo()
    }

    private fun updateCelestialInfo() {
        val now = LocalDateTime.now()
        val lat = _latitude.value
        val lon = _longitude.value

        _lunarInfo.value = CelestialCalculations.calculateLunarInfo(now)
        _seasonInfo.value = CelestialCalculations.calculateSeason(now)
        val sun = CelestialCalculations.calculateSunTimes(now, lat, lon)
        _sunTimes.value = sun
        _seasonalHour.value = CelestialCalculations.calculateSeasonalHour(now, sun)
        _tides.value = CelestialCalculations.calculateTides(now, lon)
    }

    private fun startUpdating() {
        viewModelScope.launch {
            var lastMinute = -1
            while (true) {
                val now = LocalDateTime.now()
                _currentDateTime.value = now
                // Celestial values change at most once per minute; recompute only then.
                val minuteOfDay = now.hour * 60 + now.minute
                if (minuteOfDay != lastMinute) {
                    lastMinute = minuteOfDay
                    updateCelestialInfo()
                }
                kotlinx.coroutines.delay(1000)
            }
        }
    }
}
