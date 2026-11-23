package com.rusdili.pro

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rusdili.pro.core.common.AppConstants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val themeViewModel: ThemeViewModel by viewModels()
    private var pendingDestination by mutableStateOf<String?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        pendingDestination = extractNotificationDestination(intent)
        enableEdgeToEdge()
        setContent {
            val darkThemePreference by themeViewModel.darkThemePreference.collectAsStateWithLifecycle()
            val isDarkTheme = darkThemePreference ?: isSystemInDarkTheme()

            DisposableEffect(isDarkTheme) {
                enableEdgeToEdge(
                    statusBarStyle = SystemBarStyle.auto(
                        Color.TRANSPARENT,
                        Color.TRANSPARENT
                    ) { isDarkTheme },
                    navigationBarStyle = SystemBarStyle.auto(
                        Color.argb(0xe6, 0xFF, 0xFF, 0xFF),
                        Color.argb(0x80, 0x1b, 0x1b, 0x1b)
                    ) { isDarkTheme }
                )
                onDispose {}
            }

            RusDiliTheme(darkTheme = isDarkTheme) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RusDiliNavigation(
                        isDarkTheme = isDarkTheme,
                        onToggleTheme = { themeViewModel.toggleTheme(isDarkTheme) },
                        pendingDestination = pendingDestination,
                        onDestinationConsumed = { pendingDestination = null }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        extractNotificationDestination(intent)?.let { destination ->
            pendingDestination = destination
        }
    }

    private fun extractNotificationDestination(intent: Intent?): String? =
        intent?.getStringExtra(AppConstants.EXTRA_NOTIFICATION_DESTINATION)
}
