package com.israeljuarez.sikacorekmp.onboarding

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import java.time.LocalDateTime
import java.time.ZoneId

/**
 * Implementación Android para obtener fecha actual del dispositivo
 * Usa java.time.LocalDateTime para compatibilidad con Android
 */
@RequiresApi(Build.VERSION_CODES.O)
actual fun getCurrentDate(): LocalDate {
    return try {
        val localDateTime = LocalDateTime.now(ZoneId.systemDefault())
        LocalDate(localDateTime.year, localDateTime.monthValue, localDateTime.dayOfMonth)
    } catch (e: Exception) {
        LocalDate(2024, 1, 1)
    }
}
