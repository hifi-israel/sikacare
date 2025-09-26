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

@Composable
fun ForgotPasswordScreen(
    onNavigateToLogin: () -> Unit = {}
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
                    onNavigateToLogin = onNavigateToLogin
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
                onNavigateToLogin = onNavigateToLogin
            )
        }
    }
}

@Composable
private fun ForgotPasswordContent(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }

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
            text = "Ingresa tu email y establece una nueva contraseña",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF475569)
        )

        // Campo de email
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        // Campo de nueva contraseña
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Nueva contraseña") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().height(62.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { passwordVisible = !passwordVisible }) {
                    Text(if (passwordVisible) "Ocultar" else "Mostrar")
                }
            }
        )

        // Campo de confirmar contraseña
        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirmar contraseña") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().height(62.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
            visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                TextButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                    Text(if (confirmPasswordVisible) "Ocultar" else "Mostrar")
                }
            }
        )

        // Botón de reestablecer contraseña
        Button(
            onClick = { 
                // TODO: Implementar lógica de reestablecimiento
            },
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Text("Reestablecer contraseña")
        }

        // Enlace de regreso a Login
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

        Spacer(Modifier.height(16.dp))
    }
}
