package com.example.loteriapc01.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Colores para el modo claro.
private val ColoresClaros = lightColorScheme(
    primary = AzulLoteria,
    onPrimary = Color.White,
    primaryContainer = AzulClaro,
    onPrimaryContainer = AzulLoteria,
    secondary = DoradoLoteria,
    onSecondary = Color.Black
)

// Colores para el modo oscuro.
private val ColoresOscuros = darkColorScheme(
    primary = AzulNoche,
    onPrimary = AzulLoteria,
    primaryContainer = AzulLoteria,
    onPrimaryContainer = AzulClaro,
    secondary = DoradoLoteria,
    onSecondary = Color.Black
)

/**
 * Tema Material 3 de la app. Todas las pantallas se envuelven con este tema
 * para compartir colores y tipografía.
 */
@Composable
fun LoteriaPC01Theme(
    temaOscuro: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (temaOscuro) ColoresOscuros else ColoresClaros,
        content = content
    )
}
