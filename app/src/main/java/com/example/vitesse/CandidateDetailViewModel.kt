package com.example.vitesse

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vitesse.data.AppDatabase
import com.example.vitesse.data.Candidate
import com.example.vitesse.data.CandidateRepository
import kotlinx.coroutines.launch

class CandidateDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CandidateRepository

    private val _candidate = MutableLiveData<Candidate?>()
    val candidate: LiveData<Candidate?> = _candidate

    private val _deleteSuccess = MutableLiveData<Boolean>()
    val deleteSuccess: LiveData<Boolean> = _deleteSuccess

    private val _favoriteUpdated = MutableLiveData<Boolean>()
    val favoriteUpdated: LiveData<Boolean> = _favoriteUpdated

    init {
        val candidateDao = AppDatabase.getDatabase(application, viewModelScope).candidateDao()
        repository = CandidateRepository(candidateDao)
    }

    fun loadCandidate(candidateId: Long) {
        viewModelScope.launch {
            val candidate = repository.getCandidateById(candidateId)
            _candidate.value = candidate
        }
    }

    fun deleteCandidate() {
        viewModelScope.launch {
            _candidate.value?.let { candidate ->
                try {
                    repository.delete(candidate)
                    _deleteSuccess.value = true
                } catch (e: Exception) {
                    _deleteSuccess.value = false
                }
            }
        }
    }

    fun toggleFavorite() {
        viewModelScope.launch {
            _candidate.value?.let { candidate ->
                try {
                    val updatedCandidate = candidate.copy(isFavorite = !candidate.isFavorite)
                    repository.update(updatedCandidate)
                    _candidate.value = updatedCandidate
                    _favoriteUpdated.value = updatedCandidate.isFavorite
                } catch (e: Exception) {
                    _favoriteUpdated.value = null
                }
            }
        }
    }
}