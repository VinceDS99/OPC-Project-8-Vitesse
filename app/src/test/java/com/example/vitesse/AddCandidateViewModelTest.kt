package com.example.vitesse

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vitesse.data.CandidateRepository
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class AddCandidateViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: AddCandidateViewModel
    private lateinit var mockApplication: Application
    private lateinit var mockRepository: CandidateRepository

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockApplication = mockk(relaxed = true)
        mockRepository = mockk(relaxed = true)

        // Initialiser le ViewModel
        viewModel = AddCandidateViewModel(mockApplication)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `saveCandidate with empty firstName should show error`() {
        // When
        viewModel.saveCandidate(
            firstName = "",
            lastName = "Doe",
            phone = "0123456789",
            email = "john@test.com",
            dateOfBirth = "01/01/1990",
            expectedSalary = "45000",
            notes = "Test notes",
            profilePhotoUri = null
        )

        // Then
        assertNotNull(viewModel.firstNameError.value)
        assertFalse(viewModel.saveSuccess.value ?: true)
    }

    @Test
    fun `saveCandidate with invalid email should show error`() {
        // When
        viewModel.saveCandidate(
            firstName = "John",
            lastName = "Doe",
            phone = "0123456789",
            email = "invalid-email",
            dateOfBirth = "01/01/1990",
            expectedSalary = "45000",
            notes = "Test notes",
            profilePhotoUri = null
        )

        // Then
        assertNotNull(viewModel.emailError.value)
        assertFalse(viewModel.saveSuccess.value ?: true)
    }

    @Test
    fun `saveCandidate with valid data should pass validation`() {
        // When
        viewModel.saveCandidate(
            firstName = "John",
            lastName = "Doe",
            phone = "0123456789",
            email = "john.doe@test.com",
            dateOfBirth = "01/01/1990",
            expectedSalary = "45000",
            notes = "Test notes",
            profilePhotoUri = null
        )

        // Then - Les erreurs doivent être nulles
        assertNull(viewModel.firstNameError.value)
        assertNull(viewModel.lastNameError.value)
        assertNull(viewModel.phoneError.value)
        assertNull(viewModel.emailError.value)
        assertNull(viewModel.dateError.value)
    }

    @Test
    fun `saveCandidate with all empty fields should show multiple errors`() {
        // When
        viewModel.saveCandidate(
            firstName = "",
            lastName = "",
            phone = "",
            email = "",
            dateOfBirth = "",
            expectedSalary = "",
            notes = "",
            profilePhotoUri = null
        )

        // Then - Toutes les validations obligatoires doivent échouer
        assertNotNull(viewModel.firstNameError.value)
        assertNotNull(viewModel.lastNameError.value)
        assertNotNull(viewModel.phoneError.value)
        assertNotNull(viewModel.emailError.value)
        assertNotNull(viewModel.dateError.value)
        assertFalse(viewModel.saveSuccess.value ?: true)
    }
}
