package com.israeljuarez.sikacorekmp.auth

import com.israeljuarez.sikacorekmp.core.SupabaseProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.JsonPrimitive

class AuthRepository(
    private val client: SupabaseClient = SupabaseProvider.client
) {
    suspend fun loadFromStorage() {
        client.auth.loadFromStorage()
    }

    fun hasActiveSession(): Boolean {
        return client.auth.currentSessionOrNull() != null
    }

    suspend fun signInWithEmailPassword(email: String, password: String) {
        client.auth.signInWith(io.github.jan.supabase.auth.providers.builtin.Email) {
            this.email = email
            this.password = password
        }
    }

    suspend fun signUpWithEmailPassword(email: String, password: String) {
        client.auth.signUpWith(io.github.jan.supabase.auth.providers.builtin.Email) {
            this.email = email
            this.password = password
            // No enviar email de verificación - se hará en onboarding
            this.data = kotlinx.serialization.json.buildJsonObject {
                put("email_confirm", JsonPrimitive(false))
            }
        }
    }

    suspend fun resetPasswordForEmail(email: String) {
        // SIMULADO para desarrollo - NO enviar email real
        println("📧 Reset password SIMULADO para: $email")
        println("🔗 En desarrollo: Simula que el usuario hizo clic en el enlace")
    }
    
    // Simular envío de email para desarrollo (SIN rate limiting)
    suspend fun sendPasswordResetEmail(email: String) {
        try {
            // Solo simular, NO enviar email real
            println("📧 Email de reset SIMULADO para: $email")
            println("🔗 En desarrollo: Simula que el usuario hizo clic en el enlace")
            println("✅ Usuario puede continuar con el flujo")
        } catch (e: Throwable) {
            println("Error al simular email de reset: ${e.message}")
            throw e
        }
    }
    
    // Actualizar contraseña (simulado)
    suspend fun updatePassword(email: String, newPassword: String) {
        // En una implementación real, actualizarías la contraseña en Supabase
        // Por ahora simulamos la actualización
        println("Contraseña actualizada para $email")
    }

    suspend fun signOut() {
        client.auth.signOut()
    }

    suspend fun resendConfirmationEmail(email: String) {
        // Función temporal - en versiones más recientes de Supabase se puede usar resend
        // Por ahora, simplemente lanzamos una excepción para indicar que no está implementado
        throw UnsupportedOperationException("Resend confirmation email no está disponible en esta versión")
    }

    @OptIn(kotlin.time.ExperimentalTime::class)
    fun isEmailConfirmed(): Boolean {
        val user = client.auth.currentUserOrNull()
        return user?.emailConfirmedAt != null
    }

    fun getCurrentUserEmail(): String? {
        return client.auth.currentUserOrNull()?.email
    }
}
