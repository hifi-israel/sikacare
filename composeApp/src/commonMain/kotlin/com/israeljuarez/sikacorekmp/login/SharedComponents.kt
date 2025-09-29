package com.israeljuarez.sikacorekmp.login

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import sikacore.composeapp.generated.resources.Res
import kotlinx.coroutines.launch
import sikacore.composeapp.generated.resources.google_logo
import sikacore.composeapp.generated.resources.facebook_logo_vector

/**
 * Componente compartido para el contenedor curvo con Canvas
 */
@Composable
fun SharedCurvedContainerCanvas(modifier: Modifier = Modifier, bothRounded: Boolean) {
    Canvas(modifier = modifier) {
        val radius = 24.dp.toPx()
        val w = size.width
        val h = size.height

        val path = Path().apply {
            if (bothRounded) {
                // Esquinas redondeadas arriba y abajo
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
                // Solo esquinas superiores redondeadas (comportamiento móvil previo)
                moveTo(0f, radius)
                quadraticTo(0f, 0f, radius, 0f)
                lineTo(w - radius, 0f)
                quadraticTo(w, 0f, w, radius)
                lineTo(w, h)
                lineTo(0f, h)
                close()
            }
        }

        drawPath(
            path = path,
            color = Color.White,
            style = Fill
        )
    }
}

/**
 * Componente compartido para los botones sociales (Facebook y Google)
 */
@Composable
fun SocialButtons(
    actionText: String = "Continuar",
    onFacebookClick: () -> Unit = {},
    onGoogleClick: () -> Unit = {}
) {
    var facebookText by remember { mutableStateOf("$actionText con Facebook") }
    var isFacebookDisabled by remember { mutableStateOf(false) }
    
    Column(modifier = Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        // Facebook
        val fbBlue = Color(0xFF1877F2)
        Button(
            onClick = {
                if (!isFacebookDisabled) {
                    // Mostrar "Próximamente" por 2 segundos
                    isFacebookDisabled = true
                    facebookText = "Próximamente"
                    
                    // Volver al texto original después de 2 segundos
                    kotlinx.coroutines.GlobalScope.launch {
                        kotlinx.coroutines.delay(2000)
                        facebookText = "$actionText con Facebook"
                        isFacebookDisabled = false
                    }
                }
            },
            modifier = Modifier.fillMaxWidth().height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isFacebookDisabled) Color(0xFF9CA3AF) else fbBlue, 
                contentColor = Color.White
            ),
            enabled = !isFacebookDisabled
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(Res.drawable.facebook_logo_vector),
                    contentDescription = "Facebook",
                    modifier = Modifier.size(30.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text(facebookText)
            }
        }

        // Google (estilo claro con borde)
        OutlinedButton(
            onClick = onGoogleClick,
            modifier = Modifier.fillMaxWidth().height(48.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Image(
                    painter = painterResource(Res.drawable.google_logo),
                    contentDescription = "Google",
                    modifier = Modifier.size(25.dp)
                )
                Spacer(Modifier.width(12.dp))
                Text("$actionText con Google")
            }
        }
    }
}

/**
 * Componente compartido para el separador "o continuar con"
 */
@Composable
fun SocialSeparator() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        HorizontalDivider(Modifier.weight(1f))
        Text("  o continuar con  ", color = Color(0xFF64748B))
        HorizontalDivider(Modifier.weight(1f))
    }
}
