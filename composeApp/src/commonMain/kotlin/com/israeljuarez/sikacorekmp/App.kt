package com.israeljuarez.sikacorekmp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.israeljuarez.sikacorekmp.login.LoginScreen
import com.israeljuarez.sikacorekmp.login.RegisterScreen
import com.israeljuarez.sikacorekmp.login.ForgotPasswordScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        var current by remember { mutableStateOf(AppScreen.Login) }
        when (current) {
            AppScreen.Login -> LoginScreen(
                onNavigateToRegister = { current = AppScreen.Register },
                onNavigateToForgotPassword = { current = AppScreen.ForgotPassword }
            )
            AppScreen.Register -> RegisterScreen(
                onNavigateToLogin = { current = AppScreen.Login }
            )
            AppScreen.ForgotPassword -> ForgotPasswordScreen(
                onNavigateToLogin = { current = AppScreen.Login }
            )
        }
    }
}

private enum class AppScreen { Login, Register, ForgotPassword }