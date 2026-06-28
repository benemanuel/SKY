package com.sky.app.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

data class UpdateInfo(
    val versionCode: Int,
    val versionName: String,
    val releaseUrl: String,
    val notes: String,
    val updateAvailable: Boolean,
)

private const val VERSION_URL = "https://sky.geulah.org.il/app-version.json"

suspend fun checkForUpdate(currentVersionCode: Int): UpdateInfo? = withContext(Dispatchers.IO) {
    try {
        val conn = URL(VERSION_URL).openConnection() as HttpURLConnection
        conn.connectTimeout = 5_000
        conn.readTimeout = 5_000
        val body = conn.inputStream.bufferedReader().readText()
        conn.disconnect()
        val json = JSONObject(body)
        UpdateInfo(
            versionCode = json.getInt("versionCode"),
            versionName = json.getString("versionName"),
            releaseUrl = json.getString("releaseUrl"),
            notes = json.optString("notes", ""),
            updateAvailable = currentVersionCode < json.getInt("versionCode"),
        )
    } catch (_: Exception) {
        null
    }
}
