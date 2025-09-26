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

## SikaCare

SikaCare es una aplicación móvil de inteligencia artificial creada para guiar a cualquier persona en situaciones de emergencia médica, ofreciendo instrucciones claras, visuales y seguras en tiempo real. Imagina el peor escenario: un niño se atraganta y cada segundo cuenta. SikaCare permite acceder a una animación paso a paso de la maniobra correcta, diseñada para ser seguida en la urgencia y salvar vidas antes de que llegue la ambulancia.

![Kotlin](https://img.shields.io/badge/Kotlin-Multiplatform-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white)
![Compose](https://img.shields.io/badge/Compose-Multiplatform-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white)
![Health](https://img.shields.io/badge/Health-Emergency-FF6B6B?style=for-the-badge&logo=health&logoColor=white)

### 🚨 Funcionalidades Principales

- **Primeros Auxilios**: Guías animadas y visuales para actuar correctamente en emergencias
- **Agendar Citas Médicas**: Solicita turnos sin esperas telefónicas
- **Consultas Virtuales**: Atención médica remota desde una plataforma segura
- **Preclasificación de Enfermedades**: Orientación inicial sobre síntomas
- **Noticias y Consejos**: Información confiable y educativa en salud
- **Clínicas Móviles**: Consulta rutas y horarios en tu comunidad
- **Accesibilidad**: Modo lectura en voz alta para personas con discapacidad visual
- **Recordatorios de Salud**: Notificaciones para medicamentos y citas
- **Registro de Salud**: Calcula el IMC y recibe recomendaciones personalizadas
- **Calendario de Ferias y Jornadas**: Mantente conectado con actividades preventivas del MINSA

### 🏥 Misión

La app no gestiona expedientes clínicos profesionales, pero se inspira en los lineamientos de la Normativa 004 del MINSA, garantizando que la información proporcionada sea confiable y coherente con los protocolos de salud. SikaCare es una herramienta de apoyo accesible, práctica y segura para actuar con rapidez ante emergencias.

Más allá de primeros auxilios, SikaCare promueve la prevención y el acceso a la salud para todos, apoyando los ejes del Plan Nacional de Lucha contra la Pobreza y el Desarrollo Humano. Utiliza tecnología innovadora y fomenta la formación comunitaria para salvar vidas y reducir riesgos en poblaciones vulnerables.

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

### 🎨 Características Técnicas

- **UI Moderna**: Interfaz desarrollada con Jetpack Compose
- **Splash Screen**: Pantalla de carga nativa para cada plataforma
- **Autenticación**: Pantalla de login con integración social
- **Animaciones**: Transiciones fluidas y efectos visuales
- **Material Design 3**: Sistema de diseño consistente
- **Accesibilidad**: Soporte completo para lectores de pantalla

### 📊 Estado del Desarrollo

Actualmente, el proyecto incluye la implementación del Splash Screen y la pantalla de Login. Las demás funcionalidades están planificadas y se irán integrando en futuras versiones.

**Versión Actual:**
- ✅ Splash Screen multiplataforma
- ✅ Pantalla de Login con autenticación social
- 🔄 En desarrollo: Guías de primeros auxilios
- 📋 Planificado: Sistema de citas médicas
- 📋 Planificado: Consultas virtuales



### 👨‍💻 Autor

Desarrollado por **ZeroGravity**.

---

**SikaCare no compite con el sistema público, lo complementa y potencia, acercando el conocimiento médico al hogar y convirtiendo la incertidumbre en acción. Apostar por SikaCare es apostar por un país más preparado, seguro y equitativo en el acceso a la salud.**


