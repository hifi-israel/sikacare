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
import com.israeljuarez.sikacorekmp.splash.SplashScreen
import com.israeljuarez.sikacorekmp.onboarding.OnboardingScreen
import com.israeljuarez.sikacorekmp.home.HomeScreen

@Composable
@Preview
fun App() {
    MaterialTheme {
        var current by remember { mutableStateOf(AppScreen.Splash) }
        when (current) {
            AppScreen.Splash -> SplashScreen(
                onNavigateToLogin = { current = AppScreen.Login },
                onNavigateToOnboarding = { current = AppScreen.Onboarding },
                onNavigateToHome = { current = AppScreen.Home }
            )
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
            AppScreen.Onboarding -> OnboardingScreen(
                initialEmail = "",
                onResendVerification = { /* TODO */ },
                onRefreshConfirmation = { /* TODO */ },
                onStart = { _, _ -> current = AppScreen.Home }
            )
            AppScreen.Home -> HomeScreen(
                onLogout = { current = AppScreen.Login }
            )
        }
    }
}

private enum class AppScreen { Splash, Login, Register, ForgotPassword, Onboarding, Home }