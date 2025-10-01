package com.israeljuarez.sikacorekmp.onboarding

import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.datetime.LocalDate
import kotlinx.datetime.toLocalDate
import java.time.LocalDateTime
import java.time.ZoneId

@RequiresApi(Build.VERSION_CODES.O)
actual fun getCurrentDate(): LocalDate {
    return try {
        // Obtener fecha real del dispositivo Android
        val localDateTime = LocalDateTime.now(ZoneId.systemDefault())
        LocalDate(localDateTime.year, localDateTime.monthValue, localDateTime.dayOfMonth)
    } catch (e: Exception) {
        println("Error obteniendo fecha del dispositivo Android: ${e.message}")
        LocalDate(2024, 1, 1)
    }
}
