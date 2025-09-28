# Configuración Google OAuth para SikaCare

## Problema Actual
Google OAuth está redirigiendo a `localhost` en lugar del deep link de la app, causando el error "No se puede acceder a este sitio".

## Solución: Configurar Supabase Dashboard

### 1. Supabase Dashboard → Authentication → URL Configuration

**Redirect URLs** (agregar estas URLs):
```
sikacare://auth-callback
http://localhost:3000/auth/callback
```

**Site URL**:
```
sikacare://auth-callback
```

### 2. Supabase Dashboard → Authentication → Providers → Google

**Authorized Client IDs** (agregar ambos):
```
933920856529-5gphgi299i3c6qmr76qkv5u7jah1jgbh.apps.googleusercontent.com
TU_ANDROID_CLIENT_ID_AQUI
```

**Client ID (for server)**: 
```
933920856529-5gphgi299i3c6qmr76qkv5u7jah1jgbh.apps.googleusercontent.com
```

**Client Secret**: 
```
[Tu Client Secret de Google Console]
```

### 3. Google Cloud Console → Credentials

**IMPORTANTE**: Google Console NO acepta URIs con esquemas personalizados como `sikacare://`. 
Solo agrega la URI de Supabase:

**Authorized redirect URIs** (agregar solo esta):
```
https://ywjkkpjixwoymqgswsmc.supabase.co/auth/v1/callback
```

**NO agregues** `sikacare://auth-callback` en Google Console (no lo acepta).
El deep link se configura solo en Supabase Dashboard.

### 4. AndroidManifest.xml (ya configurado)
```xml
<intent-filter android:autoVerify="true">
    <action android:name="android.intent.action.VIEW" />
    <category android:name="android.intent.category.DEFAULT" />
    <category android:name="android.intent.category.BROWSABLE" />
    <data android:scheme="sikacare" android:host="auth-callback" />
</intent-filter>
```

## Flujo Esperado
1. Usuario toca "Continuar con Google"
2. Se abre Chrome Custom Tab con Google OAuth
3. Usuario se autentica en Google
4. Google redirige a Supabase: `https://proyecto.supabase.co/auth/v1/callback`
5. Supabase procesa y redirige a: `sikacare://auth-callback`
6. Android abre la app con el deep link
7. MainActivity.onNewIntent() llama handleDeeplinks()
8. SplashScreen detecta sesión activa → navega a Home/Onboarding

## Verificar Configuración
- [ ] URLs de redirect en Supabase Dashboard
- [ ] Client IDs en Google provider
- [ ] Redirect URIs en Google Console
- [ ] Deep link en AndroidManifest.xml
- [ ] handleDeeplinks() en MainActivity
