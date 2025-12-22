package com.example.vitesse.utils

import org.junit.Assert.*
import org.junit.Test

/**
 * Tests unitaires pour la validation d'email
 */
class EmailValidatorTest {


    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    @Test
    fun `valid email addresses should pass validation`() {
        val validEmails = listOf(
            "test@example.com",
            "user.name@example.com",
            "user+tag@example.co.uk",
            "user_name@example-domain.com"
        )

        validEmails.forEach { email ->
            assertTrue("$email should be valid", isValidEmail(email))
        }
    }

    @Test
    fun `invalid email addresses should fail validation`() {
        val invalidEmails = listOf(
            "invalid-email",
            "@example.com",
            "user@",
            "user@.com"
        )

        invalidEmails.forEach { email ->
            assertFalse("$email should be invalid", isValidEmail(email))
        }
    }

    @Test
    fun `empty email should fail validation`() {
        assertFalse(isValidEmail(""))
    }

    @Test
    fun `email with spaces should fail validation`() {
        assertFalse(isValidEmail("user name@example.com"))
    }
}