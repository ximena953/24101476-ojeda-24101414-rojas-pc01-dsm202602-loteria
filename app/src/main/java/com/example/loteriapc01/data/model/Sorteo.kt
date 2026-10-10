package com.example.loteriapc01.data.model

import com.google.firebase.Timestamp

/**
 * Cabecera del sorteo, tal como está guardada en Firestore (sorteos/sorteo_prueba).
 * El estado siempre se guarda aquí en minúsculas: "abierto", "en curso" o "finalizado".
 */
data class Sorteo(
    val identificador: String = "",
    val nombre: String = "",
    val fecha: Timestamp? = null,
    val estado: String = ESTADO_ABIERTO
) {
    companion object {
        // Valores posibles del campo "estado".
        const val ESTADO_ABIERTO = "abierto"
        const val ESTADO_EN_CURSO = "en curso"
        const val ESTADO_FINALIZADO = "finalizado"
    }
}
