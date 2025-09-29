package com.israeljuarez.sikacorekmp.login

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.israeljuarez.sikacorekmp.getPlatform
import com.israeljuarez.sikacorekmp.auth.AuthRepository
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

@Composable
fun ForgotPasswordScreen(
    onNavigateToLogin: () -> Unit = {},
    onNavigateToResetWithCode: (String) -> Unit = {}
) {
    val backgroundBlue = Color(0xFF89C1EA)
    var isVisible by remember { mutableStateOf(false) }

    val containerTargetOffset: Dp = 0.dp
    val containerHiddenOffset: Dp = 600.dp

    val offsetY by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isVisible) containerTargetOffset else containerHiddenOffset,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "forgotPasswordOffset"
    )

    LaunchedEffect(Unit) { 
        isVisible = true
    }

    val isDesktop = remember { getPlatform().name.startsWith("Java") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBlue)
    ) {
        if (isDesktop) {
            BoxWithConstraints(Modifier.fillMaxSize()) {
                val side = (if (maxWidth < maxHeight) maxWidth else maxHeight) * 0.8f

                SharedCurvedContainerCanvas(
                    modifier = Modifier
                        .size(side)
                        .align(Alignment.Center)
                        .offset(y = offsetY),
                    bothRounded = true
                )

                ForgotPasswordContent(
                    modifier = Modifier
                        .size(side)
                        .align(Alignment.Center)
                        .offset(y = offsetY)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToResetWithCode = onNavigateToResetWithCode
                )
            }
        } else {
            SharedCurvedContainerCanvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
                    .align(Alignment.BottomCenter)
                    .offset(y = offsetY),
                bothRounded = false
            )

            ForgotPasswordContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
                    .align(Alignment.BottomCenter)
                    .offset(y = offsetY)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                onNavigateToLogin = onNavigateToLogin,
                onNavigateToResetWithCode = onNavigateToResetWithCode
            )
        }
    }
}

@Composable
private fun ForgotPasswordContent(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onNavigateToResetWithCode: (String) -> Unit
) {
    var email by remember { mutableStateOf("") }
    var showSuccessMessage by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var resetButtonText by remember { mutableStateOf("Enviar enlace de restablecimiento") }
    var isResetDisabled by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val repo = remember { AuthRepository() }
    
    // Validaciones en tiempo real
    val emailValidation = remember(email) {
        when {
            email.isEmpty() -> FieldValidation(ValidationState.NONE)
            !isValidEmail(email) -> FieldValidation(ValidationState.INVALID, "Formato de email inválido", true)
            else -> FieldValidation(ValidationState.VALID, "Email válido", true)
        }
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        
        Text(
            text = "Recuperar contraseña",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Ingresa tu email y te enviaremos un enlace para restablecer tu contraseña",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF475569)
        )

        // Campo de email con validación
        Column {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailValidation.state == ValidationState.INVALID,
                enabled = !showSuccessMessage,
                trailingIcon = {
                    if (isEmailComplete(email) && !showSuccessMessage) {
                        TextButton(onClick = { 
                            // Solo validar formato, no enviar código aún
                        }) {
                            Text("✓", color = ValidationSuccess)
                        }
                    }
                }
            )
            if (emailValidation.showMessage) {
                Text(
                    text = emailValidation.message,
                    color = when (emailValidation.state) {
                        ValidationState.VALID -> ValidationSuccess
                        ValidationState.INVALID -> ValidationError
                        else -> Color.Unspecified
                    },
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        // Mensaje de éxito
        if (showSuccessMessage) {
            Text(
                text = "Te enviamos un enlace para restablecer tu contraseña a: $email",
                style = MaterialTheme.typography.bodyMedium,
                color = Color(0xFF4CAF50),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = "📧 Revisa tu bandeja de entrada y spam. Usa el código que aparece en la consola para continuar.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF3B82F6),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        // Mensaje de error
        errorMessage?.let { msg ->
            Text(
                text = msg,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Botón de reestablecer contraseña
        Button(
            onClick = {
                if (!isResetDisabled) {
                    // Mostrar "Próximamente" por 2 segundos
                    resetButtonText = "Próximamente"
                    isResetDisabled = true
                    
                    scope.launch {
                        kotlinx.coroutines.delay(2000)
                        resetButtonText = "Enviar enlace de restablecimiento"
                        isResetDisabled = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            enabled = !isResetDisabled && isValidEmail(email),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isResetDisabled) Color(0xFF9CA3AF) else Color(0xFF1877F2),
                contentColor = Color.White
            )
        ) {
            Text(resetButtonText)
        }

        // Enlace de regreso a Login (solo si no se ha enviado el enlace)
        if (!showSuccessMessage) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("¿Recordaste tu contraseña? ")
                TextButton(onClick = { onNavigateToLogin() }) { 
                    Text("Inicia sesión") 
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
