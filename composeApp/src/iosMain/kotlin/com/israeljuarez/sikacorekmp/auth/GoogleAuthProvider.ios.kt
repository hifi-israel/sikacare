package com.israeljuarez.sikacorekmp.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
actual fun rememberGoogleAuthProvider(): GoogleAuthProvider {
    return remember { IosGoogleAuthProviderImpl() }
}

class IosGoogleAuthProviderImpl : GoogleAuthProvider {
    override suspend fun signInWithGoogle(): Result<Unit> {
        println("🔵 [IOS_PROVIDER] Google Auth no implementado para iOS aún.")
        return Result.failure(Exception("Google Sign-In no implementado para iOS."))
    }
}
