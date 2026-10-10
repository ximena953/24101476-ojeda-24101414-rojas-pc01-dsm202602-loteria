package com.example.loteriapc01.ui.screens

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
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.loteriapc01.data.model.Sorteo
import com.example.loteriapc01.ui.components.BarraUsuario
import com.example.loteriapc01.ui.components.Bolilla
import com.example.loteriapc01.ui.components.NumeroSeleccionable
import com.example.loteriapc01.ui.components.TarjetaSorteo
import com.example.loteriapc01.viewmodel.SorteoViewModel
import com.example.loteriapc01.viewmodel.TOTAL_BOLILLAS

private const val PANTALLA_JUGADA = "jugada"

/**
 * Registro de la jugada: el usuario elige 6 números distintos entre 1 y 45.
 * Solo se puede registrar mientras el sorteo está abierto y una sola vez.
 */
@Composable
fun JugadaScreen(
    correo: String,
    uid: String,
    sorteoViewModel: SorteoViewModel,
    irASeguimiento: () -> Unit,
    cerrarSesion: () -> Unit
) {
    // Al entrar se empieza a escuchar el sorteo y la jugada del usuario;
    // al salir de la pantalla se quitan los listeners.
    DisposableEffect(uid) {
        sorteoViewModel.iniciarEscucha(
            pantalla = PANTALLA_JUGADA,
            uid = uid,
            incluirBolillas = false
        )
        onDispose {
            sorteoViewModel.detenerEscucha(PANTALLA_JUGADA)
        }
    }

    // Números marcados en el tablero (todavía sin guardar).
    var seleccion by remember { mutableStateOf(listOf<Int>()) }

    val sorteo = sorteoViewModel.sorteo
    val jugada = sorteoViewModel.jugada
    val mensajeError = sorteoViewModel.mensajeError

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

            Spacer(modifier = Modifier.height(16.dp))

            if (jugada != null) {
                // El usuario ya tiene jugada: solo se muestra, no se puede cambiar.
                Text(
                    text = "Tu jugada registrada",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    jugada.numeros.forEach { numero ->
                        Bolilla(numero = numero)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "La jugada quedó guardada y no se puede modificar.",
                    textAlign = TextAlign.Center
                )
            } else if (sorteo == null || !sorteoViewModel.jugadaConsultada) {
                Text(text = "Consultando tu jugada...")
            } else if (sorteo.estado != Sorteo.ESTADO_ABIERTO) {
                // Sorteo en curso o finalizado: ya no se aceptan jugadas.
                Text(
                    text = "El sorteo ya no está abierto. No es posible registrar jugadas.",
                    color = MaterialTheme.colorScheme.error,
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = "Elige 6 números distintos del 1 al 45",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                // Tablero de 45 números en filas de 9.
                (1..45).chunked(9).forEach { fila ->
                    Row(modifier = Modifier.fillMaxWidth()) {
                        fila.forEach { numero ->
                            NumeroSeleccionable(
                                numero = numero,
                                seleccionado = numero in seleccion,
                                alPulsar = {
                                    if (numero in seleccion) {
                                        seleccion = seleccion - numero
                                    } else if (seleccion.size < TOTAL_BOLILLAS) {
                                        seleccion = seleccion + numero
                                    }
                                },
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Seleccionados: " + seleccion.size + " de " + TOTAL_BOLILLAS)
                Text(text = "Tu selección: " + seleccion.sorted().joinToString(", "))

                Spacer(modifier = Modifier.height(12.dp))
                Button(
                    onClick = { sorteoViewModel.registrarJugada(uid, seleccion) },
                    enabled = seleccion.size == TOTAL_BOLILLAS && !sorteoViewModel.guardando,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = if (sorteoViewModel.guardando) {
                            "Guardando..."
                        } else {
                            "Registrar jugada"
                        }
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

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = irASeguimiento,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(text = "Ir al seguimiento del sorteo")
            }
        }
    }
}
