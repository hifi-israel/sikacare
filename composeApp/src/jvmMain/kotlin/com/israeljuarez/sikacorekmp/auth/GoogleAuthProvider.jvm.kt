package com.israeljuarez.sikacorekmp.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.israeljuarez.sikacorekmp.core.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.Google

@Composable
actual fun rememberGoogleAuthProvider(): GoogleAuthProvider {
    return remember { GoogleAuthProvider() }
}

actual class GoogleAuthProvider {
    actual suspend fun signInWithGoogle(): Result<Unit> {
        return try {
            // Desktop/JVM usa el flujo OAuth estándar
            SupabaseProvider.client.auth.signInWith(Google)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
