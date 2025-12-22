package com.example.vitesse.data

import org.junit.Assert.*
import org.junit.Test

class CandidateTest {

    @Test
    fun `candidate creation with all fields should succeed`() {
        // Given
        val candidate = Candidate(
            id = 1L,
            firstName = "Jean",
            lastName = "Dupont",
            phoneNumber = "+33 6 12 34 56 78",
            email = "jean.dupont@email.com",
            dateOfBirth = "15/03/1990",
            expectedSalary = 45000.0,
            notes = "Développeur expérimenté",
            profilePhotoUrl = null,
            isFavorite = false
        )

        // Then
        assertEquals("Jean", candidate.firstName)
        assertEquals("Dupont", candidate.lastName)
        assertEquals(45000.0, candidate.expectedSalary, 0.01)
        assertFalse(candidate.isFavorite)
    }

    @Test
    fun `candidate with null photo url should use default`() {
        // Given
        val candidate = Candidate(
            firstName = "Marie",
            lastName = "Martin",
            phoneNumber = "+33 6 23 45 67 89",
            email = "marie.martin@email.com",
            dateOfBirth = "22/07/1988",
            expectedSalary = 52000.0,
            notes = "Chef de projet",
            profilePhotoUrl = null
        )

        // Then
        assertNull(candidate.profilePhotoUrl)
    }

    @Test
    fun `candidate favorite status should toggle correctly`() {
        // Given
        val candidate = Candidate(
            firstName = "Thomas",
            lastName = "Bernard",
            phoneNumber = "+33 6 34 56 78 90",
            email = "thomas.bernard@email.com",
            dateOfBirth = "10/11/1992",
            expectedSalary = 38000.0,
            notes = "Designer UX/UI",
            isFavorite = false
        )

        // When
        val updatedCandidate = candidate.copy(isFavorite = true)

        // Then
        assertFalse(candidate.isFavorite)
        assertTrue(updatedCandidate.isFavorite)
    }


    @Test
    fun `candidate with different id should not be equal`() {
        // Given
        val candidate1 = Candidate(
            id = 1L,
            firstName = "Lucas",
            lastName = "Robert",
            phoneNumber = "+33 6 56 78 90 12",
            email = "lucas.robert@email.com",
            dateOfBirth = "18/09/1991",
            expectedSalary = 48000.0,
            notes = "DevOps"
        )

        val candidate2 = candidate1.copy(id = 2L)

        // Then
        assertNotEquals(candidate1, candidate2)
    }
}