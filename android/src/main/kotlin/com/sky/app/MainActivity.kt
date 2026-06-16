package com.sky.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import com.sky.app.ui.SkyApp
import com.sky.app.ui.theme.SkyTheme
import com.sky.app.viewmodel.SkyViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SkyTheme {
                Surface(
                    modifier = androidx.compose.foundation.layout.Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: SkyViewModel = viewModel()
                    val isDarkMode by viewModel.isDarkMode.collectAsState()

                    SkyApp(viewModel = viewModel, isDarkMode = isDarkMode)
                }
            }
        }
    }
}
