package com.israeljuarez.sikacorekmp.login

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.israeljuarez.sikacorekmp.auth.AuthRepository
import com.israeljuarez.sikacorekmp.getPlatform
import kotlinx.coroutines.launch

@Composable
fun ResetPasswordWithCodeScreen(
    email: String,
    onNavigateToLogin: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val backgroundBlue = Color(0xFF89C1EA)
    var isVisible by remember { mutableStateOf(false) }

    val containerTargetOffset: Dp = 0.dp
    val containerHiddenOffset: Dp = 600.dp

    val offsetY by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isVisible) containerTargetOffset else containerHiddenOffset,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "resetPasswordOffset"
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

                ResetPasswordContent(
                    modifier = Modifier
                        .size(side)
                        .align(Alignment.Center)
                        .offset(y = offsetY)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    email = email,
                    onNavigateToLogin = onNavigateToLogin,
                    onBack = onBack
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

            ResetPasswordContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
                    .align(Alignment.BottomCenter)
                    .offset(y = offsetY)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                email = email,
                onNavigateToLogin = onNavigateToLogin,
                onBack = onBack
            )
        }
    }
}

@Composable
private fun ResetPasswordContent(
    modifier: Modifier = Modifier,
    email: String,
    onNavigateToLogin: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    var verificationCode by remember { mutableStateOf("") }
    var newPassword by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var showPassword by remember { mutableStateOf(false) }
    var showConfirmPassword by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showPasswordForm by remember { mutableStateOf(false) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var codeSent by remember { mutableStateOf(false) }
    var generatedCode by remember { mutableStateOf("") }
    
    val scope = rememberCoroutineScope()
    val repo = remember { AuthRepository() }
    
    // Enviar email de reset al cargar la pantalla
    LaunchedEffect(Unit) {
        if (!codeSent) {
            repo.sendPasswordResetEmail(email)
            codeSent = true
        }
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))
        
        // Título
        Text(
            text = if (!showPasswordForm) "Verificar código" else "Nueva contraseña",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1F2937)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (!showPasswordForm)
                "Te enviamos un enlace para restablecer tu contraseña a $email"
            else
                "Crea una nueva contraseña segura",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF6B7280),
            modifier = Modifier.padding(horizontal = 16.dp)
        )

        // Mensaje informativo sobre el email enviado
        if (codeSent && !showPasswordForm) {
            Text(
                text = "📧 Email enviado usando template de Supabase. Revisa tu bandeja de entrada y spam.",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF3B82F6),
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Text(
                text = "🔗 Haz clic en el enlace del email para continuar",
                style = MaterialTheme.typography.bodySmall,
                color = Color(0xFF10B981),
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(32.dp))

        if (!showPasswordForm) {
            // Botón para continuar (simula que el usuario hizo clic en el enlace)
            Button(
                onClick = {
                    // Simular que el usuario hizo clic en el enlace del email
                    showPasswordForm = true
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF3B82F6),
                    contentColor = Color.White
                )
            ) {
                Text("Continuar (simula clic en enlace)")
            }
            TextButton(onClick = onBack) {
                Text("Volver")
            }
        } else {
            // Campos de nueva contraseña
            OutlinedTextField(
                value = newPassword,
                onValueChange = { newPassword = it },
                label = { Text("Nueva contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showPassword = !showPassword }) {
                        Text(if (showPassword) "Ocultar" else "Mostrar")
                    }
                },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (showConfirmPassword) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    TextButton(onClick = { showConfirmPassword = !showConfirmPassword }) {
                        Text(if (showConfirmPassword) "Ocultar" else "Mostrar")
                    }
                },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Botón actualizar contraseña
            Button(
                onClick = {
                    if (newPassword.length < 8) {
                        errorMessage = "La contraseña debe tener al menos 8 caracteres"
                    } else if (newPassword != confirmPassword) {
                        errorMessage = "Las contraseñas no coinciden"
                    } else {
                        scope.launch {
                            try {
                                errorMessage = null
                                repo.updatePassword(email, newPassword)
                                successMessage = "¡Contraseña actualizada exitosamente!"
                            } catch (e: Throwable) {
                                errorMessage = "Error al actualizar la contraseña. Intenta más tarde"
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF10B981),
                    contentColor = Color.White
                )
            ) {
                Text("Actualizar contraseña")
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Mensajes de error y éxito
        errorMessage?.let { msg ->
            Text(
                text = msg,
                color = Color(0xFFEF4444),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        successMessage?.let { msg ->
            Text(
                text = msg,
                color = Color(0xFF10B981),
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Botones de navegación
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            TextButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Volver")
            }
            
            if (successMessage != null) {
                Button(
                    onClick = onNavigateToLogin,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF3B82F6),
                        contentColor = Color.White
                    )
                ) {
                    Text("Ir al login")
                }
            }
        }
        
        Spacer(Modifier.height(16.dp))
    }
}
