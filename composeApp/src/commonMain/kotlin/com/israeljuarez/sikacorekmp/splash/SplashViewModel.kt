package com.israeljuarez.sikacorekmp.splash

import com.israeljuarez.sikacorekmp.auth.AuthRepository
import com.israeljuarez.sikacorekmp.profile.ProfileRepository

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

        if (!authRepository.hasActiveSession()) return Destination.Login

        // Leer perfil para decidir onboarding/home
        val profileRepo = ProfileRepository()
        val profile = profileRepo.getProfile()
        return if (profile?.is_onboarding_seen == true) Destination.Home else Destination.Onboarding
    }
}
