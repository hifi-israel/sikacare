package com.israeljuarez.sikacorekmp

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.jetbrains.compose.resources.painterResource
import sikacore.composeapp.generated.resources.Res
import sikacore.composeapp.generated.resources.logo
import com.israeljuarez.sikacorekmp.tools.IconGenerator
import java.io.File

fun main() = application {
    // Generar iconos .ico y .icns a partir de logo.png si no existen
    IconGenerator.ensureDesktopIcons(File("composeApp/desktopIcons"))

    // Tamaño mínimo de la ventana (solo Escritorio). Modifícalo aquí:
    // Ejemplo: ancho 900.dp, alto 600.dp
    // El tamaño mínimo se configura dentro del contenido de la ventana (ver LaunchedEffect más abajo)
    Window(
        onCloseRequest = ::exitApplication,
        title = "SikaCare",
        icon = painterResource(Res.drawable.logo)
    ) {
        // Establece tamaño mínimo de la ventana (solo Escritorio). Modifícalo aquí si lo necesitas.
        // window.minimumSize = java.awt.Dimension(600, 500)
        App()
    }
}