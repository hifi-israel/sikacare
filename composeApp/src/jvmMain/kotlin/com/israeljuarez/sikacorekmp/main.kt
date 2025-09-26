package com.israeljuarez.sikacorekmp

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.painterResource
import sikacore.composeapp.generated.resources.Res
import sikacore.composeapp.generated.resources.compose_multiplatform
import sikacore.composeapp.generated.resources.logo

fun main() = application {
    var showSplash by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        // Duración del splash en Desktop: ajusta este delay
        delay(800)
        showSplash = false
    }

    // Tamaño mínimo de la ventana (solo Escritorio). Modifícalo aquí:
    // Ejemplo: ancho 900.dp, alto 600.dp
    // El tamaño mínimo se configura dentro del contenido de la ventana (ver LaunchedEffect más abajo)
    Window(
        onCloseRequest = ::exitApplication,
        title = "sikacore",
        icon = painterResource(Res.drawable.logo)
    ) {
        // Establece tamaño mínimo de la ventana (solo Escritorio). Modifícalo aquí:
        // Valores en píxeles (AWT). Ejemplo: 900x600. Ajusta según necesites.
        LaunchedEffect(Unit) {
            window.minimumSize = java.awt.Dimension(600, 500)
        }

        if (showSplash) {
            DesktopSplash()
        } else {
            App()
        }
    }
}

@Composable
private fun DesktopSplash() {
    androidx.compose.material3.MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF89C1EA)),
            contentAlignment = Alignment.Center
        ) {
            // Muestra el logo en Desktop. Si agregas logo.png a common compose resources
            // (composeApp/src/commonMain/composeResources/drawable/logo.png), cambia a Res.drawable.logo
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = null,
                modifier = Modifier.size(128.dp)
            )
        }
    }
}