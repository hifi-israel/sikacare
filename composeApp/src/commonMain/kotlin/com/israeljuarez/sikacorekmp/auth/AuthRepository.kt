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
}
