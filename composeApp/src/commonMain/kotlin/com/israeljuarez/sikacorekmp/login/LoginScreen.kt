package com.israeljuarez.sikacorekmp.login

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.israeljuarez.sikacorekmp.auth.AuthRepository
import com.israeljuarez.sikacorekmp.auth.rememberGoogleAuthProvider
import com.israeljuarez.sikacorekmp.core.SupabaseProvider
import com.israeljuarez.sikacorekmp.getPlatform
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch


/**
 * Pantalla de Login compartida para Android, iOS y Desktop.
 * - Fondo azul #89C1EA en toda la pantalla
 * - Contenedor blanco con parte superior redondeada dibujado con Canvas
 * - Animación: el contenedor (y su contenido) suben desde abajo usando animateDpAsState
 * - Solo UI (sin lógica real).
 */
@Composable
fun LoginScreen(
    onNavigateToRegister: () -> Unit = {},
    onNavigateToForgotPassword: () -> Unit = {},
    onLoginSuccess: () -> Unit = {}
) {
    val backgroundBlue = Color(0xFF89C1EA)
    var isVisible by remember { mutableStateOf(false) }

    val containerTargetOffset: Dp = 0.dp
    val containerHiddenOffset: Dp = 600.dp // fuera de pantalla

    val offsetY by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isVisible) containerTargetOffset else containerHiddenOffset,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "loginOffset"
    )

    LaunchedEffect(Unit) { 
        isVisible = true
    }

    // Detección simple de Desktop (JVM) usando Platform
    val isDesktop = remember { 
        try {
            val platform = getPlatform()
            platform.name.startsWith("Java")
        } catch (e: Exception) {
            false
        }
    }
    val scope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBlue)
    ) {
        if (isDesktop) {
            // Desktop: contenedor centrado, tamaño uniforme ~60% del lado menor, esquinas redondeadas arriba y abajo.
            BoxWithConstraints(Modifier.fillMaxSize()) {
                val side = (if (maxWidth < maxHeight) maxWidth else maxHeight) * 0.8f

                // Forma blanca animada (cuadro centrado)
                SharedCurvedContainerCanvas(
                    modifier = Modifier
                        .size(side)
                        .align(Alignment.Center)
                        .offset(y = offsetY),
                    bothRounded = true
                )

                // Contenido del login encima de la forma, mismo tamaño y animación
                LoginContent(
                    modifier = Modifier
                        .size(side)
                        .align(Alignment.Center)
                        .offset(y = offsetY)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    onNavigateToRegister = onNavigateToRegister,
                    onNavigateToForgotPassword = onNavigateToForgotPassword,
                    onLoginSuccess = onLoginSuccess
                )
            }
        } else {
            // Android/iOS: comportamiento previo (anclado abajo, 88% de alto, solo esquina superior redondeada)
            SharedCurvedContainerCanvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
                    .align(Alignment.BottomCenter)
                    .offset(y = offsetY),
                bothRounded = false
            )

            LoginContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
                    .align(Alignment.BottomCenter)
                    .offset(y = offsetY)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                onNavigateToRegister = onNavigateToRegister,
                onNavigateToForgotPassword = onNavigateToForgotPassword,
                onLoginSuccess = onLoginSuccess
            )
        }
    }
}


@Composable
private fun LoginContent(
    modifier: Modifier = Modifier,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    onLoginSuccess: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()
    val repo = remember { AuthRepository() }
    // Simplificar Google Auth - usar directamente
    val googleAuth = rememberGoogleAuthProvider()

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Inicia sesión para continuar",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF475569)
        )

        // Campos (Material 3) con tamaños más compactos y tipos de teclado
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(60.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email
            )
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password
            ),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                // Solo diseño: botón de ojo para mostrar/ocultar contraseña
                androidx.compose.material3.TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(if (passwordVisible) "Ocultar" else "Mostrar")
                }
            }
        )

        // Enlace para recuperar contraseña
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            androidx.compose.material3.TextButton(onClick = { onNavigateToForgotPassword() }) {
                Text("¿Olvidaste tu contraseña?")
            }
        }

        androidx.compose.material3.Button(
            onClick = {
                scope.launch {
                    try {
                        errorMessage = null // Limpiar error previo
                        repo.signInWithEmailPassword(email, password)
                        onLoginSuccess()
                    } catch (e: Throwable) {
                        errorMessage = when {
                            e.message?.contains("Invalid login credentials") == true -> 
                                "Email o contraseña incorrectos"
                            e.message?.contains("Email not confirmed") == true -> 
                                "Por favor confirma tu email antes de iniciar sesión"
                            e.message?.contains("Too many requests") == true -> 
                                "Demasiados intentos. Intenta más tarde"
                            e.message?.contains("validation_failed") == true -> 
                                "Email o contraseña inválidos"
                            else -> "Error al iniciar sesión. Verifica tus datos"
                        }
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1877F2),
                contentColor = Color.White
            )
        ) {
            Text("Ingresar")
        }

        // Kotlin
        errorMessage?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Separador y botones sociales
        SocialSeparator()
        SocialButtons(
            onGoogleClick = {
                // Usar la misma lógica que funciona en el registro
                scope.launch {
                    try {
                        println("🔵 [LOGIN] Iniciando proceso de Google Auth...")
                        val result = googleAuth.signInWithGoogle()
                        println("🔵 [LOGIN] Resultado de Google Auth: $result")
                        
                        if (result.isSuccess) {
                            println("✅ [LOGIN] Google Auth exitoso, navegando...")
                            onLoginSuccess()
                        } else {
                            println("❌ [LOGIN] Google Auth falló: ${result.exceptionOrNull()}")
                            errorMessage = "Error al iniciar sesión con Google: ${result.exceptionOrNull()?.message}"
                        }
                    } catch (e: Exception) {
                        println("❌ [LOGIN] Excepción en Google Auth: ${e.message}")
                        errorMessage = "Error al iniciar sesión con Google: ${e.message}"
                    }
                }
            }
        )

        // Enlace a registro
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("¿No tienes una cuenta? ")
            androidx.compose.material3.TextButton(onClick = { onNavigateToRegister() }) {
                Text("Regístrate")
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
