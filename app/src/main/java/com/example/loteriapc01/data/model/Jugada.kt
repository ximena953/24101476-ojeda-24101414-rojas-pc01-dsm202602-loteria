package com.example.loteriapc01.data.model

/**
 * Jugada de un usuario (sorteos/sorteo_prueba/jugadas/{uid}).
 * Se guarda con exactamente estos tres campos, que son los que aceptan las reglas de seguridad.
 */
data class Jugada(
    val sorteoId: String = "",
    val uid: String = "",
    val numeros: List<Int> = emptyList()
)
