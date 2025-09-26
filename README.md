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

## SikaCore

Este proyecto es una implementación con Kotlin Multiplatform (KMP) de una aplicación de autenticación moderna. Combina prácticas de desarrollo multiplataforma con una interfaz elegante y funcional.

![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Compose-Multiplatform-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)

### Aprende a PROGRAMAR aplicaciones en Kotlin Multiplataforma

**Temario:**
- Capítulo 1 - Configuración del proyecto KMP
- Capítulo 2 - Implementación del Splash Screen
- Capítulo 3 - Pantalla de Login compartida
- Capítulo 4 - Integración con autenticación social
- Capítulo 5 - Animaciones y transiciones
- Capítulo 6 - Recursos compartidos
- Capítulo 7 - Configuración multiplataforma

### 🛠 Instalación

**Requisitos:**
- Kotlin 2.0.21+
- Compose Multiplatform 1.7.0+
- IDE compatible con KMP: Android Studio o Fleet
- Conocimientos básicos de Kotlin y KMP

**Configuración:**
1. Abre el proyecto en Android Studio
2. Sincroniza el proyecto Gradle para descargar las dependencias
3. Ejecuta la aplicación en tu plataforma preferida

### ✏️ Estructura del proyecto

**Componentes:**
Puedes acceder a `composeApp/src/commonMain/kotlin/com/israeljuarez/sikacorekmp/` para acceder a todos los componentes compartidos. Se han programado de manera genérica para poder personalizarlos y reutilizarlos en cualquier plataforma.

```kotlin
// Pantalla de Login compartida
@Composable
fun LoginScreen() {
    // UI compartida para Android, iOS y Desktop
}
```

**Recursos:**
Desde `composeApp/src/commonMain/composeResources/` dispones de todos los recursos compartidos entre plataformas.

**Splash Screen:**
Primera vista del proyecto que se lanzará siempre que se ejecute la app. Implementada de manera nativa para cada plataforma:
- **Android**: `androidx.core:splashscreen`
- **iOS**: Launch Screen con Storyboard  
- **Desktop**: Splash in-app con Compose

### 🌍 Plataformas Soportadas

- **Android** (API 24+)
- **iOS** (iOS 13+)
- **Desktop** (Windows, macOS, Linux)

### 🎨 Características

- **UI Moderna**: Interfaz desarrollada con Jetpack Compose
- **Splash Screen**: Pantalla de carga nativa para cada plataforma
- **Autenticación**: Pantalla de login con integración social
- **Animaciones**: Transiciones fluidas y efectos visuales
- **Material Design 3**: Sistema de diseño consistente



### 👨‍💻 Autor

Desarrollado por **ZeroGravity**.


