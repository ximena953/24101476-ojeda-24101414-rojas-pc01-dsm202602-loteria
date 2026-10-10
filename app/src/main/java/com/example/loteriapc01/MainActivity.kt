package com.example.loteriapc01

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.loteriapc01.navigation.AppNavigation
import com.example.loteriapc01.ui.theme.LoteriaPC01Theme
import com.example.loteriapc01.viewmodel.AuthViewModel
import com.example.loteriapc01.viewmodel.SorteoViewModel

/**
 * Única Activity de la app. Crea los ViewModel y le entrega el control a Compose.
 */
class MainActivity : ComponentActivity() {

    // Los ViewModel sobreviven a los giros de pantalla.
    private val authViewModel: AuthViewModel by viewModels()
    private val sorteoViewModel: SorteoViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // La app dibuja también detrás de la barra de estado.
        setContent {
            LoteriaPC01Theme {
                AppNavigation(
                    authViewModel = authViewModel,
                    sorteoViewModel = sorteoViewModel
                )
            }
        }
    }
}
