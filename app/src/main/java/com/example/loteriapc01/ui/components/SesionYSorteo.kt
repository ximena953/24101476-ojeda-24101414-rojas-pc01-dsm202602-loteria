package com.example.loteriapc01.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.loteriapc01.data.model.Sorteo
import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

/** Muestra el correo del usuario autenticado y el botón para cerrar sesión. */
@Composable
fun BarraUsuario(correo: String, cerrarSesion: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "Sesión iniciada como",
                style = MaterialTheme.typography.labelMedium
            )
            Text(
                text = correo,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        TextButton(onClick = cerrarSesion) {
            Text(text = "Cerrar sesión")
        }
    }
}

/** Tarjeta con el nombre, la fecha y el estado del sorteo. */
@Composable
fun TarjetaSorteo(sorteo: Sorteo?) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            if (sorteo == null) {
                Text(
                    text = "Cargando sorteo...",
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            } else {
                Text(
                    text = sorteo.nombre,
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Fecha: " + formatearFecha(sorteo.fecha),
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Estado: " + sorteo.estado,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

private fun formatearFecha(fecha: Timestamp?): String {
    if (fecha == null) {
        return "sin fecha"
    }
    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    return formato.format(fecha.toDate())
}
