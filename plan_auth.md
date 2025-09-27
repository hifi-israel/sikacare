---

## 22) Configuración y secretos (equivalente a .env en KMP)

### 22.1 Principios
- En aplicaciones cliente (Android/iOS/Desktop) ningún “secreto” es realmente secreto dentro del binario. No incluyas claves sensibles de servidor (p. ej. `service_role`).
- La `anon key` de Supabase puede vivir en el cliente porque la seguridad la impone RLS en la base de datos.
- Usa constantes de compilación y variables por entorno para no hardcodear valores en el código fuerte.

### 22.2 Opción recomendada: BuildKonfig (Kotlin Multiplatform)
- Genera una clase `BuildKonfig` con constantes por buildType y por plataforma.
- Configuración (referencial) en `composeApp/build.gradle.kts`:
```kotlin
plugins {
    // ...
    id("com.codingfeline.buildkonfig") version "<latest>"
}

buildkonfig {
    packageName = "com.israeljuarez.sikacorekmp.config"

    defaultConfigs {
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "SUPABASE_URL",
            providers.gradleProperty("SUPABASE_URL").orNull ?: ""
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "SUPABASE_ANON_KEY",
            providers.gradleProperty("SUPABASE_ANON_KEY").orNull ?: ""
        )
        buildConfigField(
            com.codingfeline.buildkonfig.compiler.FieldSpec.Type.STRING,
            "WEB_GOOGLE_CLIENT_ID",
            providers.gradleProperty("WEB_GOOGLE_CLIENT_ID").orNull ?: ""
        )
    }
}
```
- En código KMP (common):
```kotlin
import com.israeljuarez.sikacorekmp.config.BuildKonfig

val supabase = createSupabaseClient(
    supabaseUrl = BuildKonfig.SUPABASE_URL,
    supabaseKey = BuildKonfig.SUPABASE_ANON_KEY
) { /* Auth + ComposeAuth con WEB_GOOGLE_CLIENT_ID */ }
```

### 22.3 Dónde colocar los valores (alternativas)
- `gradle.properties` (en el proyecto o en `~/.gradle/gradle.properties`):
```
SUPABASE_URL=https://xxxx.supabase.co
SUPABASE_ANON_KEY=eyJhbGciOi...
WEB_GOOGLE_CLIENT_ID=1234567890-abcdef.apps.googleusercontent.com
```
- Variables de entorno en CI/CD (inyectadas a `gradle.properties`).
- `local.properties` (Android) es útil para config local “de máquina” y está git ignored, pero suele reservarse para SDK paths. Si lo usas para secretos locales, no olvides mapearlos hacia `buildkonfig`/`gradle.properties` en tu script.

### 22.4 Android/iOS/Desktop
- **Android**: También puedes usar `BuildConfig` (pero no es KMP). BuildKonfig unifica el acceso desde `commonMain`.
- **iOS**: Puedes definir valores en `.xcconfig`, pero con BuildKonfig no hace falta duplicar.
- **Desktop (JVM)**: Para dev, puedes usar variables de entorno o un archivo `.env` local fuera del repo y exportarlas a Gradle.

### 22.5 Buenas prácticas
- No subas claves a Git. Usa `gradle.properties` local o variables de entorno y CI para release.
- Nunca incluir `service_role` en el cliente. Sólo `anon key`.
- Mantén los logs limpios de credenciales.

---

## 23) Plan de vistas Onboarding y Home

### 23.1 OnboardingScreen (pasarela inicial)
- **Objetivo**: Presentar características clave y, al finalizar, marcar `profiles.is_onboarding_seen = true`.
- **Estructura**:
  - Paginador con 3–4 slides (título + texto + ilustración).
  - Botones: “Siguiente” y “Comenzar” (en la última página).
  - Opción “Saltar” para usuarios avanzados (opcional).
- **Acciones**:
  - Al pulsar “Comenzar” (o “Saltar”):
    - Ejecutar `update profiles set is_onboarding_seen = true where user_id = auth.uid()`.
    - Navegar a `Home`.
- **Estados**:
  - `isLoading` durante la actualización en DB.
  - Manejo offline: si no hay conexión, guardar intención local y re intentar cuando vuelva Internet.

### 23.2 HomeScreen (pantalla principal mínima viable)
- **Objetivo**: Ser el destino por defecto tras login/onboarding.
- **Estructura**:
  - Encabezado con `full_name` y `avatar` (si existe) desde `profiles`.
  - Acciones principales (placeholders): “Primeros Auxilios”, “Citas”, “Consultas”, etc.
  - Menú con “Cerrar sesión”.
- **Acciones**:
  - Al abrir, cargar `profiles` (cachear en ViewModel) para mostrar nombre/avatar.
  - “Cerrar sesión” → `supabase.auth.signOut()` → navegar a `Login`.

### 23.3 Navegación e integración
- En `App.kt` (o tu nav host), definir rutas: `Splash`, `Login`, `Register`, `ForgotPassword`, `Onboarding`, `Home`.
- Splash decide ruta con `loadFromStorage()` y `is_onboarding_seen` (ver sección 12 y 20).
- Onboarding actualiza flag y navega a Home.
- Home mantiene sesión y ofrece logout.

### 23.4 MVVM y estados
- **OnboardingViewModel**:
  - Estado: `pageIndex`, `isLoading`, `error`.
  - Acción `finishOnboarding()` → actualiza flag y emite `NavigateTo(Home)`.
- **HomeViewModel**:
  - Estado: `profile`, `isLoading`, `error`.
  - Acción `loadProfile()` y `logout()`.

### 23.5 Checklist UI
- Onboarding:
  - Se muestra sólo si `is_onboarding_seen = false`.
  - “Comenzar” marca flag y redirige a Home.
- Home:
  - Muestra nombre/avatar si existen.
  - Botón “Cerrar sesión” funcional.

### 24.5 Verificación de correo en Onboarding
- Supabase envía el correo al registrarse (si está habilitado). La verificación se completa al abrir el enlace.
- En Onboarding:
  - Mostrar `email` y `emailConfirmedAt != null`.
  - "Reenviar verificación" → reenvío vía supabase-kt.
  - "Ya confirmé" → refrescar usuario/sesión para revalidar estado.

### 24.6 Escritura en base de datos (al pulsar "Comenzar")
```sql
update public.profiles
set gender = '<M|F|O>',
    birthdate = '<YYYY-MM-DD>',
    is_onboarding_seen = true,
    updated_at = now()
where user_id = auth.uid();
```

### 24.7 Criterios de aceptación del Sprint 1
- Con sesión restaurada y `is_onboarding_seen = true` → entra directo a `Home` (no `Login`).
- Primer inicio tras registro (email confirmado): Onboarding pide género/fecha y "Comenzar" navega a `Home`.
- Si el email no está confirmado, Onboarding bloquea "Comenzar" y permite reintentar verificación.
- "Cerrar sesión" en `Home` retorna a `Login`.

### 24.8 Pasos para implementación
1) Crear `Splash`, `Onboarding`, `Home` (placeholders) y agregar rutas en `App.kt`.
2) Implementar `Splash` con `loadFromStorage()` y ruteo.
3) Implementar `OnboardingViewModel` y UI con: verificación, género, fecha, resumen.
4) Conectar `finishOnboarding()` con actualización en `public.profiles` y navegar a `Home`.
5) Implementar `HomeViewModel` (`loadProfile()` y `logout()`).
6) Prueba E2E: registro → confirmar email → login → Onboarding → Home → cerrar y abrir (restaura sesión → Home).
