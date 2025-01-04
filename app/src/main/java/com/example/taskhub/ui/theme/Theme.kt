package com.example.taskhub.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = Color(0xFF007CF4),
    onPrimary = Color.Black,
    primaryContainer = Color(0xFF1565C0),
    onPrimaryContainer = Color(0xFFD1E4FF),
    secondary = Color(0xFF0099FF),
    onSecondary = Color.Black,
    secondaryContainer = Color(0xFF004B65),
    onSecondaryContainer = Color(0xFFBDE9FF),
    surface = Color(0xFF1A1C1E),
    onSurface = Color(0xFFE3E2E6),
    surfaceVariant = Color(0xFF23282F),
    onSurfaceVariant = Color(0xFFC4C6CF),
    background = Color(0xFF111315),
    onBackground = Color(0xFFE3E2E6),
    error = Color(0xFFFF453A),
    onError = Color.Black
)

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF007CF4),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD6E7FF),
    onPrimaryContainer = Color(0xFF001C37),
    secondary = Color(0xFF00B2FF),
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE5F3FF),
    onSecondaryContainer = Color(0xFF001E2B),
    surface = Color(0xFFF5F9FF),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE7EEFF),
    onSurfaceVariant = Color(0xFF44474E),
    background = Color(0xFFFFFFFF),
    onBackground = Color(0xFF1C1B1F),
    error = Color(0xFFFF3B30),
    onError = Color.White,
)



@Composable
fun TaskHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
//        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
//            val context = LocalContext.current
//            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
//        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}