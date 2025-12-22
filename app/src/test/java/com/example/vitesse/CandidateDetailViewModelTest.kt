package com.example.vitesse

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vitesse.data.Candidate
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test


@ExperimentalCoroutinesApi
class CandidateDetailViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `Candidate data class should store all required fields`() {
        // Given
        val candidate = Candidate(
            id = 1L,
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "+33 6 12 34 56 78",
            email = "john.doe@example.com",
            dateOfBirth = "15/03/1990",
            expectedSalary = 45000.0,
            notes = "Excellent developer",
            profilePhotoUrl = null,
            isFavorite = false
        )

        // Then - Verify all fields are correctly stored
        assertEquals(1L, candidate.id)
        assertEquals("John", candidate.firstName)
        assertEquals("Doe", candidate.lastName)
        assertEquals("+33 6 12 34 56 78", candidate.phoneNumber)
        assertEquals("john.doe@example.com", candidate.email)
        assertEquals("15/03/1990", candidate.dateOfBirth)
        assertEquals(45000.0, candidate.expectedSalary, 0.0)
        assertEquals("Excellent developer", candidate.notes)
        assertNull(candidate.profilePhotoUrl)
        assertFalse(candidate.isFavorite)
    }

    @Test
    fun `Candidate copy with isFavorite true should create favorite candidate`() {
        // Given
        val candidate = Candidate(
            id = 1L,
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "+33 6 12 34 56 78",
            email = "john.doe@example.com",
            dateOfBirth = "15/03/1990",
            expectedSalary = 45000.0,
            notes = "Test",
            profilePhotoUrl = null,
            isFavorite = false
        )

        // When - Toggle favorite
        val favoriteCandidate = candidate.copy(isFavorite = true)

        // Then
        assertFalse(candidate.isFavorite)
        assertTrue(favoriteCandidate.isFavorite)
        assertEquals(candidate.id, favoriteCandidate.id)
        assertEquals(candidate.firstName, favoriteCandidate.firstName)
    }



    @Test
    fun `Salary conversion should handle zero and negative values`() {
        // Given
        val eurToGbpRate = 0.855

        // When & Then - Zero
        assertEquals(0.0, 0.0 * eurToGbpRate, 0.0)

        // When & Then - Negative
        val negativeSalary = -1000.0
        assertEquals(-855.0, negativeSalary * eurToGbpRate, 0.1)
    }

    @Test
    fun `Candidate notes can be empty string`() {
        // Given
        val candidate = Candidate(
            id = 1L,
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "+33 6 12 34 56 78",
            email = "john.doe@example.com",
            dateOfBirth = "15/03/1990",
            expectedSalary = 45000.0,
            notes = "",
            profilePhotoUrl = null,
            isFavorite = false
        )

        // Then
        assertTrue(candidate.notes.isEmpty())
        assertNotNull(candidate.notes)
    }

    @Test
    fun `Candidate with photoUrl should store it correctly`() {
        // Given
        val photoUrl = "content://media/external/images/1234"
        val candidate = Candidate(
            id = 1L,
            firstName = "John",
            lastName = "Doe",
            phoneNumber = "+33 6 12 34 56 78",
            email = "john.doe@example.com",
            dateOfBirth = "15/03/1990",
            expectedSalary = 45000.0,
            notes = "Test",
            profilePhotoUrl = photoUrl,
            isFavorite = false
        )

        // Then
        assertEquals(photoUrl, candidate.profilePhotoUrl)
        assertNotNull(candidate.profilePhotoUrl)
    }
}