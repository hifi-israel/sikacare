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
    val full_name: String? = null,
    val phone: String? = null,
    val gender: String? = null,
    val birthdate: String? = null,
    val avatar_id: Int? = null,
    val is_onboarding_seen: Boolean? = null
)

@Serializable
data class Avatar(
    val id: Int,
    val key: String,
    val name: String,
    val image_url: String,
    val active: Boolean = true
)

/**
 * Repositorio para operaciones de perfil de usuario y avatares
 * Maneja la comunicación con Supabase para datos de perfil
 */
class ProfileRepository(
    private val client: SupabaseClient = SupabaseProvider.client
) {
    /**
     * Obtiene el perfil del usuario autenticado desde Supabase
     */
    suspend fun getProfile(): Profile? {
        val userId = client.auth.currentUserOrNull()?.id ?: return null
        val result = client.postgrest["profiles"].select {
            filter { eq("user_id", userId) }
            limit(1)
        }
        return result.decodeList<Profile>().firstOrNull()
    }

    /**
     * Completa el onboarding actualizando el perfil con datos del usuario
     */
    suspend fun finishOnboarding(
        fullName: String,
        phone: String,
        gender: String,
        birthdate: String,
        avatarId: Int
    ) {
        val userId = client.auth.currentUserOrNull()?.id ?: return
        val body = ProfileUpdate(
            full_name = fullName,
            phone = phone,
            gender = gender,
            birthdate = birthdate,
            avatar_id = avatarId,
            is_onboarding_seen = true
        )
        client.postgrest["profiles"].update(body) {
            filter { eq("user_id", userId) }
        }
    }
    
    /**
     * Actualiza el estado de onboarding visto por el usuario
     */
    suspend fun updateOnboardingSeen(seen: Boolean) {
        val userId = client.auth.currentUserOrNull()?.id ?: return
        val body = ProfileUpdate(is_onboarding_seen = seen)
        client.postgrest["profiles"].update(body) {
            filter { eq("user_id", userId) }
        }
    }
    
    /**
     * Actualiza el número de teléfono del usuario
     */
    suspend fun updatePhone(phone: String) {
        val userId = client.auth.currentUserOrNull()?.id ?: return
        val body = ProfileUpdate(phone = phone)
        client.postgrest["profiles"].update(body) {
            filter { eq("user_id", userId) }
        }
    }
    
    /**
     * Obtiene la lista de avatares disponibles desde Supabase
     */
    suspend fun getAvatars(): List<Avatar> {
        return try {
            val result = client.postgrest["avatars"].select {
                filter { eq("active", true) }
            }
            result.decodeList<Avatar>()
        } catch (e: Exception) {
            println("Error obteniendo avatares: ${e.message}")
            emptyList()
        }
    }
}
