package com.israeljuarez.sikacorekmp.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import org.jetbrains.compose.resources.painterResource
import sikacore.composeapp.generated.resources.Res
import sikacore.composeapp.generated.resources.person
import com.israeljuarez.sikacorekmp.profile.ProfileRepository
import com.israeljuarez.sikacorekmp.core.SupabaseProvider
import kotlinx.coroutines.launch
import io.github.jan.supabase.auth.auth

/**
 * Pantalla principal de la aplicación
 * Muestra avatar del usuario y opción de cerrar sesión
 */
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    fullName: String? = null,
    avatarLabel: String? = null,
    onLogout: () -> Unit = {}
) {
    val scope = rememberCoroutineScope()
    val profileRepo = remember { ProfileRepository() }
    var name by remember { mutableStateOf(fullName) }
    var avatarId by remember { mutableStateOf<Int?>(null) }
    var avatarImageUrl by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        // Cargar perfil actual (suspend) con try/catch
        try {
            val profile = profileRepo.getProfile()
            name = profile?.full_name ?: name
            avatarId = profile?.avatar_id
            
            // Cargar la imagen del avatar si tenemos un ID
            avatarId?.let { id ->
                val avatars = profileRepo.getAvatars()
                val selectedAvatar = avatars.find { it.id == id }
                avatarImageUrl = selectedAvatar?.image_url
                println("🏠 [DEBUG] Avatar cargado en Home: ID=$id, URL=$avatarImageUrl")
            }
        } catch (e: Throwable) {
            println("❌ [ERROR] Error cargando perfil en Home: ${e.message}")
            // noop: mantenemos valores por defecto
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Avatar circular con imagen
        if (avatarImageUrl != null) {
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(
                        width = 3.dp,
                        color = Color(0xFF1877F2),
                        shape = CircleShape
                    )
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                AsyncImage(
                    model = avatarImageUrl,
                    contentDescription = "Avatar del usuario",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop,
                    error = painterResource(Res.drawable.person),
                    placeholder = painterResource(Res.drawable.person),
                    onError = { 
                        println("❌ [ERROR] Error cargando avatar en Home: ${it.result.throwable?.message}")
                    },
                    onSuccess = {
                        println("✅ [SUCCESS] Avatar cargado exitosamente en Home")
                    }
                )
            }
        } else {
            // Avatar por defecto si no hay imagen
            Box(
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .background(Color.White)
                    .border(
                        width = 2.dp,
                        color = Color(0xFFE5E7EB),
                        shape = CircleShape
                    )
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(Res.drawable.person),
                    contentDescription = "Avatar por defecto",
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(20.dp),
                    tint = Color(0xFF6B7280)
                )
            }
        }
        
        Spacer(Modifier.height(16.dp))
        
        Text(
            text = "Bienvenido${name?.let { ", $it" } ?: ""}",
            style = MaterialTheme.typography.headlineMedium
        )
        
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
