package com.israeljuarez.sikacorekmp.onboarding

import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import java.time.LocalDateTime
import java.time.ZoneId

actual fun getCurrentDate(): LocalDate {
    return try {
        // Obtener fecha real del dispositivo JVM/Desktop
        val localDateTime = LocalDateTime.now(ZoneId.systemDefault())
        LocalDate(localDateTime.year, localDateTime.monthValue, localDateTime.dayOfMonth)
    } catch (e: Exception) {
        println("Error obteniendo fecha del dispositivo JVM: ${e.message}")
        LocalDate(2024, 1, 1)
    }
}
