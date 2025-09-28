package com.israeljuarez.sikacorekmp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.rememberCoroutineScope
import org.jetbrains.compose.ui.tooling.preview.Preview
import com.israeljuarez.sikacorekmp.login.LoginScreen
import com.israeljuarez.sikacorekmp.login.RegisterScreen
import com.israeljuarez.sikacorekmp.login.ForgotPasswordScreen
import com.israeljuarez.sikacorekmp.splash.SplashScreen
import com.israeljuarez.sikacorekmp.onboarding.OnboardingScreen
import com.israeljuarez.sikacorekmp.home.HomeScreen
import com.israeljuarez.sikacorekmp.profile.ProfileRepository
import com.israeljuarez.sikacorekmp.core.SupabaseProvider
import kotlinx.coroutines.launch
import io.github.jan.supabase.auth.auth

@Composable
@Preview
fun App() {
    MaterialTheme {
        var current by remember { mutableStateOf(AppScreen.Splash) }
        val scope = rememberCoroutineScope()
        val initialEmail = remember { SupabaseProvider.client.auth.currentUserOrNull()?.email ?: "" }
        when (current) {
            AppScreen.Splash -> SplashScreen(
                onNavigateToLogin = { current = AppScreen.Login },
                onNavigateToOnboarding = { current = AppScreen.Onboarding },
                onNavigateToHome = { current = AppScreen.Home }
            )
            AppScreen.Login -> LoginScreen(
                onNavigateToRegister = { current = AppScreen.Register },
                onNavigateToForgotPassword = { current = AppScreen.ForgotPassword },
                onLoginSuccess = { current = AppScreen.Splash }
            )
            AppScreen.Register -> RegisterScreen(
                onNavigateToLogin = { current = AppScreen.Login },
                onRegisteredConfirmed = { current = AppScreen.Splash }
            )
            AppScreen.ForgotPassword -> ForgotPasswordScreen(
                onNavigateToLogin = { current = AppScreen.Login }
            )
            AppScreen.Onboarding -> OnboardingScreen(
                initialEmail = initialEmail,
                onResendVerification = { /* opcional: supabase.auth.resend(...) si lo habilitamos */ },
                onRefreshConfirmation = { current = AppScreen.Splash },
                onStart = { gender, birthdate ->
                    scope.launch {
                        ProfileRepository().finishOnboarding(gender, birthdate)
                        current = AppScreen.Home
                    }
                }
            )
            AppScreen.Home -> HomeScreen(
                onLogout = { current = AppScreen.Login }
            )
        }
    }
}

private enum class AppScreen { Splash, Login, Register, ForgotPassword, Onboarding, Home }