package com.israeljuarez.sikacorekmp.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.foundation.Image
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import com.israeljuarez.sikacorekmp.getPlatform
import sikacore.composeapp.generated.resources.Res
import sikacore.composeapp.generated.resources.person
import sikacore.composeapp.generated.resources.email
import sikacore.composeapp.generated.resources.phone
import sikacore.composeapp.generated.resources.calendar
import sikacore.composeapp.generated.resources.check
import com.israeljuarez.sikacorekmp.auth.AuthRepository
import com.israeljuarez.sikacorekmp.login.SharedCurvedContainerCanvas
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch

@Composable
fun OnboardingScreen(
    onNavigateToHome: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val backgroundBlue = Color(0xFF89C1EA)
    var isVisible by remember { mutableStateOf(false) }

    val containerTargetOffset: Dp = 0.dp
    val containerHiddenOffset: Dp = 600.dp

    val offsetY by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isVisible) containerTargetOffset else containerHiddenOffset,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "onboardingOffset"
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

                OnboardingContent(
                    modifier = Modifier
                        .size(side)
                        .align(Alignment.Center)
                        .offset(y = offsetY)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    onNavigateToHome = onNavigateToHome,
                    onLogout = onLogout
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

            OnboardingContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
                    .align(Alignment.BottomCenter)
                    .offset(y = offsetY)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                onNavigateToHome = onNavigateToHome,
                onLogout = onLogout
            )
        }
    }
}

@Composable
private fun OnboardingContent(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit,
    onLogout: () -> Unit
) {
    val viewModel = remember { OnboardingViewModel() }
    val currentStep by viewModel.currentStep.collectAsState()
    val userProfile by viewModel.userProfile.collectAsState()
    val isEmailVerified by viewModel.isEmailVerified.collectAsState()
    val isViewModelLoading by viewModel.isLoading.collectAsState()
    
    var fullName by remember { mutableStateOf(userProfile.fullName) }
    var phone by remember { mutableStateOf("") }
    var selectedGender by remember { mutableStateOf("") }
    var birthdate by remember { mutableStateOf("") }
    var selectedAvatarId by remember { mutableStateOf(1) }
    var avatars by remember { mutableStateOf<List<com.israeljuarez.sikacorekmp.profile.Avatar>>(emptyList()) }
    var verificationCode by remember { mutableStateOf("") }
    var showCodeInput by remember { mutableStateOf(false) }
    var codeSent by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDatePicker by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    
    val scope = rememberCoroutineScope()
    val repo = remember { AuthRepository() }
    val profileRepo = remember { com.israeljuarez.sikacorekmp.profile.ProfileRepository() }
    
    // Obtener email del usuario actual
    val userEmail = remember { repo.getCurrentUserEmail() ?: "" }
    
    // Determinar si es usuario de Google
    val isGoogleUser = remember { userProfile.verificationMethod == "google" }
    
    // Cargar avatares al iniciar
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                avatars = profileRepo.getAvatars()
                if (avatars.isNotEmpty()) {
                    selectedAvatarId = avatars.first().id
                }
            } catch (e: Exception) {
                println("Error cargando avatares: ${e.message}")
            }
        }
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        // Header con avatar
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Selección de Avatar
                Text(
                    text = "Selecciona tu avatar:",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.fillMaxWidth()
                )
                Text(
                    text = "Completa tu perfil",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1F2937)
                )
                
        Text(
                    text = "Necesitamos algunos datos adicionales para personalizar tu experiencia",
            style = MaterialTheme.typography.bodyMedium,
                    color = Color(0xFF6B7280),
                    textAlign = TextAlign.Center
                )
            }
        }

        // Campos del perfil
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Campo de nombre completo
                OutlinedTextField(
                    value = fullName,
                    onValueChange = { fullName = it },
                    label = { Text("Nombre completo") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    leadingIcon = {
                        Icon(painter = painterResource(Res.drawable.person), contentDescription = null)
                    }
                )

                // Campo de género con chips más pequeños
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Género:",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        FilterChip(
                            selected = selectedGender == "M",
                            onClick = { selectedGender = "M" },
                            label = { Text("Masculino", style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier.height(32.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF89C1EA),
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = selectedGender == "F",
                            onClick = { selectedGender = "F" },
                            label = { Text("Femenino", style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier.height(32.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF89C1EA),
                                selectedLabelColor = Color.White
                            )
                        )
                        FilterChip(
                            selected = selectedGender == "O",
                            onClick = { selectedGender = "O" },
                            label = { Text("Otro", style = MaterialTheme.typography.bodySmall) },
                            modifier = Modifier.height(32.dp),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFF89C1EA),
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                
                // Campo de teléfono (8 dígitos)
                OutlinedTextField(
                    value = phone,
                    onValueChange = { newPhone ->
                        // Solo permitir números y máximo 8 dígitos
                        if (newPhone.all { it.isDigit() } && newPhone.length <= 8) {
                            phone = newPhone
                        }
                    },
                    label = { Text("Teléfono (8 dígitos)") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    placeholder = { Text("12345678") },
                    leadingIcon = {
                        Icon(painter = painterResource(Res.drawable.phone), contentDescription = null)
                    },
                    isError = phone.isNotEmpty() && phone.length != 8,
                    supportingText = {
                        if (phone.isNotEmpty() && phone.length != 8) {
                            Text("El teléfono debe tener exactamente 8 dígitos", color = MaterialTheme.colorScheme.error)
                        }
                    }
                )
                
                // Campo de fecha de nacimiento con DatePicker
                OutlinedTextField(
                    value = birthdate,
                    onValueChange = { },
                    label = { Text("Fecha de nacimiento") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { showDatePicker = true },
                    enabled = false,
                    singleLine = true,
                    placeholder = { Text("DD/MM/AAAA") },
                    leadingIcon = {
                        Icon(painter = painterResource(Res.drawable.calendar), contentDescription = null)
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )

                // Campo de email (solo lectura)
        OutlinedTextField(
                    value = userEmail,
                    onValueChange = { },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = false,
                    leadingIcon = {
                        Icon(painter = painterResource(Res.drawable.email), contentDescription = null)
                    }
                )
            }
        }

        // Sección de verificación de email (solo para registro clásico)
        if (!isGoogleUser && !isEmailVerified) {
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEF3C7)),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text("📧", style = MaterialTheme.typography.headlineSmall)
                        Text(
                            text = "Verificación de email",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF92400E)
                        )
                    }
                    
                    Text(
                        text = "Email: $userEmail",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF6B7280)
                    )
                    
                    if (!showCodeInput) {
                        Button(
                            onClick = {
                                // Simular envío de código
                                verificationCode = (100000..999999).random().toString()
                                codeSent = true
                                showCodeInput = true
                                errorMessage = null
                            },
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF59E0B),
                                contentColor = Color.White
                            )
                        ) {
                            Text("Enviar código de verificación")
                        }
                    } else {
                        OutlinedTextField(
                            value = verificationCode,
                            onValueChange = { verificationCode = it },
                            label = { Text("Código de verificación") },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        
        Row(
            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    if (verificationCode.length == 6) {
                                        // Simular verificación exitosa
                                        viewModel.skipVerification()
                                    } else {
                                        errorMessage = "El código debe tener 6 dígitos"
                                    }
                                },
                                modifier = Modifier.weight(1f),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF10B981),
                                    contentColor = Color.White
                                )
                            ) {
                                Text("Verificar")
                            }
                            
                            TextButton(onClick = { showCodeInput = false }) {
                                Text("Cancelar")
                            }
                        }
                    }
                    
                    if (codeSent && !isEmailVerified) {
                        Text(
                            text = "📧 Te hemos enviado un código de 6 dígitos a $userEmail. Revisa tu bandeja de entrada y spam.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color(0xFF92400E),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        } else if (isGoogleUser || isEmailVerified) {
            // Mostrar mensaje de éxito si ya está verificado
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFD1FAE5))
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.check),
                        contentDescription = null,
                        tint = Color(0xFF065F46)
                    )
                    Text(
                        text = if (isGoogleUser) "Cuenta de Google verificada automáticamente" else "Correo verificado correctamente",
                        color = Color(0xFF065F46),
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        // Mensaje de error
        errorMessage?.let { msg ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFEE2E2))
            ) {
                Text(
                    text = msg,
                    color = Color(0xFFDC2626),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de continuar
        Button(
            onClick = {
                if (fullName.isNotEmpty() && selectedGender.isNotEmpty() && birthdate.isNotEmpty() && phone.isNotEmpty()) {
                    scope.launch {
                        isLoading = true
                        try {
                            // Actualizar perfil completo
                            profileRepo.finishOnboarding(
                                fullName = fullName,
                                phone = phone,
                                gender = selectedGender,
                                birthdate = birthdate,
                                avatarId = selectedAvatarId
                            )
                            onNavigateToHome()
                        } catch (e: Exception) {
                            errorMessage = "Error al completar el onboarding: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                } else {
                    errorMessage = when {
                        fullName.isEmpty() -> "El nombre completo es obligatorio"
                        phone.isEmpty() -> "El teléfono es obligatorio"
                        selectedGender.isEmpty() -> "Por favor selecciona tu género"
                        birthdate.isEmpty() -> "Por favor ingresa tu fecha de nacimiento"
                        else -> "Por favor completa todos los campos"
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(56.dp),
            enabled = fullName.isNotEmpty() && phone.length == 8 && selectedGender.isNotEmpty() && birthdate.isNotEmpty() && (isGoogleUser || isEmailVerified) && !isLoading,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1877F2),
                contentColor = Color.White
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    color = Color.White
                )
            } else {
                Text(
                    text = "Continuar",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Botón de cerrar sesión
        TextButton(
            onClick = onLogout,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Cerrar sesión")
        }

        Spacer(Modifier.height(16.dp))
    }
}
