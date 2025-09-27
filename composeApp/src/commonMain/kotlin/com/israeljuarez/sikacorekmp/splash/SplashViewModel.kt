package com.israeljuarez.sikacorekmp.splash

import com.israeljuarez.sikacorekmp.auth.AuthRepository

class SplashViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) {
    sealed class Destination {
        data object Login : Destination()
        data object Onboarding : Destination()
        data object Home : Destination()
    }

    suspend fun resolveDestination(): Destination {
        // Cargar sesión almacenada si existe
        authRepository.loadFromStorage()
        // TODO: ensureProfile() y leer is_onboarding_seen para decidir entre Onboarding/Home.
        return if (authRepository.hasActiveSession()) Destination.Home else Destination.Login
    }
}
