package com.israeljuarez.sikacorekmp.onboarding

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Clock
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

actual fun getCurrentDate(): LocalDate {
    return try {
        // Obtener fecha real del dispositivo iOS
        Clock.System.now().toLocalDateTime(TimeZone.currentSystemDefault()).date
    } catch (e: Exception) {
        println("Error obteniendo fecha del dispositivo iOS: ${e.message}")
        LocalDate(2024, 1, 1)
    }
}
