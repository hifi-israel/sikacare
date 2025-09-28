package com.israeljuarez.sikacorekmp.auth

import androidx.compose.runtime.Composable

@Composable
expect fun rememberGoogleAuthProvider(): GoogleAuthProvider

expect class GoogleAuthProvider {
    suspend fun signInWithGoogle(): Result<Unit>
}
