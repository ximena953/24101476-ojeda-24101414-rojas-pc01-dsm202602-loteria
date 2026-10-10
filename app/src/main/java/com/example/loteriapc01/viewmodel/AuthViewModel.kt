package com.example.loteriapc01.viewmodel

import android.util.Patterns
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.example.loteriapc01.data.repository.AuthRepository
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException

/**
 * Estado de la autenticación. Las pantallas leen estas variables; como son estado de
 * Compose, la interfaz se vuelve a dibujar sola cuando cambian.
 */
class AuthViewModel : ViewModel() {

    private val repositorio = AuthRepository()

    // Datos del usuario con sesión iniciada (null si no hay sesión).
    var uid by mutableStateOf<String?>(repositorio.usuarioActual?.uid)
        private set
    var correo by mutableStateOf<String?>(repositorio.usuarioActual?.email)
        private set

    var cargando by mutableStateOf(false)
        private set
    var mensajeError by mutableStateOf<String?>(null)
        private set

    fun iniciarSesion(correoIngresado: String, clave: String) {
        val correoLimpio = correoIngresado.trim()
        val errorValidacion = validar(correoLimpio, clave)
        if (errorValidacion != null) {
            mensajeError = errorValidacion
            return
        }
        cargando = true
        mensajeError = null
        repositorio.iniciarSesion(correoLimpio, clave) { excepcion ->
            cargando = false
            if (excepcion == null) {
                actualizarUsuario()
            } else {
                mensajeError = traducirError(excepcion)
            }
        }
    }

    fun registrar(correoIngresado: String, clave: String, confirmacion: String) {
        val correoLimpio = correoIngresado.trim()
        val errorValidacion = validar(correoLimpio, clave)
        if (errorValidacion != null) {
            mensajeError = errorValidacion
            return
        }
        if (clave != confirmacion) {
            mensajeError = "Las contraseñas no coinciden."
            return
        }
        cargando = true
        mensajeError = null
        // Al crear la cuenta, Firebase deja la sesión iniciada con ese usuario.
        repositorio.registrar(correoLimpio, clave) { excepcion ->
            cargando = false
            if (excepcion == null) {
                actualizarUsuario()
            } else {
                mensajeError = traducirError(excepcion)
            }
        }
    }

    fun cerrarSesion() {
        repositorio.cerrarSesion()
        actualizarUsuario()
    }

    fun limpiarError() {
        mensajeError = null
    }

    private fun actualizarUsuario() {
        uid = repositorio.usuarioActual?.uid
        correo = repositorio.usuarioActual?.email
    }

    // Validaciones locales antes de llamar a Firebase.
    private fun validar(correo: String, clave: String): String? {
        if (correo.isEmpty() || clave.isEmpty()) {
            return "Ingresa tu correo y tu contraseña."
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(correo).matches()) {
            return "El correo no tiene un formato válido."
        }
        if (clave.length < 6) {
            return "La contraseña debe tener al menos 6 caracteres."
        }
        return null
    }

    // Convierte los errores de Firebase en mensajes claros para el usuario.
    private fun traducirError(error: Exception): String {
        return when (error) {
            is FirebaseAuthWeakPasswordException ->
                "La contraseña debe tener al menos 6 caracteres."
            is FirebaseAuthInvalidCredentialsException ->
                "Correo o contraseña incorrectos."
            is FirebaseAuthInvalidUserException ->
                "No existe una cuenta con ese correo."
            is FirebaseAuthUserCollisionException ->
                "Ya existe una cuenta registrada con ese correo."
            is FirebaseNetworkException ->
                "Sin conexión a internet. Inténtalo de nuevo."
            else ->
                "Ocurrió un error: " + (error.localizedMessage ?: "desconocido")
        }
    }
}
