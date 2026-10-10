package com.example.loteriapc01.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.loteriapc01.R
import com.example.loteriapc01.data.repository.SorteoRepository
import com.example.loteriapc01.ui.components.Bolilla
import com.example.loteriapc01.ui.theme.LoteriaPC01Theme
import com.example.loteriapc01.ui.theme.VerdeOk
import com.google.firebase.FirebaseApp

/**
 * Pantalla inicial de la Fase 1. Solo comprueba que el proyecto, Compose y
 * Material 3 funcionan. En las siguientes fases será reemplazada por el login.
 */
@Composable
fun BienvenidaScreen() {
    // Estado de Compose: cuando cambia, la pantalla se vuelve a dibujar sola.
    var toques by remember { mutableIntStateOf(0) }

    // Verificación de Firebase: si la app se inicializó con google-services.json,
    // FirebaseApp existe y conoce el id del proyecto. Si no, queda en null.
    val proyectoFirebase = remember {
        try {
            FirebaseApp.getInstance().options.projectId
        } catch (e: IllegalStateException) {
            null
        }
    }

    Scaffold { paddingInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Bolillas decorativas (una resaltada, para ver los dos estilos).
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Bolilla(numero = 7)
                Bolilla(numero = 21, acertada = true)
                Bolilla(numero = 45)
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = stringResource(R.string.app_name),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "¡Bienvenido! Aquí podrás registrar tu jugada y seguir el sorteo en tiempo real.",
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Indicador visual de que el proyecto está funcionando.
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(VerdeOk)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Proyecto funcionando",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Text(
                            text = "Kotlin · Jetpack Compose · Material 3",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Estado de la conexión con Firebase y documento que usará la app.
            Text(
                text = if (proyectoFirebase != null) {
                    "Firebase inicializado: $proyectoFirebase"
                } else {
                    "Firebase NO inicializado"
                },
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )
            Text(
                text = "Sorteo: ${SorteoRepository.COLECCION_SORTEOS}/${SorteoRepository.ID_SORTEO_PRUEBA}",
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Botón de prueba: confirma que la interfaz responde y se actualiza.
            Button(onClick = { toques++ }) {
                Text(text = "Probar interacción")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Toques: $toques",
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

// Vista previa dentro de Android Studio (no se ejecuta en la app real).
@Preview(showBackground = true)
@Composable
private fun BienvenidaScreenPreview() {
    LoteriaPC01Theme {
        BienvenidaScreen()
    }
}
