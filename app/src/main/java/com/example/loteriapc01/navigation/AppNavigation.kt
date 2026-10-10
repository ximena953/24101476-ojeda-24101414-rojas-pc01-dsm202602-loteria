package com.example.loteriapc01.navigation

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import com.example.loteriapc01.ui.screens.JugadaScreen
import com.example.loteriapc01.ui.screens.LoginScreen
import com.example.loteriapc01.ui.screens.RegistroScreen
import com.example.loteriapc01.ui.screens.SeguimientoScreen
import com.example.loteriapc01.viewmodel.AuthViewModel
import com.example.loteriapc01.viewmodel.SorteoViewModel

/** Pantallas de la aplicación. */
enum class Pantalla {
    LOGIN,
    REGISTRO,
    JUGADA,
    SEGUIMIENTO
}

/**
 * Navegación sencilla: una variable de estado indica qué pantalla se muestra.
 * Sin sesión solo se puede estar en Login o Registro; con sesión, en Jugada o Seguimiento.
 */
@Composable
fun AppNavigation(
    authViewModel: AuthViewModel,
    sorteoViewModel: SorteoViewModel
) {
    var pantalla by rememberSaveable { mutableStateOf(Pantalla.LOGIN) }

    val uid = authViewModel.uid
    val correo = authViewModel.correo ?: ""

    val cerrarSesion = {
        sorteoViewModel.limpiar()
        authViewModel.cerrarSesion()
        pantalla = Pantalla.LOGIN
    }

    // Botón "atrás" del teléfono: vuelve a la pantalla anterior en lugar de cerrar la app.
    BackHandler(enabled = pantalla == Pantalla.REGISTRO || pantalla == Pantalla.SEGUIMIENTO) {
        if (pantalla == Pantalla.REGISTRO) {
            authViewModel.limpiarError()
            pantalla = Pantalla.LOGIN
        } else {
            pantalla = Pantalla.JUGADA
        }
    }

    if (uid == null) {
        // Usuario sin sesión.
        if (pantalla == Pantalla.REGISTRO) {
            RegistroScreen(
                authViewModel = authViewModel,
                irALogin = {
                    authViewModel.limpiarError()
                    pantalla = Pantalla.LOGIN
                }
            )
        } else {
            LoginScreen(
                authViewModel = authViewModel,
                irARegistro = {
                    authViewModel.limpiarError()
                    pantalla = Pantalla.REGISTRO
                }
            )
        }
    } else {
        // Usuario autenticado: primero su jugada y luego el seguimiento.
        if (pantalla == Pantalla.SEGUIMIENTO) {
            SeguimientoScreen(
                correo = correo,
                uid = uid,
                sorteoViewModel = sorteoViewModel,
                volverAJugada = { pantalla = Pantalla.JUGADA },
                cerrarSesion = cerrarSesion
            )
        } else {
            JugadaScreen(
                correo = correo,
                uid = uid,
                sorteoViewModel = sorteoViewModel,
                irASeguimiento = { pantalla = Pantalla.SEGUIMIENTO },
                cerrarSesion = cerrarSesion
            )
        }
    }
}
