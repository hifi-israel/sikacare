package com.israeljuarez.sikacorekmp.login

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import sikacore.composeapp.generated.resources.Res
import sikacore.composeapp.generated.resources.google_logo
import sikacore.composeapp.generated.resources.facebook_logo_vector
import com.israeljuarez.sikacorekmp.getPlatform

@Composable
fun RegisterScreen(
    onNavigateToLogin: () -> Unit = {}
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

    LaunchedEffect(Unit) { }

    val isDesktop = remember { getPlatform().name.startsWith("Java") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBlue)
    ) {
        if (isDesktop) {
            BoxWithConstraints(Modifier.fillMaxSize()) {
                val side = (if (maxWidth < maxHeight) maxWidth else maxHeight) * 0.8f

                CurvedContainerCanvasRegister(
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
                    onNavigateToLogin = onNavigateToLogin
                )
            }
        } else {
            CurvedContainerCanvasRegister(
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
                onNavigateToLogin = onNavigateToLogin
            )
        }
    }
}

@Composable
private fun CurvedContainerCanvasRegister(modifier: Modifier = Modifier, bothRounded: Boolean) {
    Canvas(modifier = modifier) {
        val radius = 24.dp.toPx()
        val w = size.width
        val h = size.height

        val path = Path().apply {
            if (bothRounded) {
                moveTo(0f, radius)
                quadraticTo(0f, 0f, radius, 0f)
                lineTo(w - radius, 0f)
                quadraticTo(w, 0f, w, radius)
                lineTo(w, h - radius)
                quadraticTo(w, h, w - radius, h)
                lineTo(radius, h)
                quadraticTo(0f, h, 0f, h - radius)
                close()
            } else {
                moveTo(0f, radius)
                quadraticTo(0f, 0f, radius, 0f)
                lineTo(w - radius, 0f)
                quadraticTo(w, 0f, w, radius)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
        }

        drawPath(path = path, color = Color.White, style = Fill)
    }
}

@Composable
private fun RegisterContent(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit
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

        Button(onClick = { /* TODO: Registrar */ }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
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
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HorizontalDivider(Modifier.weight(1f))
            Text("  o continuar con  ", color = Color(0xFF64748B))
            HorizontalDivider(Modifier.weight(1f))
        }

        Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            val fbBlue = Color(0xFF1877F2)
            Button(
                onClick = { },
                modifier = Modifier.fillMaxWidth().height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = fbBlue, contentColor = Color.White)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(Res.drawable.facebook_logo_vector),
                        contentDescription = "Facebook",
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Continuar con Facebook")
                }
            }

            OutlinedButton(onClick = { }, modifier = Modifier.fillMaxWidth().height(48.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Image(
                        painter = painterResource(Res.drawable.google_logo),
                        contentDescription = "Google",
                        modifier = Modifier.size(25.dp)
                    )
                    Spacer(Modifier.width(12.dp))
                    Text("Continuar con Google")
                }
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}
