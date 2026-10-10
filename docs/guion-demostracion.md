# Guion de demostración — PC01 Lotería

## Antes de grabar

1. En Firebase Console → Firestore, confirmar que `sorteos/sorteo_prueba` tiene
   `estado = "abierto"` y que la subcolección `bolillas` está vacía.
2. Abrir la app en dos sesiones (dos emuladores, o emulador y teléfono).
3. Sesión 1: iniciar sesión con el **Usuario A** y registrar la jugada **5, 12, 18, 23, 34, 41**.
4. Sesión 2: iniciar sesión con el **Usuario B** y registrar la jugada **5, 12, 18, 23, 34, 42**.
5. En ambas sesiones pulsar **Ir al seguimiento del sorteo**.
   Debe verse "Esperando el inicio del sorteo", 0 bolillas y 0 aciertos.

Si una cuenta ya tiene jugada de una prueba anterior, no se puede cambiar desde la app:
hay que borrar su documento en `sorteos/sorteo_prueba/jugadas` desde la consola o usar
una cuenta nueva.

## Durante la grabación

| Paso | Acción en Firebase Console | Qué debe verse en ambas pantallas |
|------|----------------------------|-----------------------------------|
| 1 | Cambiar `estado` de `abierto` a `en curso` | "Sorteo en curso" |
| 2 | Agregar documento en `bolillas`: `numero = 5`, `orden = 1` | Bolilla 5 · 1 acierto |
| 3 | `numero = 12`, `orden = 2` | 2 aciertos |
| 4 | `numero = 18`, `orden = 3` | 3 aciertos |
| 5 | `numero = 23`, `orden = 4` | 4 aciertos |
| 6 | `numero = 34`, `orden = 5` | 5 aciertos en A y en B, **sin resultado definitivo** |
| 7 | `numero = 41`, `orden = 6` | A: 6 aciertos · B: 5 aciertos, todavía sin resultado |
| 8 | Cambiar `estado` a `finalizado` | A: **GANADOR** · B: **SIGUE INTENTANDO** |
| 9 | Cerrar y volver a abrir la app en una sesión | Se recuperan jugada y bolillas y se recalcula el resultado |

Cada bolilla se agrega como un documento nuevo (id automático) con los campos
`numero` y `orden` de tipo **number**. Esperar a verla en las dos pantallas antes de
publicar la siguiente.

## Código para mostrar y explicar

- **Escucha de cambios:** `data/repository/SorteoRepository.kt`
  (`escucharSorteo`, `escucharBolillas`, `escucharJugada` con `addSnapshotListener`).
- **Gestión de listeners:** `viewmodel/SorteoViewModel.kt`
  (`iniciarEscucha`, `detenerEscucha`, `onCleared`) y el `DisposableEffect` de
  `ui/screens/SeguimientoScreen.kt`.
- **Cálculos:** `viewmodel/CalculoSorteo.kt`
  (`numerosAcertados`, `hayResultadoDefinitivo`, `resultadoFinal`).
- **Autenticación:** `data/repository/AuthRepository.kt` y `viewmodel/AuthViewModel.kt`.
- **Validaciones de la jugada:** `SorteoViewModel.validarJugada` y las reglas de Firestore.
