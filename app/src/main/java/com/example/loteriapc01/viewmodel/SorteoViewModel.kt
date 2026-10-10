package com.example.loteriapc01.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.loteriapc01.data.model.BolillaPublicada
import com.example.loteriapc01.data.model.Jugada
import com.example.loteriapc01.data.model.Sorteo
import com.example.loteriapc01.data.repository.SorteoRepository
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.ListenerRegistration

/**
 * Estado del sorteo, de las bolillas y de la jugada del usuario.
 * Mantiene los listeners de Firestore y se encarga de quitarlos.
 */
class SorteoViewModel : ViewModel() {

    private val repositorio = SorteoRepository()

    var sorteo by mutableStateOf<Sorteo?>(null)
        private set
    var bolillas by mutableStateOf<List<BolillaPublicada>>(emptyList())
        private set
    var jugada by mutableStateOf<Jugada?>(null)
        private set

    // true cuando Firestore ya respondió si el usuario tiene jugada o no.
    var jugadaConsultada by mutableStateOf(false)
        private set
    var guardando by mutableStateOf(false)
        private set
    var mensajeError by mutableStateOf<String?>(null)
        private set

    // Listeners activos y pantalla que los pidió.
    private val escuchas = mutableListOf<ListenerRegistration>()
    private var pantallaActiva: String? = null

    /**
     * Empieza a escuchar Firestore para una pantalla. Antes quita los listeners
     * anteriores, así nunca quedan duplicados.
     */
    fun iniciarEscucha(pantalla: String, uid: String, incluirBolillas: Boolean) {
        quitarEscuchas()
        pantallaActiva = pantalla
        mensajeError = null

        escuchas.add(
            repositorio.escucharSorteo(
                alCambiar = { sorteoRecibido -> sorteo = sorteoRecibido },
                alFallar = { error -> mensajeError = traducirError(error) }
            )
        )
        escuchas.add(
            repositorio.escucharJugada(
                uid = uid,
                alCambiar = { jugadaRecibida ->
                    jugada = jugadaRecibida
                    jugadaConsultada = true
                },
                alFallar = { error -> mensajeError = traducirError(error) }
            )
        )
        if (incluirBolillas) {
            escuchas.add(
                repositorio.escucharBolillas(
                    alCambiar = { bolillasRecibidas -> bolillas = bolillasRecibidas },
                    alFallar = { error -> mensajeError = traducirError(error) }
                )
            )
        }
    }

    /** La pantalla avisa que se cerró: se dejan de escuchar los cambios. */
    fun detenerEscucha(pantalla: String) {
        if (pantallaActiva == pantalla) {
            quitarEscuchas()
            pantallaActiva = null
        }
    }

    /** Al cerrar sesión se borra lo que había en memoria del usuario anterior. */
    fun limpiar() {
        quitarEscuchas()
        pantallaActiva = null
        sorteo = null
        bolillas = emptyList()
        jugada = null
        jugadaConsultada = false
        guardando = false
        mensajeError = null
    }

    /** Valida y registra la jugada del usuario. */
    fun registrarJugada(uid: String, numeros: List<Int>) {
        val errorValidacion = validarJugada(numeros)
        if (errorValidacion != null) {
            mensajeError = errorValidacion
            return
        }
        guardando = true
        mensajeError = null
        repositorio.guardarJugada(uid, numeros.sorted()) { excepcion ->
            guardando = false
            if (excepcion != null) {
                mensajeError = traducirError(excepcion)
            }
        }
    }

    private fun validarJugada(numeros: List<Int>): String? {
        if (sorteo?.estado != Sorteo.ESTADO_ABIERTO) {
            return "El sorteo ya no está abierto: no se pueden registrar jugadas."
        }
        if (jugada != null) {
            return "Ya registraste tu jugada para este sorteo."
        }
        if (numeros.size != TOTAL_BOLILLAS) {
            return "Debes elegir exactamente 6 números."
        }
        if (numeros.toSet().size != TOTAL_BOLILLAS) {
            return "Los 6 números deben ser distintos."
        }
        if (numeros.any { numero -> numero < 1 || numero > 45 }) {
            return "Los números deben estar entre 1 y 45."
        }
        return null
    }

    private fun traducirError(error: Exception): String {
        if (error is FirebaseFirestoreException &&
            error.code == FirebaseFirestoreException.Code.PERMISSION_DENIED
        ) {
            return "Firestore rechazó la operación: el sorteo no está abierto, " +
                "ya tienes una jugada o no tienes permiso."
        }
        return "Ocurrió un error: " + (error.localizedMessage ?: "desconocido")
    }

    private fun quitarEscuchas() {
        escuchas.forEach { escucha -> escucha.remove() }
        escuchas.clear()
    }

    // Si el ViewModel se destruye, tampoco quedan listeners activos.
    override fun onCleared() {
        quitarEscuchas()
        super.onCleared()
    }
}
