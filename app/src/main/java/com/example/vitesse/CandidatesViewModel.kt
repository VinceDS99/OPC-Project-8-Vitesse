package com.example.vitesse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.vitesse.data.AppDatabase
import com.example.vitesse.data.Candidate
import com.example.vitesse.data.CandidateRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CandidatesViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CandidateRepository

    // Liste de tous les candidats
    val allCandidates: LiveData<List<Candidate>>

    // Liste des favoris
    val favoriteCandidates: LiveData<List<Candidate>>

    // État de chargement
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // Onglet actif (true = Tous, false = Favoris)
    private val _activeTab = MutableLiveData<TabType>(TabType.ALL)
    val activeTab: LiveData<TabType> = _activeTab

    // Liste affichée selon l'onglet actif
    private val _displayedCandidates = MutableLiveData<List<Candidate>>()
    val displayedCandidates: LiveData<List<Candidate>> = _displayedCandidates

    init {
        val candidateDao = AppDatabase.getDatabase(application, viewModelScope).candidateDao()
        repository = CandidateRepository(candidateDao)

        allCandidates = repository.allCandidates.asLiveData()
        favoriteCandidates = repository.favoriteCandidates.asLiveData()

        loadData()
    }

    private fun loadData() {
        viewModelScope.launch {
            _isLoading.value = true
            // Simulation d'un chargement
            delay(1500)
            _isLoading.value = false
        }
    }

    fun setActiveTab(tab: TabType) {
        _activeTab.value = tab
        updateDisplayedCandidates()
    }

    private fun updateDisplayedCandidates() {
        _displayedCandidates.value = when (_activeTab.value) {
            TabType.ALL -> allCandidates.value ?: emptyList()
            TabType.FAVORITES -> favoriteCandidates.value ?: emptyList()
            null -> emptyList()
        }
    }

    // Observer les changements de candidats
    fun observeCandidates() {
        allCandidates.observeForever { candidates ->
            if (_activeTab.value == TabType.ALL) {
                _displayedCandidates.value = candidates
            }
        }

        favoriteCandidates.observeForever { candidates ->
            if (_activeTab.value == TabType.FAVORITES) {
                _displayedCandidates.value = candidates
            }
        }
    }
}

enum class TabType {
    ALL,
    FAVORITES
}