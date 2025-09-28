package com.israeljuarez.sikacorekmp.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.israeljuarez.sikacorekmp.profile.ProfileRepository
import com.israeljuarez.sikacorekmp.core.SupabaseProvider
import kotlinx.coroutines.launch
import io.github.jan.supabase.auth.auth

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    fullName: String? = null,
    avatarLabel: String? = null,
    onLogout: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf(fullName) }
    var avatar by remember { mutableStateOf(avatarLabel) }

    LaunchedEffect(Unit) {
        // Cargar perfil actual (suspend) con try/catch
        try {
            val profile = ProfileRepository().getProfile()
            name = profile?.full_name ?: name
            avatar = profile?.avatar_id?.toString() ?: avatar
        } catch (_: Throwable) {
            // noop: mantenemos valores por defecto
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Bienvenido${name?.let { ", $it" } ?: ""}",
            style = MaterialTheme.typography.headlineMedium
        )
        Spacer(Modifier.height(8.dp))
        if (!avatar.isNullOrBlank()) {
            Text(
                text = "Avatar: $avatar",
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = {
                scope.launch {
                    try {
                        SupabaseProvider.client.auth.signOut()
                    } catch (_: Throwable) {
                        // ignorar error de logout
                    }
                    onLogout()
                }
            },
            modifier = Modifier.fillMaxWidth(0.6f).height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1877F2),
                contentColor = Color.White
            )
        ) { Text("Cerrar sesión") }
    }
}
