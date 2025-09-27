package com.israeljuarez.sikacorekmp.core

import com.israeljuarez.sikacorekmp.config.BuildKonfig
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.postgrest

object SupabaseProvider {
    val client: SupabaseClient by lazy {
        createSupabaseClient(
            supabaseUrl = BuildKonfig.SUPABASE_URL,
            supabaseKey = BuildKonfig.SUPABASE_ANON_KEY
        ) {
            // Plugins
            install(io.github.jan.supabase.auth.Auth)
            install(Postgrest)
            // TODO: instalar ComposeAuth (Google) cuando conectemos OAuth
        }
    }
}
