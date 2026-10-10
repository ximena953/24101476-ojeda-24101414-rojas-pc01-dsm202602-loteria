package com.example.loteriapc01.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

/**
 * Acceso a Firebase Authentication (correo y contraseña).
 * Cada operación avisa cuando termina: null si salió bien, o la excepción si falló.
 */
class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) {
    // Usuario con sesión iniciada (Firebase recuerda la sesión al reabrir la app).
    val usuarioActual: FirebaseUser?
        get() = auth.currentUser

    fun registrar(correo: String, clave: String, alTerminar: (Exception?) -> Unit) {
        auth.createUserWithEmailAndPassword(correo, clave)
            .addOnCompleteListener { tarea -> alTerminar(tarea.exception) }
    }

    fun iniciarSesion(correo: String, clave: String, alTerminar: (Exception?) -> Unit) {
        auth.signInWithEmailAndPassword(correo, clave)
            .addOnCompleteListener { tarea -> alTerminar(tarea.exception) }
    }

    fun cerrarSesion() {
        auth.signOut()
    }
}
