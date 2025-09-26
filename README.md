This is a Kotlin Multiplatform project targeting Android, iOS, Desktop (JVM).

* [/composeApp](./composeApp/src) is for code that will be shared across your Compose Multiplatform applications.
  It contains several subfolders:
  - [commonMain](./composeApp/src/commonMain/kotlin) is for code that’s common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the platform indicated in the folder name.
    For example, if you want to use Apple’s CoreCrypto for the iOS part of your Kotlin app,
    the [iosMain](./composeApp/src/iosMain/kotlin) folder would be the right place for such calls.
    Similarly, if you want to edit the Desktop (JVM) specific part, the [jvmMain](./composeApp/src/jvmMain/kotlin)
    folder is the appropriate location.

* [/iosApp](./iosApp/iosApp) contains iOS applications. Even if you’re sharing your UI with Compose Multiplatform,
  you need this entry point for your iOS app. This is also where you should add SwiftUI code for your project.

### Build and Run Android Application

To build and run the development version of the Android app, use the run configuration from the run widget
in your IDE’s toolbar or build it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:assembleDebug
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:assembleDebug
  ```

### Build and Run Desktop (JVM) Application

To build and run the development version of the desktop app, use the run configuration from the run widget
in your IDE’s toolbar or run it directly from the terminal:
- on macOS/Linux
  ```shell
  ./gradlew :composeApp:run
  ```
- on Windows
  ```shell
  .\gradlew.bat :composeApp:run
  ```

### Build and Run iOS Application

To build and run the development version of the iOS app, use the run configuration from the run widget
in your IDE’s toolbar or open the [/iosApp](./iosApp) directory in Xcode and run it from there.

---

Learn more about [Kotlin Multiplatform](https://www.jetbrains.com/help/kotlin-multiplatform-dev/get-started.html)…


---

## Actualización (ES): Splash Screen 

Esta sección documenta en español cómo implementamos y configuramos la pantalla de carga inicial (Splash) que aparece antes de la pantalla de Login, en las tres plataformas del proyecto (Android, iOS y Desktop), sin modificar el contenido por defecto anterior de este README.

### Resumen
- Propósito: mostrar un primer frame inmediato y consistente mientras la app termina de inicializarse, y luego transicionar al contenido (actualmente la vista de Login compartida).
- Color de fondo: #89C1EA (coherente con el diseño del Login).
- Logo: `logo.png` (mantenido por plataforma donde aplica o como recurso compartido para Desktop).

### Android (nativo con androidx.core:splashscreen)
- Dependencia agregada: `androidx.core:core-splashscreen:1.0.1`.
- Tema de Splash: definido en `composeApp/src/androidMain/res/values/themes.xml` como `Theme.App.Splash`, heredando de `Theme.SplashScreen`.
    - `windowSplashScreenBackground` → `@color/splash_background` (#89C1EA) definido en `res/values/colors.xml`.
    - `windowSplashScreenAnimatedIcon` → `@drawable/logo` (coloca tú `logo.png` en `composeApp/src/androidMain/res/drawable/` con ese nombre).
    - `postSplashScreenTheme` → `@style/Theme.App`.
- Manifest: `MainActivity` usa el tema `Theme.App.Splash` para que el sistema muestre el splash al lanzar.
- Código: en `MainActivity.kt` se invoca `installSplashScreen()` al inicio de `onCreate()`.
- Duración del splash: el sistema lo gestiona automáticamente; opcionalmente se puede controlar con `splash.setKeepOnScreenCondition { ... }` si coordinas con un estado de carga.

Archivos clave:
- `composeApp/src/androidMain/res/values/colors.xml`
- `composeApp/src/androidMain/res/values/themes.xml`
- `composeApp/src/androidMain/AndroidManifest.xml`
- `composeApp/src/androidMain/kotlin/com/israeljuarez/sikacorekmp/MainActivity.kt`
- `composeApp/src/androidMain/res/drawable/logo.png`

### iOS (Launch Screen nativa)
- Launch Screen: `iosApp/iosApp/LaunchScreen.storyboard` con fondo #89C1EA y una `UIImageView` centrada que muestra la imagen `logo`.
- Configuración: `Info.plist` define `UILaunchStoryboardName = LaunchScreen`.
- Recursos: `iosApp/iosApp/Assets.xcassets/logo.imageset/logo.png` (puedes añadir @2x/@3x si deseas mayor nitidez).
- Duración del splash: iOS la gestiona; se muestra hasta que el primer frame de la app esté listo (no hay temporizador configurable).

Archivos clave:
- `iosApp/iosApp/LaunchScreen.storyboard`
- `iosApp/iosApp/Info.plist`
- `iosApp/iosApp/Assets.xcassets/logo.imageset/`

### Desktop (in‑app Splash con Compose)
- Implementación: se muestra un splash simple previo a `App()` mientras la app “arranca”.
- Dónde: `composeApp/src/jvmMain/kotlin/com/israeljuarez/sikacorekmp/main.kt`.
    - Ventana `Window` con `icon = painterResource(Res.drawable.logo)`.
    - `DesktopSplash()` muestra un `Box` de fondo #89C1EA y el `logo` centrado.
    - Control de duración: `delay(800)` en un `LaunchedEffect` (ajustable, en milisegundos).
- Recursos: usa el recurso compartido `composeApp/src/commonMain/composeResources/drawable/logo.png` expuesto como `Res.drawable.logo`.

Archivos clave:
- `composeApp/src/jvmMain/kotlin/com/israeljuarez/sikacorekmp/main.kt`
- `composeApp/src/commonMain/composeResources/drawable/logo.png`

### Flujo de arranque y transición al Login
1. Al abrir la app:
    - Android: el sistema muestra el splash nativo (tema + drawable) inmediatamente.
    - iOS: el sistema muestra la Launch Screen del storyboard.
    - Desktop: se presenta `DesktopSplash()`.
2. Una vez listo el primer frame/contenido:
    - Android/iOS: el sistema oculta el splash y se renderiza `App()` (que actualmente muestra `LoginScreen()`).
    - Desktop: tras `delay(800)`, `showSplash = false` y se renderiza `App()`.

### Notas y buenas prácticas
- Mantén el splash minimalista: color de fondo + logo estático; evita texto/animaciones complejas en la pantalla nativa de arranque.
- Para mostrar texto o más contenido al inicio, hazlo en una “pantalla de splash in‑app” dentro de Compose (posterior al splash del sistema), si fuera necesario.
- Alinea los recursos (logo y color) entre plataformas para consistencia visual.

### Próximos pasos (opcionales)
- Android: coordinar `setKeepOnScreenCondition` con un estado de inicialización real para una transición más suave.
- iOS: añadir variantes @2x/@3x al asset `logo` para pantallas Retina.
- Desktop: ajustar el `delay` del splash o sustituirlo por un estado de inicialización real (carga de configuración/recursos) en lugar de un tiempo fijo.


---

## Actualización (ES): Pantalla de Login compartida con Compose Multiplatform

Esta sección documenta en español la primera vista compartida (Login) implementada en `commonMain` para Android, iOS y Desktop, sin modificar el contenido por defecto anterior de este README.

### Qué se implementó
- Vista de Login 100% compartida (KMP) usando Compose:
  - Fondo azul corporativo `#89C1EA` a pantalla completa.
  - Contenedor blanco con borde superior redondeado, dibujado con `Canvas + Path`.
  - Animación de entrada: el contenedor (y su contenido) suben desde abajo (600.dp) hasta su posición final (0.dp) con `animateDpAsState` y `FastOutSlowInEasing` (duración ~600 ms).
  - Contenido de la vista (solo UI): título, subtítulo, campos de email/contraseña, botón “Ingresar”, separador y botones sociales.
  - Botón social de Google con icono oficial desde recursos compartidos. El de Facebook queda pendiente hasta recibir el asset oficial.

### Archivos y rutas principales
- Pantalla de login (UI compartida):
  - `composeApp/src/commonMain/kotlin/com/israeljuarez/sikacorekmp/login/LoginScreen.kt`
- Punto de entrada compartido:
  - `composeApp/src/commonMain/kotlin/com/israeljuarez/sikacorekmp/App.kt` (invoca `LoginScreen()` dentro de `MaterialTheme`).
- Recursos compartidos (Compose resources):
  - Carpeta: `composeApp/src/commonMain/composeResources/drawable/`
  - Icono de Google: `google_logo.png`
    - Nota: si tu archivo original se llama `android_dark_rd_na@2x.png` (descargado del paquete oficial), fue renombrado a `google_logo.png` para generar un identificador estable en Compose (`Res.drawable.google_logo`). No se alteraron colores ni forma del logotipo.

### Cómo ajustar la animación
- Offset inicial del contenedor: `containerHiddenOffset = 600.dp`.
- Offset final: `containerTargetOffset = 0.dp`.
- Duración y curva: `tween(durationMillis = 600, easing = FastOutSlowInEasing)`.
- Ubicación del código: en `LoginScreen()` dentro de `LoginScreen.kt`.

### Estado actual de los botones sociales
- Google: usando el logo oficial (icono + texto “Continuar con Google”), respetando guías de marca.
- Facebook: pendiente de asset oficial. El botón se muestra por ahora sin icono hasta agregar `facebook_logo.png` a recursos compartidos.

### Notas de cumplimiento de marca
- Los logos de Google y Facebook/Meta son marcas registradas. Pueden usarse en botones de “Continuar/Acceder con …” siguiendo las guías oficiales de cada proveedor (sin recolorear ni modificar proporciones del logo).
- Recomendación: mantener los textos sugeridos (por ejemplo, “Continuar con Google”) y alturas mínimas de 48 dp (Android/Desktop) o 44 pt (iOS).

### Splash y tiempos (contexto)
- Android: splash nativo con `androidx.core:splashscreen` (fondo `#89C1EA` + `@drawable/logo`). La duración puede sincronizarse opcionalmente con `setKeepOnScreenCondition` en `MainActivity`.
- iOS: Launch Screen nativa (estática) configurada con `LaunchScreen.storyboard`. No hay control de tiempo: se muestra hasta el primer frame.
- Desktop: splash in‑app simple en `main.kt` con un `delay(800)` ajustable.

### Próximos pasos sugeridos
- Añadir `facebook_logo.png` en `composeApp/src/commonMain/composeResources/drawable/` y conectar el icono en el botón de Facebook.
- Internacionalización (i18n) de los textos del Login (por ejemplo, cambiar idioma desde la app).
- Integración futura de SDKs de autenticación por plataforma (Google/Facebook) o flujos OAuth donde corresponda.
- Accesibilidad: descripciones de contenido, contraste y tamaños táctiles mínimos.


