# Lotería PC01

Práctica Calificada 01 — Desarrollo de Aplicaciones Móviles (ESAN, 2026-02).

Aplicación Android en Kotlin y Jetpack Compose que permite autenticarse, registrar una
jugada de seis números y seguir un sorteo en tiempo real con Firebase Authentication y
Cloud Firestore.

## Integrantes

| Código   | Nombre                         |
|----------|--------------------------------|
| 24101476 | Flavia Camila Ojeda Rodas      |
| 24101414 | Rojas _(completar nombres)_    |

## Requisitos

- Android Studio con el JDK incluido (el proyecto usa Gradle 9.6.0, AGP 8.7.2 y Kotlin 2.0.21).
- Emulador o dispositivo con Android 8.0 (API 26) o superior y conexión a internet.
- Proyecto Firebase `24101414-24101476-pc1-dsm2026` (id `pc1-dsm2026`) con
  Authentication (correo y contraseña) y Cloud Firestore habilitados.

## Cómo ejecutar

1. Abrir la carpeta `LoteriaPC01` en Android Studio y esperar el Gradle Sync.
2. Verificar que exista `app/google-services.json` (app Android `com.example.loteriapc01`).
3. Iniciar un emulador desde Device Manager y pulsar **Run ▶**.
4. Para la demostración se necesitan dos sesiones: dos emuladores, o un emulador y un
   teléfono, cada uno con una cuenta distinta.

## Estructura del código

```
app/src/main/java/com/example/loteriapc01/
├── MainActivity.kt                 Crea los ViewModel y arranca Compose
├── navigation/AppNavigation.kt     Navegación entre las cuatro pantallas
├── data/model/                     Sorteo, BolillaPublicada, Jugada
├── data/repository/
│   ├── AuthRepository.kt           Firebase Authentication
│   └── SorteoRepository.kt         Cloud Firestore (lecturas, listeners y jugada)
├── viewmodel/
│   ├── AuthViewModel.kt            Sesión, validaciones y mensajes de error
│   ├── SorteoViewModel.kt          Listeners en tiempo real y registro de jugada
│   └── CalculoSorteo.kt            Aciertos y resultado (GANADOR / SIGUE INTENTANDO)
└── ui/
    ├── screens/                    Login, Registro, Jugada, Seguimiento
    ├── components/                 Bolilla, NumeroSeleccionable, BarraUsuario, TarjetaSorteo
    └── theme/                      Colores y tema Material 3
```

## Datos en Firestore

```
sorteos/sorteo_prueba                    identificador, nombre, fecha (Timestamp), estado
sorteos/sorteo_prueba/bolillas/{id}      numero (number), orden (number, 1 a 6)
sorteos/sorteo_prueba/jugadas/{uid}      sorteoId, uid, numeros (lista de 6 enteros)
```

- `estado` toma los valores `abierto`, `en curso` o `finalizado`.
- El id del documento de la jugada es el UID del usuario: solo puede existir una jugada
  por usuario y cada usuario lee únicamente la suya.
- Los textos GANADOR y SIGUE INTENTANDO **no** se guardan en Firestore: la app los calcula.

## Cómo funciona

- **Autenticación:** `AuthRepository` llama a `createUserWithEmailAndPassword`,
  `signInWithEmailAndPassword` y `signOut`. Firebase recuerda la sesión al reabrir la app.
- **Persistencia:** la jugada se crea con `set()` en `jugadas/{uid}`. Las reglas de
  seguridad solo aceptan la creación si el sorteo está `abierto`, si los seis números son
  enteros distintos entre 1 y 45 y si el documento pertenece al usuario autenticado.
  No se permite modificar ni borrar.
- **Tiempo real:** `SorteoRepository` usa `addSnapshotListener` sobre el sorteo, las
  bolillas y la jugada. No hay consultas periódicas.
- **Gestión de listeners:** cada pantalla llama a `iniciarEscucha` al abrirse y a
  `detenerEscucha` en `onDispose`. `iniciarEscucha` quita primero los listeners
  anteriores, así no quedan duplicados; `onCleared` los quita si el ViewModel se destruye.
- **Cálculos:** `CalculoSorteo.kt` cuenta los números de la jugada presentes entre las
  bolillas (sin importar el orden) y solo entrega el resultado definitivo cuando el estado
  es `finalizado` y hay seis bolillas.

## Demostración

El guion paso a paso está en [`docs/guion-demostracion.md`](docs/guion-demostracion.md).

## Entrega

Nombre del repositorio (trabajo en pares):
`24101476-ojeda-24101414-rojas-pc01-dsm202602-loteria`
