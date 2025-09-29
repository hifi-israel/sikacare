package com.israeljuarez.sikacorekmp.core

import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest

object SupabaseProvider {
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = "https://ywjkkpjixwoymqgswsmc.supabase.co",
            supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl3amtrcGppeHdveW1xZ3N3c21jIiwicm9sZSI6ImFub24iLCJpYXQiOjE3NTg5ODI0NTEsImV4cCI6MjA3NDU1ODQ1MX0.nSbJ_8HkEjRJdP07XIjvkeU897CNJ0NVZVmKfn7GF1E"
        ) {
            // Plugins
            install(Auth) {
                host = "auth-callback"
                scheme = "sikacare"
            }
            install(Postgrest)
        }
    }
}
