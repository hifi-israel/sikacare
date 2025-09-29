package com.israeljuarez.sikacorekmp.auth

import android.content.Context
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialException
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.android.libraries.identity.googleid.GoogleIdTokenParsingException
import com.israeljuarez.sikacorekmp.config.BuildKonfig
import com.israeljuarez.sikacorekmp.core.SupabaseProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.IDToken
import io.github.jan.supabase.auth.providers.Google
import io.github.jan.supabase.exceptions.RestException

class AndroidGoogleAuth {
    
    suspend fun signInWithGoogle(context: Context): Result<Unit> {
        return try {
            println("🔵 [ANDROID] Iniciando Google Auth para Android...")
            val credentialManager = CredentialManager.create(context)
            
            // NO usar nonce para evitar conflictos con Supabase
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildKonfig.ANDROID_GOOGLE_CLIENT_ID) // Web Client ID
                // .setNonce(hashedNonce) // Removido para evitar conflicto
                .build()
            
            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()
            
            val result = credentialManager.getCredential(
                request = request,
                context = context,
            )
            
            val googleIdTokenCredential = GoogleIdTokenCredential
                .createFrom(result.credential.data)
            val googleIdToken = googleIdTokenCredential.idToken
            
            // Sign in with Supabase using ID Token
            println("🔵 [ANDROID] Autenticando con Supabase...")
            SupabaseProvider.client.auth.signInWith(IDToken) {
                idToken = googleIdToken
                provider = Google
                // No pasar nonce ya que no lo estamos usando
            }
            println("✅ [ANDROID] Google Auth exitoso en Android")
            Result.success(Unit)
            
        } catch (e: GetCredentialException) {
            println("❌ [ANDROID] Error de credenciales: ${e.message}")
            Result.failure(Exception("Credential error: ${e.message}"))
        } catch (e: GoogleIdTokenParsingException) {
            println("❌ [ANDROID] Error parsing token: ${e.message}")
            Result.failure(Exception("Token parsing error: ${e.message}"))
        } catch (e: RestException) {
            println("❌ [ANDROID] Error de Supabase: ${e.message}")
            Result.failure(Exception("Supabase error: ${e.message}"))
        } catch (e: Exception) {
            println("❌ [ANDROID] Error general: ${e.message}")
            Result.failure(e)
        }
    }
}
