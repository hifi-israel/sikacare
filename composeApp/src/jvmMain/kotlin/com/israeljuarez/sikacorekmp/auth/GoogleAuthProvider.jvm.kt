package com.israeljuarez.sikacorekmp.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.israeljuarez.sikacorekmp.core.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google

class JvmGoogleAuthProvider : GoogleAuthProvider {
    override suspend fun signInWithGoogle(): Result<Unit> {
        return try {
            println("🔵 [JVM] Iniciando Google Auth para JVM...")
            // Desktop/JVM usa el flujo OAuth estándar
            SupabaseProvider.client.auth.signInWith(Google)
            println("✅ [JVM] Google Auth exitoso en JVM")
            Result.success(Unit)
        } catch (e: Exception) {
            println("❌ [JVM] Error en Google Auth JVM: ${e.message}")
            Result.failure(e)
        }
    }
}

@Composable
actual fun rememberGoogleAuthProvider(): GoogleAuthProvider {
    return remember { JvmGoogleAuthProvider() }
}
