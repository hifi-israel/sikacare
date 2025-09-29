package com.israeljuarez.sikacorekmp.profile

import com.israeljuarez.sikacorekmp.core.SupabaseProvider
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.serialization.Serializable

@Serializable
data class Profile(
    val user_id: String,
    val full_name: String? = null,
    val phone: String? = null,
    val gender: String? = null,
    val birthdate: String? = null,
    val avatar_id: Int? = null,
    val is_onboarding_seen: Boolean = false
)

@Serializable
private data class ProfileUpdate(
    val gender: String? = null,
    val birthdate: String? = null,
    val is_onboarding_seen: Boolean? = null
)

class ProfileRepository(
    private val client: SupabaseClient = SupabaseProvider.client
) {
    suspend fun getProfile(): Profile? {
        val userId = client.auth.currentUserOrNull()?.id ?: return null
        val result = client.postgrest["profiles"].select {
            filter { eq("user_id", userId) }
            limit(1)
        }
        return result.decodeList<Profile>().firstOrNull()
    }

    suspend fun finishOnboarding(gender: String, birthdate: String) {
        val userId = client.auth.currentUserOrNull()?.id ?: return
        val body = ProfileUpdate(
            gender = gender,
            birthdate = birthdate,
            is_onboarding_seen = true
        )
        client.postgrest["profiles"].update(body) {
            filter { eq("user_id", userId) }
        }
    }
    
    suspend fun updateOnboardingSeen(seen: Boolean) {
        val userId = client.auth.currentUserOrNull()?.id ?: return
        val body = ProfileUpdate(is_onboarding_seen = seen)
        client.postgrest["profiles"].update(body) {
            filter { eq("user_id", userId) }
        }
    }
}
