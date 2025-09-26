package com.israeljuarez.sikacorekmp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeContentPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.israeljuarez.sikacorekmp.login.LoginScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        // Render de la primera vista compartida (Login)
        LoginScreen()
    }
}