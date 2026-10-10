// Build a nivel de proyecto: solo declara los plugins y sus versiones.
// Se aplican de verdad en el módulo :app (app/build.gradle.kts).
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.google.services) apply false
}
