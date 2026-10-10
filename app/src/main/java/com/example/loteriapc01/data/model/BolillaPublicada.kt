package com.example.loteriapc01.data.model

/**
 * Una bolilla publicada en Firestore (sorteos/sorteo_prueba/bolillas).
 * numero: valor de la bolilla (1 a 45). orden: orden de salida (1 a 6).
 */
data class BolillaPublicada(
    val numero: Int = 0,
    val orden: Int = 0
)
