package com.antigravity.mobile.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = IndigoGlow,
    onPrimary = TextPrimary,
    primaryContainer = DarkSurfaceElevated,
    onPrimaryContainer = IndigoGlowLight,
    secondary = CyanNeon,
    onSecondary = DeepObsidian,
    secondaryContainer = DarkSurfaceElevated,
    onSecondaryContainer = CyanNeon,
    tertiary = EmeraldSuccess,
    background = DeepObsidian,
    onBackground = TextPrimary,
    surface = DarkSurface,
    onSurface = TextPrimary,
    surfaceVariant = DarkSurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = DarkBorder,
    outlineVariant = DarkBorderSubtle,
    error = RoseError,
    onError = TextPrimary,
    errorContainer = RoseErrorBg,
    onErrorContainer = RoseError
)

@Composable
fun AntigravityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = DeepObsidian.toArgb()
            window.navigationBarColor = DeepObsidian.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = AntigravityTypography,
        shapes = AntigravityShapes,
        content = content
    )
}
