package com.example.loteriapc01.viewmodel

import com.example.loteriapc01.data.model.BolillaPublicada
import com.example.loteriapc01.data.model.Sorteo

// Cálculos del sorteo. Se hacen en la app a partir de la jugada guardada y de las
// bolillas recibidas desde Firestore. Ningún resultado se escribe en Firestore.

const val TOTAL_BOLILLAS = 6
const val RESULTADO_GANADOR = "GANADOR"
const val RESULTADO_SIGUE_INTENTANDO = "SIGUE INTENTANDO"

/** Números de la jugada que ya salieron entre las bolillas. El orden no importa. */
fun numerosAcertados(numerosJugada: List<Int>, bolillas: List<BolillaPublicada>): Set<Int> {
    val numerosSorteados = bolillas.map { bolilla -> bolilla.numero }.toSet()
    return numerosJugada.toSet().intersect(numerosSorteados)
}

/** Cantidad de números distintos de la jugada presentes entre las bolillas publicadas. */
fun contarAciertos(numerosJugada: List<Int>, bolillas: List<BolillaPublicada>): Int =
    numerosAcertados(numerosJugada, bolillas).size

/** El resultado definitivo solo existe con el sorteo finalizado y las seis bolillas recibidas. */
fun hayResultadoDefinitivo(estado: String?, cantidadBolillas: Int): Boolean =
    estado == Sorteo.ESTADO_FINALIZADO && cantidadBolillas >= TOTAL_BOLILLAS

/** GANADOR con seis aciertos; SIGUE INTENTANDO con menos de seis. */
fun resultadoFinal(aciertos: Int): String =
    if (aciertos >= TOTAL_BOLILLAS) RESULTADO_GANADOR else RESULTADO_SIGUE_INTENTANDO

/** Mensaje que se muestra según el estado del sorteo. */
fun mensajeDelSorteo(estado: String?, cantidadBolillas: Int): String = when {
    estado == null -> "Cargando sorteo..."
    estado == Sorteo.ESTADO_ABIERTO -> "Esperando el inicio del sorteo"
    estado == Sorteo.ESTADO_EN_CURSO -> "Sorteo en curso"
    hayResultadoDefinitivo(estado, cantidadBolillas) -> "Sorteo finalizado"
    estado == Sorteo.ESTADO_FINALIZADO -> "Esperando resultados: aún faltan bolillas"
    else -> "Estado del sorteo: $estado"
}
