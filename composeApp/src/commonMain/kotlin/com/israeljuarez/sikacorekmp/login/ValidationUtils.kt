package com.israeljuarez.sikacorekmp.login

import androidx.compose.ui.graphics.Color

/**
 * Utilidades de validación para formularios de login
 */

// Colores para feedback visual
val ValidationSuccess = Color(0xFF10B981) // Verde
val ValidationError = Color(0xFFEF4444)   // Rojo
val ValidationWarning = Color(0xFFF59E0B) // Amarillo

/**
 * Valida si un email tiene formato correcto y es válido
 */
fun isValidEmail(email: String): Boolean {
    if (email.isBlank()) return false
    
    // Regex más estricto para validar email
    val emailRegex = "^[a-zA-Z0-9]([a-zA-Z0-9._-]*[a-zA-Z0-9])?@[a-zA-Z0-9]([a-zA-Z0-9.-]*[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$".toRegex()
    
    // Verificaciones adicionales
    val parts = email.split("@")
    if (parts.size != 2) return false
    
    val localPart = parts[0]
    val domainPart = parts[1]
    
    // El local part no puede empezar o terminar con punto
    if (localPart.startsWith(".") || localPart.endsWith(".")) return false
    
    // No puede tener puntos consecutivos
    if (localPart.contains("..")) return false
    
    // El dominio debe tener al menos un punto
    if (!domainPart.contains(".")) return false
    
    // El dominio no puede empezar o terminar con punto o guión
    if (domainPart.startsWith(".") || domainPart.endsWith(".") || 
        domainPart.startsWith("-") || domainPart.endsWith("-")) return false
    
    return emailRegex.matches(email)
}

/**
 * Valida si un email está completo (termina con .com, .org, etc.)
 */
fun isEmailComplete(email: String): Boolean {
    return email.contains("@") && email.contains(".") && 
           email.split(".").last().length >= 2
}

/**
 * Valida si una contraseña cumple con los requisitos:
 * - Mínimo 8 caracteres
 * - Al menos una letra mayúscula
 * - Al menos una letra minúscula
 * - Al menos un número (opcional, pero recomendado)
 */
fun isValidPassword(password: String): PasswordValidationResult {
    val hasMinLength = password.length >= 8
    val hasUpperCase = password.any { it.isUpperCase() }
    val hasLowerCase = password.any { it.isLowerCase() }
    val hasNumber = password.any { it.isDigit() }
    
    val errors = mutableListOf<String>()
    
    if (!hasMinLength) errors.add("Mínimo 8 caracteres")
    if (!hasUpperCase) errors.add("Al menos una mayúscula")
    if (!hasLowerCase) errors.add("Al menos una minúscula")
    if (!hasNumber) errors.add("Al menos un número")
    
    return PasswordValidationResult(
        isValid = errors.isEmpty(),
        errors = errors,
        hasMinLength = hasMinLength,
        hasUpperCase = hasUpperCase,
        hasLowerCase = hasLowerCase,
        hasNumber = hasNumber
    )
}

/**
 * Valida si dos contraseñas coinciden
 */
fun doPasswordsMatch(password: String, confirmPassword: String): Boolean {
    return password.isNotEmpty() && confirmPassword.isNotEmpty() && password == confirmPassword
}

/**
 * Resultado de validación de contraseña
 */
data class PasswordValidationResult(
    val isValid: Boolean,
    val errors: List<String>,
    val hasMinLength: Boolean,
    val hasUpperCase: Boolean,
    val hasLowerCase: Boolean,
    val hasNumber: Boolean
)

/**
 * Estados de validación para feedback visual
 */
enum class ValidationState {
    NONE,       // Sin validación
    VALIDATING, // Validando
    VALID,      // Válido
    INVALID     // Inválido
}

/**
 * Información de validación para un campo
 */
data class FieldValidation(
    val state: ValidationState = ValidationState.NONE,
    val message: String = "",
    val showMessage: Boolean = false
)
