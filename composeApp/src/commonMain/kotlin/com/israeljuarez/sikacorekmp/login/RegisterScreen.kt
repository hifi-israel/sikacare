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
    var confirmPassword by remember { mutableStateOf("") }
    var verificationCode by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    var showVerificationCode by remember { mutableStateOf(false) }
    var emailLocked by remember { mutableStateOf("") }
    
    // Validaciones en tiempo real
    val emailValidation = remember(email) {
        when {
            email.isEmpty() -> FieldValidation(ValidationState.NONE)
            !isValidEmail(email) -> FieldValidation(ValidationState.INVALID, "Formato de email inválido", true)
            else -> FieldValidation(ValidationState.VALID, "Email válido", true)
        }
    }
    
    val passwordValidation = remember(password) {
        if (password.isEmpty()) {
            FieldValidation(ValidationState.NONE)
        } else {
            val result = isValidPassword(password)
            FieldValidation(
                state = if (result.isValid) ValidationState.VALID else ValidationState.INVALID,
                message = if (result.isValid) "Contraseña válida" else result.errors.joinToString(", "),
                showMessage = true
            )
        }
    }
    
    val passwordMatchValidation = remember(password, confirmPassword) {
        when {
            confirmPassword.isEmpty() -> FieldValidation(ValidationState.NONE)
            doPasswordsMatch(password, confirmPassword) -> FieldValidation(ValidationState.VALID, "Las contraseñas coinciden", true)
            else -> FieldValidation(ValidationState.INVALID, "Las contraseñas no coinciden", true)
        }
    }
    
    val phoneValidation = remember(phone) {
        when {
            phone.isEmpty() -> FieldValidation(ValidationState.NONE)
            phone.length != 8 -> FieldValidation(ValidationState.INVALID, "Debe tener exactamente 8 dígitos", true)
            else -> FieldValidation(ValidationState.VALID, "Teléfono válido", true)
        }
    }

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

        // Campo de email con validación
        Column {
            OutlinedTextField(
                value = email,
                onValueChange = { 
                    if (!showVerificationCode) {
                        email = it
                    }
                },
                label = { Text("Email") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                isError = emailValidation.state == ValidationState.INVALID,
                enabled = !showVerificationCode,
                trailingIcon = {
                    if (isEmailComplete(email) && !showVerificationCode) {
                        TextButton(onClick = { 
                            // Solo validar formato, no enviar código aún
                        }) {
                            Text("✓", color = ValidationSuccess)
                        }
                    } else if (showVerificationCode) {
                        TextButton(onClick = { 
                            // Permitir cambiar email pero reiniciar proceso
                            showVerificationCode = false
                            emailLocked = ""
                            verificationCode = ""
                        }) {
                            Text("Cambiar", color = ValidationWarning)
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

        // Teléfono Nicaragua: 8 dígitos numéricos con validación
        Column {
            OutlinedTextField(
                value = phone,
                onValueChange = { raw ->
                    val filtered = raw.filter { it.isDigit() }.take(8)
                    phone = filtered
                },
                label = { Text("Número telefónico (8 dígitos)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                isError = phoneValidation.state == ValidationState.INVALID
            )
            if (phoneValidation.showMessage) {
                Text(
                    text = phoneValidation.message,
                    color = when (phoneValidation.state) {
                        ValidationState.VALID -> ValidationSuccess
                        ValidationState.INVALID -> ValidationError
                        else -> Color.Unspecified
                    },
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        // Campo de contraseña con validación
        Column {
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().height(62.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordValidation.state == ValidationState.INVALID,
                trailingIcon = {
                    TextButton(onClick = { passwordVisible = !passwordVisible }) {
                        Text(if (passwordVisible) "Ocultar" else "Mostrar")
                    }
                }
            )
            if (passwordValidation.showMessage) {
                Text(
                    text = passwordValidation.message,
                    color = when (passwordValidation.state) {
                        ValidationState.VALID -> ValidationSuccess
                        ValidationState.INVALID -> ValidationError
                        else -> Color.Unspecified
                    },
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        // Campo de confirmar contraseña con validación
        Column {
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                label = { Text("Confirmar contraseña") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth().height(62.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                isError = passwordMatchValidation.state == ValidationState.INVALID,
                trailingIcon = {
                    TextButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                        Text(if (confirmPasswordVisible) "Ocultar" else "Mostrar")
                    }
                }
            )
            if (passwordMatchValidation.showMessage) {
                Text(
                    text = passwordMatchValidation.message,
                    color = when (passwordMatchValidation.state) {
                        ValidationState.VALID -> ValidationSuccess
                        ValidationState.INVALID -> ValidationError
                        else -> Color.Unspecified
                    },
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        // Campo de código de verificación (solo visible después de enviar código)
        if (showVerificationCode) {
            Column {
                OutlinedTextField(
                    value = verificationCode,
                    onValueChange = { verificationCode = it },
                    label = { Text("Código de verificación") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth().height(60.dp),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    placeholder = { Text("Ingresa el código enviado a $emailLocked") }
                )
                Text(
                    text = "Código enviado a: $emailLocked",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFF64748B),
                    modifier = Modifier.padding(start = 16.dp, top = 4.dp)
                )
            }
        }

        Button(
            onClick = { 
                if (!showVerificationCode) {
                    // Primera vez: enviar código de verificación
                    if (isValidEmail(email) && passwordValidation.state == ValidationState.VALID && passwordMatchValidation.state == ValidationState.VALID && phoneValidation.state == ValidationState.VALID) {
                        showVerificationCode = true
                        emailLocked = email
                        // TODO: Enviar código de verificación al email
                    }
                } else {
                    // Segunda vez: verificar código y registrar
                    if (verificationCode.isNotEmpty()) {
                        // TODO: Verificar código y proceder con registro
                        onNavigateToRegister()
                    }
                }
            }, 
            modifier = Modifier.fillMaxWidth().height(48.dp),
            enabled = if (!showVerificationCode) {
                isValidEmail(email) && passwordValidation.state == ValidationState.VALID && passwordMatchValidation.state == ValidationState.VALID && phoneValidation.state == ValidationState.VALID
            } else {
                verificationCode.isNotEmpty()
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF89C1EA),
                contentColor = Color.White
            )
        ) {
            Text(if (showVerificationCode) "Verificar código y registrarse" else "Enviar código de verificación")
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
