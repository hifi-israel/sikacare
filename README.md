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
### 📱 Guía de Usuario Rápida

**Primeros Pasos:**
1. Descarga e instala la aplicación SikaCare desde tu tienda de aplicaciones
2. Abre la app y permite los permisos necesarios para un funcionamiento óptimo
3. Regístrate o inicia sesión para acceder a todas las funcionalidades

**Uso Básico:**
- **Emergencias**: Toca "Primeros Auxilios" para guías visuales paso a paso
- **Citas**: Usa "Agendar Cita" para solicitar turnos médicos sin llamadas
- **Consultas**: Accede a "Consultas Virtuales" para atención remota
- **Salud**: Registra tu peso y altura en "Mi Salud" para calcular tu IMC
- **Recordatorios**: Configura notificaciones en "Recordatorios" para medicamentos y citas
- **Accesibilidad**: Activa el modo de lectura en voz alta en la configuración

**Navegación:**
- Desliza hacia la izquierda/derecha para navegar entre secciones
- Usa el menú inferior para acceso rápido a funciones principales
- Consulta "Noticias y Consejos" para información actualizada de salud

### 🏥 Misión

SikaCare no compite con el sistema público, lo complementa y lo potencia, acerca el conocimiento médico al hogar y convierte la incertidumbre en acción. Apostar por SikaCare es apostar por un país más preparado, más seguro y con mayor equidad en el acceso a la salud.

Desarrollado por **ZeroGravity**, SikaCare se convierte en una herramienta esencial para la comunidad.

### 🛠 Instalación

**Requisitos:**
- **Sistema Operativo**: Windows 10/11, macOS 12+, o Linux (Ubuntu 20.04+)
- **Memoria RAM**: 8 GB mínimo, 16 GB recomendado
- **Espacio en Disco**: 8 GB mínimo para Android Studio + SDKs
- **Java Development Kit (JDK)**: JDK 11 o superior (JDK 17 recomendado)
- **Gradle**: 8.14.3 (incluido en el proyecto)
- **IDE**: IntelliJ IDEA 2025.2.2 (Ultimate Edition) con soporte para Kotlin Multiplatform
**Dependencias Principales:**
- **Kotlin Multiplatform**: 2.2.20
- **Compose Multiplatform**: 1.9.0
- **Android Gradle Plugin**: 8.12.0
- **Android Compile SDK**: 36
- **Android Minimum SDK**: 24
- **AndroidX Activity Compose**: 1.11.0
- **AndroidX Lifecycle**: 2.9.4
- **Kotlinx Coroutines**: 1.10.2
- **Supabase**: 3.2.4
  - Auth-kt
  - Postgrest-kt
- **Ktor Client**: 3.0.3 (para comunicación HTTP)

**Instalación y Configuración:**

**Paso 1: Preparar el Entorno**
1. Descarga e instala IntelliJ IDEA 2025.2.2 Ultimate Edition desde [jetbrains.com](https://www.jetbrains.com/idea/)
2. Instala el JDK 17 desde [oracle.com](https://www.oracle.com/java/technologies/downloads/) o [adoptium.net](https://adoptium.net/)
3. Verifica la instalación ejecutando `java -version` en la terminal

**Paso 2: Configurar el Proyecto**
1. Clona o descarga el repositorio del proyecto
2. Abre IntelliJ IDEA y selecciona "Open" > "Open as Project"
3. Navega hasta la carpeta del proyecto y selecciónala
4. Espera a que Gradle sincronice automáticamente (puede tomar varios minutos)

**Paso 3: Descargar Dependencias**
1. Una vez abierto el proyecto, ve a "File" > "Project Structure"
2. Asegúrate de que el JDK configurado sea el JDK 17
3. Ve a "File" > "Sync Project with Gradle Files" para descargar todas las dependencias
4. Si es necesario, ejecuta `./gradlew build` (Linux/macOS) o `.\gradlew.bat build` (Windows)

**Paso 4: Ejecutar la Aplicación**
1. Para **Android**: Usa el botón "Run" en la barra superior o ejecuta `./gradlew :composeApp:assembleDebug`
2. Para **Desktop (JVM)**: Ejecuta `./gradlew :composeApp:run`
3. Para **iOS**: Abre el directorio `iosApp` en Xcode y ejecútalo desde allí

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

**Pantallas implementadas:**
- **LoginScreen**: Autenticación con email/contraseña y Google
- **RegisterScreen**: Registro de nuevos usuarios con validación
- **ForgotPasswordScreen**: Recuperación de contraseña vía email
- **ResetPasswordWithCodeScreen**: Restablecimiento con código de verificación
- **IntroScreen**: Onboarding con 3 pantallas de introducción
- **OnboardingScreen**: Completar perfil de usuario post-registro
- **SplashScreen**: Pantalla de carga nativa para cada plataforma

**Recursos:**
Desde `composeApp/src/commonMain/composeResources/` dispones de todos los recursos compartidos entre plataformas:
- Imágenes (logo.png, Imagen_vista1-3.png, google_logo.png)
- Iconos vectoriales (arrow_back/forward.xml, check.xml, person.xml, phone.xml, email.xml)
- Recursos generados automáticamente con Compose Resources

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

- **UI Moderna**: Interfaz desarrollada con Jetpack Compose Multiplatform
- **Splash Screen**: Pantalla de carga nativa para cada plataforma
- **Autenticación**: Sistema completo con Supabase Auth
  - Login con email/contraseña
  - Registro con validación de campos
  - Autenticación con Google (OAuth)
  - Recuperación de contraseña con código de verificación
  - Verificación de email
- **Base de Datos**: Integración con Supabase
  - Tabla `profiles` para datos de usuario
  - Tabla `avatars` para iconos predefinidos
  - RLS (Row Level Security) para protección de datos
  - Triggers automáticos para gestión de perfiles
- **Onboarding**: Sistema de introducción interactivo
  - 3 pantallas de bienvenida con animaciones
  - Completar perfil post-registro
  - Seguimiento con `is_onboarding_seen`
- **Animaciones**: Transiciones fluidas y efectos visuales
- **Material Design 3**: Sistema de diseño consistente
- **Accesibilidad**: Soporte completo para lectores de pantalla
- **Gestión de Estado**: ViewModels con StateFlow para arquitectura reactiva
- **Navegación**: Sistema de navegación entre pantallas con callbacks

### 📊 Estado del Desarrollo

El proyecto cuenta con un sistema de autenticación completo, onboarding interactivo y base de datos integrada. Las funcionalidades médicas principales están en desarrollo.

**Versión Actual (v0.2.0):**
- ✅ Splash Screen multiplataforma
- ✅ Sistema de autenticación completo (Supabase)
  - ✅ Login con email/contraseña
  - ✅ Registro con validación
  - ✅ Autenticación con Google OAuth
  - ✅ Recuperación de contraseña
  - ✅ Verificación de email
- ✅ Sistema de Onboarding
  - ✅ IntroScreen con 3 pantallas de bienvenida
  - ✅ OnboardingScreen para completar perfil
- ✅ Base de datos con Supabase
  - ✅ Gestión de perfiles de usuario
  - ✅ Sistema de avatares
  - ✅ RLS para seguridad
- 🔄 En desarrollo: Guías de primeros auxilios
- 📋 Planificado: Sistema de citas médicas
- 📋 Planificado: Consultas virtuales
- 📋 Planificado: Preclasificación de enfermedades



### 👨‍💻 Equipo de Desarrollo

Desarrollado por **ZeroGravity**.

---

**SikaCare no compite con el sistema público, lo complementa y potencia, acercando el conocimiento médico al hogar y convirtiendo la incertidumbre en acción. Apostar por SikaCare es apostar por un país más preparado, seguro y equitativo en el acceso a la salud.**


