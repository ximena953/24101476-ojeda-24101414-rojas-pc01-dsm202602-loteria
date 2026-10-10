package com.example.loteriapc01.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

/**
 * Un número del tablero (1 a 45) que se puede marcar o desmarcar al tocarlo.
 */
@Composable
fun NumeroSeleccionable(
    numero: Int,
    seleccionado: Boolean,
    alPulsar: () -> Unit,
    modifier: Modifier = Modifier
) {
    val colorFondo = if (seleccionado) {
        MaterialTheme.colorScheme.primary
    } else {
        MaterialTheme.colorScheme.surfaceVariant
    }
    val colorTexto = if (seleccionado) {
        MaterialTheme.colorScheme.onPrimary
    } else {
        MaterialTheme.colorScheme.onSurfaceVariant
    }

    Box(
        modifier = modifier
            .padding(2.dp)
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(colorFondo)
            .clickable(onClick = alPulsar),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = numero.toString(),
            color = colorTexto,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
