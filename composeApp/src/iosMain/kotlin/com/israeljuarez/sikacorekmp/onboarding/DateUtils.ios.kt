package com.israeljuarez.sikacorekmp.onboarding

import kotlinx.datetime.LocalDate
import kotlinx.datetime.Month
import platform.Foundation.NSDate
import platform.Foundation.NSCalendar
import platform.Foundation.NSCalendarUnitYear
import platform.Foundation.NSCalendarUnitMonth
import platform.Foundation.NSCalendarUnitDay

/**
 * Implementación iOS para obtener fecha actual del dispositivo
 * Usa NSDate y NSCalendar para compatibilidad con iOS
 */
actual fun getCurrentDate(): LocalDate {
    return try {
        val now = NSDate()
        val calendar = NSCalendar.currentCalendar
        val components = calendar.components(
            NSCalendarUnitYear or NSCalendarUnitMonth or NSCalendarUnitDay,
            now
        )
        
        LocalDate(
            year = components.year.toInt(),
            month = Month(components.month.toInt()),
            day = components.day.toInt()
        )
    } catch (e: Exception) {
        LocalDate(2024, 1, 1)
    }
}
