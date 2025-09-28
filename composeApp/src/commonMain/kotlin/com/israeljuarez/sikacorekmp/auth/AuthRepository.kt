package com.israeljuarez.sikacorekmp.auth

import com.israeljuarez.sikacorekmp.core.SupabaseProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth

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
        }
    }

    suspend fun resetPasswordForEmail(email: String) {
        client.auth.resetPasswordForEmail(email)
    }
}
