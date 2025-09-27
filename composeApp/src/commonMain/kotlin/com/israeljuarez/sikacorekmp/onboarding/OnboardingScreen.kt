package com.israeljuarez.sikacorekmp.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun OnboardingScreen(
    modifier: Modifier = Modifier,
    // Datos opcionales para mostrar al usuario
    initialEmail: String = "",
    // Callbacks (placeholder, aún sin lógica real)
    onResendVerification: () -> Unit = {},
    onRefreshConfirmation: () -> Unit = {},
    onStart: (gender: String, birthdate: String) -> Unit = { _, _ -> }
) {
    var email by remember { mutableStateOf(initialEmail) }
    var isEmailConfirmed by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf<String?>(null) } // "M" | "F" | "O"
    var birthdate by remember { mutableStateOf("") } // YYYY-MM-DD

    val isStartEnabled = isEmailConfirmed && !selectedGender.isNullOrEmpty() && birthdate.matches(Regex("\\d{4}-\\d{2}-\\d{2}"))

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        Text(text = "Bienvenido", style = MaterialTheme.typography.headlineMedium)
        Text(
            text = "Completemos tu perfil para empezar",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF475569)
        )

        // Sección: Verificación de correo
        Text(text = "Verificación de correo", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = email,
            onValueChange = { /* solo lectura en onboarding */ },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth(),
            enabled = false
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(if (isEmailConfirmed) "Correo confirmado" else "Correo no confirmado")
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { onResendVerification() }) { Text("Reenviar verificación") }
                TextButton(onClick = {
                    // Placeholder: alternar para demo; en implementación real refrescar desde Auth
                    isEmailConfirmed = true
                    onRefreshConfirmation()
                }) { Text("Ya confirmé") }
            }
        }

        // Sección: Selección de género
        Text(text = "Género", style = MaterialTheme.typography.titleMedium)
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
            GenderOption("M", selectedGender) { selectedGender = it }
            GenderOption("F", selectedGender) { selectedGender = it }
            GenderOption("O", selectedGender) { selectedGender = it }
        }

        // Sección: Fecha de nacimiento (texto simple YYYY-MM-DD para placeholder)
        OutlinedTextField(
            value = birthdate,
            onValueChange = { birthdate = it.take(10) },
            label = { Text("Fecha de nacimiento (YYYY-MM-DD)") },
            modifier = Modifier.fillMaxWidth()
        )

        // Botón Comenzar
        Button(
            onClick = { onStart(selectedGender ?: "", birthdate) },
            enabled = isStartEnabled,
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1877F2),
                contentColor = Color.White
            )
        ) { Text("Comenzar") }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun GenderOption(value: String, selected: String?, onSelect: (String) -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        RadioButton(selected = selected == value, onClick = { onSelect(value) })
        Text(value)
    }
}
