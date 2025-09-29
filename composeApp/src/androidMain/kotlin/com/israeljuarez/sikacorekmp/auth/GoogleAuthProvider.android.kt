package com.israeljuarez.sikacorekmp.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

@Composable
actual fun rememberGoogleAuthProvider(): GoogleAuthProvider {
    val context = LocalContext.current
    return remember { GoogleAuthProvider(AndroidGoogleAuth(), context) }
}

actual class GoogleAuthProvider(
    private val androidAuth: AndroidGoogleAuth,
    private val context: android.content.Context
) {
    actual suspend fun signInWithGoogle(): Result<Unit> {
        println("🔵 [ANDROID_PROVIDER] Context disponible: ${context != null}")
        println("🔵 [ANDROID_PROVIDER] AndroidAuth disponible: ${androidAuth != null}")
        return androidAuth.signInWithGoogle(context)
    }
}
