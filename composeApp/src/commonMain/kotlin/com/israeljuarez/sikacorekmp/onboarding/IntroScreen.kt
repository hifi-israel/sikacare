package com.israeljuarez.sikacorekmp.onboarding

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.israeljuarez.sikacorekmp.getPlatform
import com.israeljuarez.sikacorekmp.login.SharedCurvedContainerCanvas
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource
import sikacore.composeapp.generated.resources.Res
import sikacore.composeapp.generated.resources.Imagen_vista1
import sikacore.composeapp.generated.resources.imagen_vista2
import sikacore.composeapp.generated.resources.imagen_vista3
import sikacore.composeapp.generated.resources.arrow_back
import sikacore.composeapp.generated.resources.arrow_forward
import sikacore.composeapp.generated.resources.check
import com.israeljuarez.sikacorekmp.profile.ProfileRepository
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

// Función para obtener el recurso de imagen según el paso
@Composable
private fun getImageResource(step: Int): DrawableResource {
    return when (step) {
        0 -> Res.drawable.Imagen_vista1
        1 -> Res.drawable.imagen_vista2
        2 -> Res.drawable.imagen_vista3
        else -> Res.drawable.Imagen_vista1
    }
}

@Composable
fun IntroScreen(
    onFinish: () -> Unit = {},
    onBack: () -> Unit = {}
) {
    val backgroundBlue = Color(0xFF89C1EA)
    var isVisible by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val profileRepo = remember { ProfileRepository() }

    val containerTargetOffset: Dp = 0.dp
    val containerHiddenOffset: Dp = 600.dp

    val offsetY by animateDpAsState(
        targetValue = if (isVisible) containerTargetOffset else containerHiddenOffset,
        animationSpec = tween(durationMillis = 600, easing = FastOutSlowInEasing),
        label = "introOffset"
    )

    LaunchedEffect(Unit) {
        isVisible = true
    }

    val isDesktop = remember { getPlatform().name.startsWith("Java") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundBlue)
    ) {
        if (isDesktop) {
            BoxWithConstraints(Modifier.fillMaxSize()) {
                val side = (if (maxWidth < maxHeight) maxWidth else maxHeight) * 0.8f

                SharedCurvedContainerCanvas(
                    modifier = Modifier
                        .size(side)
                        .align(Alignment.Center)
                        .offset(y = offsetY),
                    bothRounded = true
                )

                IntroContent(
                    modifier = Modifier
                        .size(side)
                        .align(Alignment.Center)
                        .offset(y = offsetY)
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    onFinish = onFinish
                )
            }
        } else {
            SharedCurvedContainerCanvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
                    .align(Alignment.BottomCenter)
                    .offset(y = offsetY),
                bothRounded = false
            )

            IntroContent(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.88f)
                    .align(Alignment.BottomCenter)
                    .offset(y = offsetY)
                    .padding(horizontal = 24.dp, vertical = 16.dp),
                onFinish = onFinish
            )
        }
    }
}

@Composable
private fun IntroContent(
    modifier: Modifier = Modifier,
    onFinish: () -> Unit
) {
    val viewModel = remember { OnboardingViewModel() }
    val scope = rememberCoroutineScope()
    val profileRepo = remember { ProfileRepository() }
    val currentStep by viewModel.currentStep.collectAsState()
    val introScreens = viewModel.introScreens

    val currentScreen = introScreens.getOrNull(currentStep)
    val isLastScreen = currentStep == introScreens.size - 1
    val isFirstScreen = currentStep == 0

    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(Modifier.height(32.dp))

        // Imagen principal directamente sobre fondo azul
        if (currentScreen != null) {
            Image(
                painter = painterResource(getImageResource(currentStep)),
                contentDescription = currentScreen.title,
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(250.dp),
                contentScale = ContentScale.Fit
            )
        }

        // Contenido de texto sin Card
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            currentScreen?.let { screen ->
                Text(
                    text = screen.title,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF1A1A1A),
                    textAlign = TextAlign.Center
                )

                Text(
                    text = screen.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color(0xFF4A4A4A),
                    textAlign = TextAlign.Center,
                    lineHeight = MaterialTheme.typography.bodyLarge.lineHeight * 1.4
                )
            }
        }

        // Indicadores de progreso
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            repeat(introScreens.size) { index ->
                Box(
                    modifier = Modifier
                        .size(if (index == currentStep) 12.dp else 8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(
                            if (index == currentStep) Color(0xFF1877F2)
                            else Color(0xFFE5E7EB)
                        )
                )
                if (index < introScreens.size - 1) {
                    Spacer(modifier = Modifier.width(8.dp))
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botones de navegación
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Botón atrás
            if (!isFirstScreen) {
                Button(
                    onClick = viewModel::previousStep,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF6B7280),
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        painter = painterResource(Res.drawable.arrow_back),
                        contentDescription = "Atrás",
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Atrás")
                }
            } else {
                Spacer(modifier = Modifier.width(1.dp))
            }

            // Botón siguiente/finalizar
            Button(
                onClick = {
                    if (isLastScreen) {
                        scope.launch {
                            try {
                                // Actualizar is_onboarding_seen a true
                                profileRepo.updateOnboardingSeen(true)
                                onFinish()
                            } catch (e: Exception) {
                                // En caso de error, continuar de todos modos
                                onFinish()
                            }
                        }
                    } else {
                        viewModel.nextStep()
                    }
                },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1877F2),
                    contentColor = Color.White
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.height(48.dp)
            ) {
                Text(
                    text = if (isLastScreen) "Finalizar" else "Siguiente",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                if (!isLastScreen) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(Res.drawable.arrow_forward),
                        contentDescription = "Siguiente",
                        modifier = Modifier.size(18.dp)
                    )
                } else {
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        painter = painterResource(Res.drawable.check),
                        contentDescription = "Finalizar",
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Botón de saltar (solo en las primeras pantallas)
        if (!isLastScreen) {
            TextButton(
                onClick = {
                    scope.launch {
                        try {
                            // Actualizar is_onboarding_seen a true al saltar
                            profileRepo.updateOnboardingSeen(true)
                            onFinish()
                        } catch (e: Exception) {
                            // En caso de error, continuar de todos modos
                            onFinish()
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Saltar introducción",
                    color = Color(0xFF1877F2)
                )
            }
        }

        Spacer(Modifier.height(16.dp))
    }
}