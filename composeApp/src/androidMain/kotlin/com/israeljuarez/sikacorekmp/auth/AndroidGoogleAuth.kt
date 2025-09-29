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
import java.security.MessageDigest
import java.util.UUID

class AndroidGoogleAuth {
    
    suspend fun signInWithGoogle(context: Context): Result<Unit> {
        return try {
            println("🔵 [ANDROID] Iniciando Google Auth para Android...")
            val credentialManager = CredentialManager.create(context)
            
            // Generate nonce for security
            val rawNonce = UUID.randomUUID().toString()
            val bytes = rawNonce.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            val hashedNonce = digest.fold("") { str, it -> str + "%02x".format(it) }
            
            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(BuildKonfig.ANDROID_GOOGLE_CLIENT_ID) // Web Client ID
                .setNonce(hashedNonce)
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
            
            // Sign in with Supabase using ID Token (sin nonce)
            println("🔵 [ANDROID] Autenticando con Supabase...")
            SupabaseProvider.client.auth.signInWith(IDToken) {
                idToken = googleIdToken
                provider = Google
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
