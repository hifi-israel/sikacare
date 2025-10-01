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
import coil3.compose.AsyncImage
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.IconButton
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Popup
import kotlinx.coroutines.launch
import kotlinx.datetime.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OnboardingContent(
    modifier: Modifier = Modifier,
    onNavigateToHome: () -> Unit,
    onLogout: () -> Unit
) {
    val viewModel = remember { OnboardingViewModel() }
    val userProfile by viewModel.userProfile.collectAsState()
    val isEmailVerified by viewModel.isEmailVerified.collectAsState()
    
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
                    println("Avatares cargados: $avatars")
                    // Si no hay avatares en la BD, crear unos por defecto
                    avatars = listOf(

                        com.israeljuarez.sikacorekmp.profile.Avatar(1, "avatar_1", "Avatar 1", ""),
                        com.israeljuarez.sikacorekmp.profile.Avatar(2, "avatar_2", "Avatar 2", ""),
                        com.israeljuarez.sikacorekmp.profile.Avatar(3, "avatar_3", "Avatar 3", ""),
                        com.israeljuarez.sikacorekmp.profile.Avatar(4, "avatar_4", "Avatar 4", ""),
                        com.israeljuarez.sikacorekmp.profile.Avatar(5, "avatar_5", "Avatar 5", "")
                    )
                }
                selectedAvatarId = avatars.first().id
            } catch (e: Exception) {
                println("Error cargando avatares: ${e.message}")
                // Usar avatares por defecto si hay error
                avatars = listOf(
                    com.israeljuarez.sikacorekmp.profile.Avatar(1, "avatar_1", "Avatar 1", ""),
                    com.israeljuarez.sikacorekmp.profile.Avatar(2, "avatar_2", "Avatar 2", ""),
                    com.israeljuarez.sikacorekmp.profile.Avatar(3, "avatar_3", "Avatar 3", "")
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
                                    // Cargar imagen real del avatar desde Supabase
                                    if (avatar.image_url.isNotEmpty()) {
                                        AsyncImage(
                                            model = avatar.image_url,
                                            contentDescription = avatar.name,
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .padding(4.dp),
                                            contentScale = ContentScale.Crop,
                                            error = painterResource(Res.drawable.person),
                                            placeholder = painterResource(Res.drawable.person)
                                        )
                                    } else {
                                        // Fallback: icono genérico si no hay imagen
                                    Icon(
                                        painter = painterResource(Res.drawable.person),
                                        contentDescription = avatar.name,
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .padding(12.dp),
                                            tint = if (selectedAvatarId == avatar.id) Color.White else Color(0xFF6B7280)
                                    )
                                    }
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
                    placeholder = { Text("84148665") },
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
                
                // Selector de fecha de nacimiento simplificado
                Text(
                    text = "Fecha de nacimiento",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                )
                
                var showDatePicker by remember { mutableStateOf(false) }
                var selectedDay by remember { mutableStateOf("") }
                var selectedMonth by remember { mutableStateOf("") }
                var selectedYear by remember { mutableStateOf("") }
                
                // Obtener fecha actual dinámicamente
                val currentDate = remember { getCurrentDate() }
                val currentYear = currentDate.year
                val currentMonth = currentDate.month
                val currentDay = currentDate.day
                
                // Formatear fecha para mostrar
                val formattedDate = if (selectedDay.isNotEmpty() && selectedMonth.isNotEmpty() && selectedYear.isNotEmpty()) {
                    val month = selectedMonth.padStart(2, '0')
                    val day = selectedDay.padStart(2, '0')
                    "$month/$day/$selectedYear"
                } else ""
                
                // Actualizar birthdate cuando cambian los valores
                LaunchedEffect(selectedDay, selectedMonth, selectedYear) {
                    if (selectedDay.isNotEmpty() && selectedMonth.isNotEmpty() && selectedYear.isNotEmpty()) {
                        val day = selectedDay.toIntOrNull() ?: 0
                        val month = selectedMonth.toIntOrNull() ?: 0
                        val year = selectedYear.toIntOrNull() ?: 0
                        
                        // Validar que no sea una fecha futura
                        if (!isFutureDate(day, month, year)) {
                            // Formato YYYY-MM-DD para PostgreSQL
                            birthdate = "$selectedYear-${selectedMonth.padStart(2, '0')}-${selectedDay.padStart(2, '0')}"
                        } else {
                            // Si es fecha futura, limpiar la selección
                            selectedDay = ""
                            selectedMonth = ""
                            selectedYear = ""
                        }
                    }
                }
                
                // Campo de texto para mostrar la fecha
                OutlinedTextField(
                    value = formattedDate,
                    onValueChange = { },
                    label = { Text("Fecha de nacimiento") },
                    placeholder = { Text("MM/DD/YYYY") },
                    readOnly = true,
                    trailingIcon = {
                        IconButton(onClick = { showDatePicker = !showDatePicker }) {
                            Text("📅")
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                )

                // Modal de selección de fecha
                if (showDatePicker) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Seleccionar fecha",
                                style = MaterialTheme.typography.headlineSmall
                            )
                            
                            // Selectores de fecha
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Día
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Día", style = MaterialTheme.typography.labelMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    var expandedDay by remember { mutableStateOf(false) }
                                    Box {
                                        OutlinedTextField(
                                            value = selectedDay,
                                            onValueChange = { 
                                                if (it.length <= 2 && (it.isEmpty() || it.toIntOrNull()?.let { num -> num in 1..31 } == true)) {
                                                    selectedDay = it
                                                }
                                            },
                                            placeholder = { Text("DD") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp)
                                                .clickable { expandedDay = true }
                                        )
                                        DropdownMenu(
                                            expanded = expandedDay,
                                            onDismissRequest = { expandedDay = false },
                                            modifier = Modifier.heightIn(max = 180.dp) // Limitar a ~3 elementos
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
                                }
                                
                                // Mes
                                Column(modifier = Modifier.weight(1.5f)) {
                                    Text("Mes", style = MaterialTheme.typography.labelMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    var expandedMonth by remember { mutableStateOf(false) }
                                    val months = listOf(
                                        "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                                        "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
                                    )
                                    Box {
                                        OutlinedTextField(
                                            value = if (selectedMonth.isNotEmpty()) months[selectedMonth.toInt() - 1] else "",
                                            onValueChange = { },
                                            placeholder = { Text("Mes") },
                                            readOnly = true,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp)
                                                .clickable { expandedMonth = true }
                                        )
                                        DropdownMenu(
                                            expanded = expandedMonth,
                                            onDismissRequest = { expandedMonth = false },
                                            modifier = Modifier.heightIn(max = 180.dp) // Limitar a ~3 elementos
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
                                }
                                
                                // Año
                                Column(modifier = Modifier.weight(1f)) {
                                    Text("Año", style = MaterialTheme.typography.labelMedium)
                                    Spacer(modifier = Modifier.height(4.dp))
                                    var expandedYear by remember { mutableStateOf(false) }
                                    Box {
                                        OutlinedTextField(
                                            value = selectedYear,
                                            onValueChange = { 
                                                if (it.length <= 4 && (it.isEmpty() || it.toIntOrNull()?.let { num -> num in 1920..currentYear } == true)) {
                                                    selectedYear = it
                                                }
                                            },
                                            placeholder = { Text("YYYY") },
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp),
                                            textStyle = MaterialTheme.typography.bodyLarge
                                        )
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(56.dp)
                                                .clickable { expandedYear = true }
                                        )
                                        DropdownMenu(
                                            expanded = expandedYear,
                                            onDismissRequest = { expandedYear = false },
                                            modifier = Modifier.heightIn(max = 180.dp) // Limitar a ~3 elementos
                                        ) {
                                            (currentYear downTo 1920).forEach { year ->
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
                            }
                            
                            // Botones
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Button(
                                    onClick = { showDatePicker = false },
                                    modifier = Modifier.weight(1f),
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF6B7280)
                                    )
                                ) {
                                    Text("Cancelar")
                                }
                                Button(
                                    onClick = { showDatePicker = false },
                                    modifier = Modifier.weight(1f),
                                    enabled = selectedDay.isNotEmpty() && selectedMonth.isNotEmpty() && selectedYear.isNotEmpty()
                                ) {
                                    Text("Confirmar")
                                }
                            }
                        }
                    }
                }
                
                // Mostrar fecha seleccionada o error
                if (formattedDate.isNotEmpty()) {
                    Text(
                        text = "Fecha seleccionada: $formattedDate",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFF4CAF50),
                        modifier = Modifier.padding(top = 8.dp)
                    )
                } else if (selectedDay.isNotEmpty() || selectedMonth.isNotEmpty() || selectedYear.isNotEmpty()) {
                    Text(
                        text = "⚠️ Debe completar todos los capos.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color(0xFFF44336),
                        modifier = Modifier.padding(top = 8.dp)
                    )
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
                        birthdate.isEmpty() -> "Por favor selecciona tu fecha de nacimiento"
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

// Función expect para obtener la fecha actual del dispositivo
expect fun getCurrentDate(): LocalDate

// Función para validar si una fecha es futura
fun isFutureDate(day: Int, month: Int, year: Int): Boolean {
    return try {
        val selectedDate = LocalDate(year, month, day)
        val currentDate = getCurrentDate()
        selectedDate > currentDate
    } catch (e: Exception) {
        false
    }
}
