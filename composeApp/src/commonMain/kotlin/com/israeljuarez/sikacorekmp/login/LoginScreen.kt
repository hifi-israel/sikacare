package com.israeljuarez.sikacorekmp.login

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import org.jetbrains.compose.resources.painterResource
import sikacore.composeapp.generated.resources.Res
import sikacore.composeapp.generated.resources.google_logo

// import sikacore.composeapp.generated.resources.logo

/**
 * Pantalla de Login compartida para Android, iOS y Desktop.
 * - Fondo azul #89C1EA en toda la pantalla
 * - Contenedor blanco con parte superior redondeada dibujado con Canvas
 * - Animación: el contenedor (y su contenido) suben desde abajo usando animateDpAsState
 * - Solo UI (sin lógica real).
 */
@Composable
fun LoginScreen() {
    val backgroundBlue = Color(0xFF89C1EA)
    var isVisible by remember { mutableStateOf(false) }

    val containerTargetOffset: Dp = 0.dp
    val containerHiddenOffset: Dp = 600.dp // fuera de pantalla

    val offsetY by androidx.compose.animation.core.animateDpAsState(
        targetValue = if (isVisible) containerTargetOffset else containerHiddenOffset,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "loginOffset"
    )

    LaunchedEffect(Unit) { isVisible = true }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBlue)
    ) {
        // Forma blanca animada
        CurvedContainerCanvas(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .align(Alignment.BottomCenter)
                .offset(y = offsetY)
        )

        // Contenido del login encima de la forma
        LoginContent(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.88f)
                .align(Alignment.BottomCenter)
                .offset(y = offsetY)
                .padding(horizontal = 24.dp, vertical = 16.dp)
        )
    }
}

@Composable
private fun CurvedContainerCanvas(modifier: Modifier = Modifier) {
    Canvas(modifier = modifier) {
        val radius = 24.dp.toPx()
        val w = size.width
        val h = size.height

        val path = Path().apply {
            // Esquinas superiores redondeadas
            moveTo(0f, radius)
            quadraticBezierTo(0f, 0f, radius, 0f)
            lineTo(w - radius, 0f)
            quadraticBezierTo(w, 0f, w, radius)
            lineTo(w, h)
            lineTo(0f, h)
            close()
        }

        drawPath(
            path = path,
            color = Color.White,
            style = Fill
        )
    }
}

@Composable
private fun LoginContent(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Spacer(Modifier.height(8.dp))

        Text(
            text = "Bienvenido",
            style = MaterialTheme.typography.headlineMedium
        )
        Text(
            text = "Inicia sesión para continuar",
            style = MaterialTheme.typography.bodyMedium,
            color = Color(0xFF475569)
        )

        // Campos (solo UI)
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Email") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = "",
            onValueChange = {},
            label = { Text("Contraseña") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )

        Button(onClick = {}, modifier = Modifier.fillMaxWidth()) {
            Text("Ingresar")
        }

        // Separador
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Divider(Modifier.weight(1f))
            Text("  o continuar con  ", color = Color(0xFF64748B))
            androidx.compose.material3.Divider(Modifier.weight(1f))
        }

        // Botones sociales (icono + texto).
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            // Google: usa el asset compartido. Si ya compilaste los recursos, reemplaza por Res.drawable.android_dark_rd_na_2x
            SocialButton(
                text = "Continuar con Google",
                icon = { Image(painterResource(Res.drawable.google_logo), contentDescription = "Google") },
                onClick = {}
            )
            // Facebook: a la espera del asset oficial. Se muestra solo texto por ahora.
            SocialButton(
                text = "Continuar con Facebook",
                icon = { /* TODO: Reemplazar cuando se agregue facebook_logo.png a common resources */ },
                onClick = {}
            )
        }

        Spacer(Modifier.height(16.dp))
    }
}

@Composable
private fun SocialButton(
    text: String,
    icon: @Composable () -> Unit,
    onClick: () -> Unit,
) {
    androidx.compose.material3.OutlinedButton(onClick = onClick) {
        icon()
        Spacer(Modifier.width(8.dp))
        Text(text)
    }
}
