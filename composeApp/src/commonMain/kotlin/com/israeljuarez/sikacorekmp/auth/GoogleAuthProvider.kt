package com.israeljuarez.sikacorekmp.auth

import androidx.compose.runtime.Composable

@Composable
expect fun rememberGoogleAuthProvider(): GoogleAuthProvider

interface GoogleAuthProvider {
    suspend fun signInWithGoogle(): Result<Unit>
}

