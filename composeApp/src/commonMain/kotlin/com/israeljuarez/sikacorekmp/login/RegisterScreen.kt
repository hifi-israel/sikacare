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
fun RegisterScreen(
    onNavigateToLogin: () -> Unit = {},
    onNavigateToRegister: () -> Unit = {}
) {
    val backgroundBlue = Color(0xFF89C1EA)
    var isVisible by remember { mutableStateOf(false) }

    val containerTargetOffset: Dp = 0.dp
    val containerHiddenOffset: Dp = 600.dp

    val offsetY by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isVisible) containerTargetOffset else containerHiddenOffset,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "registerOffset"
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

                RegisterContent(
                    modifier = Modifier
                        .size(side)
                        .align(Alignment.Center)
                        .offset(y = offsetY)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    onNavigateToLogin = onNavigateToLogin,
                    onNavigateToRegister = onNavigateToRegister
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

            RegisterContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
                    .align(Alignment.BottomCenter)
                    .offset(y = offsetY)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                onNavigateToLogin = onNavigateToLogin,
                onNavigateToRegister = onNavigateToRegister
            )
        }
    }
}


@Composable
private fun RegisterContent(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        Text(text = "Crear cuenta", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Completa tus datos para registrarte",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF475569)
        )

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().height(60.dp)
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )

        // Teléfono Nicaragua: 8 dígitos numéricos
        OutlinedTextField(
            value = phone,
            onValueChange = { raw ->
                val filtered = raw.filter { it.isDigit() }.take(8)
                phone = filtered
            },
            label = { Text("Número telefónico (8 dígitos)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth().height(60.dp),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
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

        Button(onClick = { onNavigateToRegister() }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
            Text("Regístrate")
        }

        // Enlace a Login
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("¿Ya tienes una cuenta? ")
            TextButton(onClick = { onNavigateToLogin() }) { Text("Inicia sesión") }
        }

        // Separador y botones sociales
        SocialSeparator()
        SocialButtons()

        Spacer(Modifier.height(16.dp))
    }
}
