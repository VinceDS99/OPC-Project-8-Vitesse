package com.example.vitesse

import android.app.Application
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.vitesse.data.Candidate
import com.example.vitesse.data.CandidateRepository
import io.mockk.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CandidatesViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = UnconfinedTestDispatcher()

    private lateinit var viewModel: CandidatesViewModel
    private lateinit var mockApplication: Application

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)

        mockApplication = mockk(relaxed = true)

        // Initialiser le ViewModel
        viewModel = CandidatesViewModel(mockApplication)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setActiveTab to ALL should update active tab`() = runTest {
        // When
        viewModel.setActiveTab(TabType.ALL)
        advanceUntilIdle()

        // Then
        assertEquals(TabType.ALL, viewModel.activeTab.value)
    }

    @Test
    fun `setActiveTab to FAVORITES should filter favorites`() = runTest {
        // When
        viewModel.setActiveTab(TabType.FAVORITES)
        advanceUntilIdle()

        // Then
        assertEquals(TabType.FAVORITES, viewModel.activeTab.value)
    }

    @Test
    fun `Search should filter candidates by first name`() = runTest {
        // When
        viewModel.setSearchQuery("Jean")
        advanceUntilIdle()

        // Then
        assertEquals("Jean", viewModel.searchQuery.value)
    }

    @Test
    fun `Search should filter candidates by last name`() = runTest {
        // When
        viewModel.setSearchQuery("Dupont")
        advanceUntilIdle()

        // Then
        assertEquals("Dupont", viewModel.searchQuery.value)
    }

    @Test
    fun `Search with empty string should show all candidates`() = runTest {
        // When
        viewModel.setSearchQuery("")
        advanceUntilIdle()

        // Then
        assertEquals("", viewModel.searchQuery.value)
    }

    @Test
    fun `search should be case insensitive`() = runTest {
        // When
        viewModel.setSearchQuery("JEAN")
        advanceUntilIdle()

        // Then
        assertEquals("JEAN", viewModel.searchQuery.value)
    }

    @Test
    fun `isLoading should be false after data loaded`() = runTest {
        // Given - Attendre que le chargement se termine
        advanceTimeBy(2000)
        advanceUntilIdle()

        // Then
        assertFalse(viewModel.isLoading.value ?: true)
    }
}