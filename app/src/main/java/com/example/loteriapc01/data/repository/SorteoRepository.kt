package com.example.loteriapc01.data.repository

import com.example.loteriapc01.data.model.BolillaPublicada
import com.example.loteriapc01.data.model.Jugada
import com.example.loteriapc01.data.model.Sorteo
import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration

/**
 * Acceso a Cloud Firestore. Estructura de datos:
 *
 *   sorteos/sorteo_prueba                  -> cabecera (identificador, nombre, fecha, estado)
 *   sorteos/sorteo_prueba/bolillas/{id}    -> numero, orden
 *   sorteos/sorteo_prueba/jugadas/{uid}    -> sorteoId, uid, numeros
 *
 * Las funciones "escuchar..." usan addSnapshotListener: Firestore avisa cada vez que
 * el dato cambia. Devuelven un ListenerRegistration que sirve para dejar de escuchar.
 */
class SorteoRepository(
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    fun referenciaSorteo(): DocumentReference =
        db.collection(COLECCION_SORTEOS).document(ID_SORTEO_PRUEBA)

    // El id del documento de la jugada es el UID: así solo puede existir una por usuario.
    private fun referenciaJugada(uid: String): DocumentReference =
        referenciaSorteo().collection(COLECCION_JUGADAS).document(uid)

    /** Escucha la cabecera del sorteo (nombre, fecha y estado). */
    fun escucharSorteo(
        alCambiar: (Sorteo?) -> Unit,
        alFallar: (Exception) -> Unit
    ): ListenerRegistration {
        return referenciaSorteo().addSnapshotListener { documento, error ->
            if (error != null) {
                alFallar(error)
            } else if (documento != null && documento.exists()) {
                alCambiar(aSorteo(documento))
            } else {
                alCambiar(null)
            }
        }
    }

    /** Escucha las bolillas publicadas y las entrega ordenadas por orden de salida. */
    fun escucharBolillas(
        alCambiar: (List<BolillaPublicada>) -> Unit,
        alFallar: (Exception) -> Unit
    ): ListenerRegistration {
        return referenciaSorteo().collection(COLECCION_BOLILLAS)
            .addSnapshotListener { consulta, error ->
                if (error != null) {
                    alFallar(error)
                } else if (consulta != null) {
                    val bolillas = consulta.documents
                        .mapNotNull { documento -> aBolilla(documento) }
                        .sortedBy { bolilla -> bolilla.orden }
                    alCambiar(bolillas)
                }
            }
    }

    /** Escucha únicamente la jugada del usuario indicado. */
    fun escucharJugada(
        uid: String,
        alCambiar: (Jugada?) -> Unit,
        alFallar: (Exception) -> Unit
    ): ListenerRegistration {
        return referenciaJugada(uid).addSnapshotListener { documento, error ->
            if (error != null) {
                alFallar(error)
            } else if (documento != null && documento.exists()) {
                alCambiar(aJugada(documento))
            } else {
                alCambiar(null)
            }
        }
    }

    /**
     * Crea la jugada del usuario. Las reglas de Firestore rechazan la escritura si el
     * sorteo no está abierto, si ya existe una jugada o si los números no son válidos.
     */
    fun guardarJugada(uid: String, numeros: List<Int>, alTerminar: (Exception?) -> Unit) {
        val datos = hashMapOf<String, Any>(
            "sorteoId" to ID_SORTEO_PRUEBA,
            "uid" to uid,
            "numeros" to numeros
        )
        referenciaJugada(uid).set(datos)
            .addOnSuccessListener { alTerminar(null) }
            .addOnFailureListener { error -> alTerminar(error) }
    }

    // ---- Conversión de documentos de Firestore a objetos de la app ----

    private fun aSorteo(documento: DocumentSnapshot): Sorteo {
        // Se normaliza el estado para aceptar "En curso", "en_curso", etc.
        val estado = (documento.get("estado")?.toString() ?: "")
            .trim()
            .lowercase()
            .replace("_", " ")
            .replace("-", " ")
        return Sorteo(
            identificador = documento.get("identificador")?.toString() ?: documento.id,
            nombre = documento.get("nombre")?.toString() ?: "",
            fecha = documento.get("fecha") as? Timestamp,
            estado = estado
        )
    }

    private fun aBolilla(documento: DocumentSnapshot): BolillaPublicada? {
        // Los números están guardados como valores numéricos en Firestore.
        val numero = (documento.get("numero") as? Number)?.toInt() ?: return null
        val orden = (documento.get("orden") as? Number)?.toInt() ?: return null
        return BolillaPublicada(numero = numero, orden = orden)
    }

    private fun aJugada(documento: DocumentSnapshot): Jugada {
        val lista = documento.get("numeros") as? List<*> ?: emptyList<Any>()
        val numeros = lista.mapNotNull { valor -> (valor as? Number)?.toInt() }
        return Jugada(
            sorteoId = documento.get("sorteoId")?.toString() ?: "",
            uid = documento.get("uid")?.toString() ?: "",
            numeros = numeros
        )
    }

    companion object {
        const val COLECCION_SORTEOS = "sorteos"
        const val ID_SORTEO_PRUEBA = "sorteo_prueba"
        const val COLECCION_BOLILLAS = "bolillas"
        const val COLECCION_JUGADAS = "jugadas"
    }
}
