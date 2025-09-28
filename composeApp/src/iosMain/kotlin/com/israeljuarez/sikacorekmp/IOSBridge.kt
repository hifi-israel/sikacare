package com.israeljuarez.sikacorekmp

import com.israeljuarez.sikacorekmp.core.SupabaseProvider
import io.github.jan.supabase.auth.handleDeeplinks
import platform.Foundation.NSURL

object IOSBridge {
    // Llama desde AppDelegate/SceneDelegate: IOSBridge.handleDeepLink(url)
    fun handleDeepLink(url: NSURL) {
        SupabaseProvider.client.handleDeeplinks(url)
    }
}
