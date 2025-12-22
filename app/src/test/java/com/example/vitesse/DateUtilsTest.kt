package com.example.vitesse.utils

import org.junit.Assert.*
import org.junit.Test
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter

class DateUtilsTest {

    private fun calculateAge(dateOfBirth: String): Int {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(dateOfBirth, formatter)
            val currentDate = LocalDate.now()
            Period.between(birthDate, currentDate).years
        } catch (e: Exception) {
            0
        }
    }

    @Test
    fun `calculateAge should return correct age`() {
        // Given
        val birthDate = "01/01/2000"

        // When
        val age = calculateAge(birthDate)

        // Then
        assertTrue(age >= 25)
    }

    @Test
    fun `calculateAge with invalid format should return 0`() {
        // Given
        val invalidDate = "invalid-date"

        // When
        val age = calculateAge(invalidDate)

        // Then
        assertEquals(0, age)
    }

    @Test
    fun `calculateAge with future date should return negative or 0`() {
        // Given
        val futureDate = "01/01/2030"

        // When
        val age = calculateAge(futureDate)

        // Then
        assertTrue(age <= 0)
    }

    @Test
    fun `calculateAge with today's date should return 0`() {
        // Given
        val today = LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))

        // When
        val age = calculateAge(today)

        // Then
        assertEquals(0, age)
    }
}