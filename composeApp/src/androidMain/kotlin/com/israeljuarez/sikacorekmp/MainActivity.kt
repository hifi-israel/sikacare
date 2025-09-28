package com.israeljuarez.sikacorekmp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.israeljuarez.sikacorekmp.core.SupabaseProvider
import io.github.jan.supabase.auth.handleDeeplinks
import android.content.Intent

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Instala el Splash del sistema lo antes posible
        installSplashScreen()
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)

        // Manejar deep links de OAuth/OTP
        intent?.let { SupabaseProvider.client.handleDeeplinks(it) }

        setContent {
            App()
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        SupabaseProvider.client.handleDeeplinks(intent)
    }
}

@Preview
@Composable
fun AppAndroidPreview() {
    App()
}