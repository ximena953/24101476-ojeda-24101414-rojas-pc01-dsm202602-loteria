package com.example.loteriapc01.ui.screens

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.loteriapc01.ui.components.BarraUsuario
import com.example.loteriapc01.ui.components.Bolilla
import com.example.loteriapc01.ui.components.TarjetaSorteo
import com.example.loteriapc01.viewmodel.SorteoViewModel
import com.example.loteriapc01.viewmodel.TOTAL_BOLILLAS
import com.example.loteriapc01.viewmodel.hayResultadoDefinitivo
import com.example.loteriapc01.viewmodel.mensajeDelSorteo
import com.example.loteriapc01.viewmodel.numerosAcertados
import com.example.loteriapc01.viewmodel.resultadoFinal

private const val PANTALLA_SEGUIMIENTO = "seguimiento"

/**
 * Seguimiento del sorteo en tiempo real: estado, bolillas publicadas, jugada del
 * usuario con sus aciertos resaltados y resultado final.
 */
@Composable
fun SeguimientoScreen(
    correo: String,
    uid: String,
    sorteoViewModel: SorteoViewModel,
    volverAJugada: () -> Unit,
    cerrarSesion: () -> Unit
) {
    // Mientras esta pantalla está abierta se escuchan el sorteo, las bolillas y la
    // jugada. Al salir, onDispose quita los listeners para que no queden activos.
    DisposableEffect(uid) {
        sorteoViewModel.iniciarEscucha(
            pantalla = PANTALLA_SEGUIMIENTO,
            uid = uid,
            incluirBolillas = true
        )
        onDispose {
            sorteoViewModel.detenerEscucha(PANTALLA_SEGUIMIENTO)
        }
    }

    val sorteo = sorteoViewModel.sorteo
    val bolillas = sorteoViewModel.bolillas
    val jugada = sorteoViewModel.jugada
    val mensajeError = sorteoViewModel.mensajeError

    // Cálculos hechos en la app con los datos recibidos de Firestore.
    // Se recalculan solos cada vez que llega una bolilla o cambia el estado.
    val numerosJugada = jugada?.numeros ?: emptyList()
    val acertados = numerosAcertados(numerosJugada, bolillas)
    val aciertos = acertados.size
    val cantidadBolillas = bolillas.size

    Scaffold { paddingInterno ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingInterno)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BarraUsuario(correo = correo, cerrarSesion = cerrarSesion)

            Spacer(modifier = Modifier.height(12.dp))

            TarjetaSorteo(sorteo = sorteo)

            Spacer(modifier = Modifier.height(12.dp))

            // Mensaje según el estado: espera, "Sorteo en curso", etc.
            Text(
                text = mensajeDelSorteo(sorteo?.estado, cantidadBolillas),
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Bolillas publicadas: " + cantidadBolillas + " de " + TOTAL_BOLILLAS,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (bolillas.isEmpty()) {
                Text(text = "Todavía no se publica ninguna bolilla.")
            } else {
                // Las bolillas ya llegan ordenadas por orden de salida.
                Row(
                    modifier = Modifier.horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    bolillas.forEach { bolilla ->
                        Bolilla(numero = bolilla.numero)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = "Tu jugada",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            if (jugada == null) {
                Text(
                    text = if (sorteoViewModel.jugadaConsultada) {
                        "No registraste una jugada para este sorteo."
                    } else {
                        "Consultando tu jugada..."
                    },
                    textAlign = TextAlign.Center
                )
            } else {
                // Cada número acertado se resalta en dorado.
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    numerosJugada.forEach { numero ->
                        Bolilla(numero = numero, acertada = numero in acertados)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Aciertos: " + aciertos,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )

                // Resultado definitivo: solo con el sorteo finalizado y las 6 bolillas.
                if (hayResultadoDefinitivo(sorteo?.estado, cantidadBolillas)) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = resultadoFinal(aciertos),
                        style = MaterialTheme.typography.headlineLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        textAlign = TextAlign.Center
                    )
                }
            }

            if (mensajeError != null) {
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = mensajeError,
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedButton(
                onClick = volverAJugada,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Volver a mi jugada")
            }
        }
    }
}
