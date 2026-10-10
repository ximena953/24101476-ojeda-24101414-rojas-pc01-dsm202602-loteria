package com.example.loteriapc01.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * Dibuja un número dentro de un círculo, como una bolilla de lotería.
 * Si [acertada] es true se pinta de dorado. Este componente se reutilizará
 * en las pantallas de jugada y de seguimiento del sorteo.
 */
@Composable
fun Bolilla(
    numero: Int,
    modifier: Modifier = Modifier,
    acertada: Boolean = false
) {
    val colorFondo = if (acertada) {
        MaterialTheme.colorScheme.secondary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val colorTexto = if (acertada) {
        MaterialTheme.colorScheme.onSecondary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .size(48.dp)
            .clip(CircleShape)
            .background(colorFondo),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = numero.toString(),
            color = colorTexto,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}
