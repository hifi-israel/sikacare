package com.israeljuarez.sikacorekmp.onboarding

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.israeljuarez.sikacorekmp.auth.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

data class OnboardingScreen(
    val id: Int,
    val title: String,
    val description: String,
    val imageUrl: String,
    val orderIndex: Int
)

data class UserProfile(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val avatarUrl: String = "",
    val verificationMethod: String = "email"
)

/**
 * ViewModel para manejar el estado del onboarding
 * Gestiona perfil de usuario, avatares y navegación entre pantallas
 */
class OnboardingViewModel(
    private val authRepository: AuthRepository = AuthRepository()
) {
    private val _currentStep = MutableStateFlow(0)
    val currentStep: StateFlow<Int> = _currentStep.asStateFlow()
    
    private val _userProfile = MutableStateFlow(UserProfile())
    val userProfile: StateFlow<UserProfile> = _userProfile.asStateFlow()
    
    private val _isEmailVerified = MutableStateFlow(false)
    val isEmailVerified: StateFlow<Boolean> = _isEmailVerified.asStateFlow()
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    var verificationCode by mutableStateOf("")
        private set
    
    var showCodeInput by mutableStateOf(false)
        private set
    
    var codeSent by mutableStateOf(false)
        private set
    
    var errorMessage by mutableStateOf<String?>(null)
        private set
    
    // Pantallas de introducción (solo 3)
    val introScreens = listOf(
        OnboardingScreen(
            id = 1,
            title = "Bienvenido a SikaCare",
            description = "Tu salud en tus manos. Accede a servicios médicos de calidad desde la comodidad de tu hogar.",
            imageUrl = "Imagen_vista1",
            orderIndex = 1
        ),
        OnboardingScreen(
            id = 2,
            title = "Consultas médicas virtuales",
            description = "Conecta con profesionales de la salud las 24 horas. Consultas seguras y confidenciales.",
            imageUrl = "imagen_vista2",
            orderIndex = 2
        ),
        OnboardingScreen(
            id = 3,
            title = "Historial médico seguro",
            description = "Mantén tu información médica organizada y segura. Acceso fácil cuando lo necesites.",
            imageUrl = "imagen_vista3",
            orderIndex = 3
        )
    )
    
    init {
        loadUserData()
    }
    
    /**
     * Carga datos del usuario autenticado desde el repositorio
     */
    private fun loadUserData() {
        val email = authRepository.getCurrentUserEmail()
        if (email != null) {
            _userProfile.value = _userProfile.value.copy(email = email)
            _isEmailVerified.value = authRepository.isEmailConfirmed()
        }
    }
    
    /**
     * Actualiza los datos del perfil de usuario
     */
    fun updateProfile(
        fullName: String = _userProfile.value.fullName,
        phone: String = _userProfile.value.phone,
        avatarUrl: String = _userProfile.value.avatarUrl
    ) {
        _userProfile.value = _userProfile.value.copy(
            fullName = fullName,
            phone = phone,
            avatarUrl = avatarUrl
        )
    }
    
    /**
     * Avanza al siguiente paso del onboarding
     */
    fun nextStep() {
        if (_currentStep.value < introScreens.size - 1) {
            _currentStep.value = _currentStep.value + 1
        }
    }
    
    /**
     * Retrocede al paso anterior del onboarding
     */
    fun previousStep() {
        if (_currentStep.value > 0) {
            _currentStep.value = _currentStep.value - 1
        }
    }
    
    /**
     * Envía código de verificación al email del usuario
     */
    fun sendVerificationCode() {
        val email = _userProfile.value.email
        if (email.isNotEmpty()) {
            verificationCode = (100000..999999).random().toString()
            codeSent = true
            showCodeInput = true
            errorMessage = null
        }
    }
    
    /**
     * Verifica el código de verificación ingresado por el usuario
     */
    fun verifyCode(inputCode: String): Boolean {
        return if (inputCode == verificationCode) {
            _isEmailVerified.value = true
            showCodeInput = false
            errorMessage = null
            true
        } else {
            errorMessage = "Código incorrecto. Intenta de nuevo."
            false
        }
    }
    
    
    suspend fun completeOnboarding(): Boolean {
        return try {
            _isLoading.value = true
            errorMessage = null
            
            // Aquí guardarías los datos del perfil en Supabase
            // Por ahora simulamos el guardado
            kotlinx.coroutines.delay(1000)
            
            _isLoading.value = false
            true
        } catch (e: Exception) {
            _isLoading.value = false
            errorMessage = "Error al completar el onboarding: ${e.message}"
            false
        }
    }
    
    fun clearError() {
        errorMessage = null
    }
    
    fun skipVerification() {
        _isEmailVerified.value = true
        showCodeInput = false
    }
    
}
