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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
    
    // Cargar avatares al iniciar o usar valores por defecto
    LaunchedEffect(Unit) {
        scope.launch {
            try {
                avatars = profileRepo.getAvatars()
                if (avatars.isEmpty()) {
                    // Si no hay avatares en la BD, crear unos por defecto
                    avatars = listOf(
                        com.israeljuarez.sikacorekmp.profile.Avatar(1, "", "Avatar 1"),
                        com.israeljuarez.sikacorekmp.profile.Avatar(2, "", "Avatar 2"),
                        com.israeljuarez.sikacorekmp.profile.Avatar(3, "", "Avatar 3"),
                        com.israeljuarez.sikacorekmp.profile.Avatar(4, "", "Avatar 4"),
                        com.israeljuarez.sikacorekmp.profile.Avatar(5, "", "Avatar 5")
                    )
                }
                selectedAvatarId = avatars.first().id
            } catch (e: Exception) {
                println("Error cargando avatares: ${e.message}")
                // Usar avatares por defecto si hay error
                avatars = listOf(
                    com.israeljuarez.sikacorekmp.profile.Avatar(1, "", "Avatar 1"),
                    com.israeljuarez.sikacorekmp.profile.Avatar(2, "", "Avatar 2"),
                    com.israeljuarez.sikacorekmp.profile.Avatar(3, "", "Avatar 3")
                )
                selectedAvatarId = 1
            }
        }
    }

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        // Header con título
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
                
                // Selección de Avatar
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Selecciona tu avatar:",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF1F2937),
                    modifier = Modifier.fillMaxWidth()
                )
                
                // Mostrar avatares
                if (avatars.isNotEmpty()) {
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 8.dp)
                    ) {
                        items(avatars.size) { index ->
                            val avatar = avatars[index]
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.clickable { selectedAvatarId = avatar.id }
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(72.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (selectedAvatarId == avatar.id) Color(0xFF1877F2).copy(alpha = 0.2f)
                                            else Color(0xFFE5E7EB)
                                        )
                                        .padding(4.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    // Usar diferentes iconos o colores para cada avatar
                                    Icon(
                                        painter = painterResource(Res.drawable.person),
                                        contentDescription = avatar.name,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp),
                                        tint = if (selectedAvatarId == avatar.id) Color(0xFF1877F2) else Color(0xFF6B7280)
                                    )
                                }
                                // Nombre del avatar
                                Text(
                                    text = avatar.name,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (selectedAvatarId == avatar.id) Color(0xFF1877F2) else Color(0xFF6B7280),
                                    modifier = Modifier.padding(top = 4.dp)
                                )
                            }
                        }
                    }
                } else {
                    // Loading placeholder
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        repeat(3) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFFE5E7EB).copy(alpha = 0.5f))
                            )
                        }
                    }
                }
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
                
                // Selector de fecha de nacimiento estilo Google
                Text(
                    text = "Fecha de nacimiento",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                
                var selectedDay by remember { mutableStateOf("") }
                var selectedMonth by remember { mutableStateOf("") }
                var selectedYear by remember { mutableStateOf("") }
                
                // Actualizar birthdate cuando cambian los valores
                LaunchedEffect(selectedDay, selectedMonth, selectedYear) {
                    if (selectedDay.isNotEmpty() && selectedMonth.isNotEmpty() && selectedYear.isNotEmpty()) {
                        // Formato YYYY-MM-DD para la base de datos
                        birthdate = "$selectedYear-${selectedMonth.padStart(2, '0')}-${selectedDay.padStart(2, '0')}"
                    }
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Dropdown para día
                    var expandedDay by remember { mutableStateOf(false) }
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = selectedDay,
                            onValueChange = { },
                            label = { Text("Día") },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = if (expandedDay) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedDay = true }
                        )
                        DropdownMenu(
                            expanded = expandedDay,
                            onDismissRequest = { expandedDay = false }
                        ) {
                            (1..31).forEach { day ->
                                DropdownMenuItem(
                                    text = { Text(day.toString()) },
                                    onClick = {
                                        selectedDay = day.toString()
                                        expandedDay = false
                                    }
                                )
                            }
                        }
                    }
                    
                    // Dropdown para mes
                    var expandedMonth by remember { mutableStateOf(false) }
                    val months = listOf(
                        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
                    )
                    Box(modifier = Modifier.weight(1.5f)) {
                        OutlinedTextField(
                            value = if (selectedMonth.isNotEmpty()) months[selectedMonth.toInt() - 1] else "",
                            onValueChange = { },
                            label = { Text("Mes") },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = if (expandedMonth) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedMonth = true }
                        )
                        DropdownMenu(
                            expanded = expandedMonth,
                            onDismissRequest = { expandedMonth = false }
                        ) {
                            months.forEachIndexed { index, month ->
                                DropdownMenuItem(
                                    text = { Text(month) },
                                    onClick = {
                                        selectedMonth = (index + 1).toString()
                                        expandedMonth = false
                                    }
                                )
                            }
                        }
                    }
                    
                    // Dropdown para año
                    var expandedYear by remember { mutableStateOf(false) }
                    val currentYear = 2024
                    val years = (currentYear downTo 1920).toList()
                    Box(modifier = Modifier.weight(1f)) {
                        OutlinedTextField(
                            value = selectedYear,
                            onValueChange = { },
                            label = { Text("Año") },
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    imageVector = if (expandedYear) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expandedYear = true }
                        )
                        DropdownMenu(
                            expanded = expandedYear,
                            onDismissRequest = { expandedYear = false }
                        ) {
                            years.forEach { year ->
                                DropdownMenuItem(
                                    text = { Text(year.toString()) },
                                    onClick = {
                                        selectedYear = year.toString()
                                        expandedYear = false
                                    }
                                )
                            }
                        }
                    }
                }

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
