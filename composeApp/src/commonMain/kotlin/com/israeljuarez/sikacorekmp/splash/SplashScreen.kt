package com.israeljuarez.sikacorekmp.splash

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.background
import androidx.compose.ui.graphics.Color
import androidx.compose.foundation.Image
import org.jetbrains.compose.resources.painterResource
import sikacore.composeapp.generated.resources.Res
import sikacore.composeapp.generated.resources.logo
import kotlinx.coroutines.delay

@Composable
fun SplashScreen(
    modifier: Modifier = Modifier,
    onNavigateToLogin: () -> Unit = {},
    onNavigateToOnboarding: () -> Unit = {},
    onNavigateToHome: () -> Unit = {}
) {
    val vm = remember { SplashViewModel() }
    val navigated = remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        // Min-delay para evitar doble transición abrupta con el splash del sistema
        delay(300)
        try {
            when (vm.resolveDestination()) {
                SplashViewModel.Destination.Login -> if (!navigated.value) { navigated.value = true; onNavigateToLogin() }
                SplashViewModel.Destination.Onboarding -> if (!navigated.value) { navigated.value = true; onNavigateToOnboarding() }
                SplashViewModel.Destination.Home -> if (!navigated.value) { navigated.value = true; onNavigateToHome() }
            }
        } catch (_: Throwable) {
            if (!navigated.value) { navigated.value = true; onNavigateToLogin() }
        }
    }
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF89C1EA)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(Res.drawable.logo),
                contentDescription = null
            )
            Spacer(Modifier.height(16.dp))
            CircularProgressIndicator()
        }
    }
}
