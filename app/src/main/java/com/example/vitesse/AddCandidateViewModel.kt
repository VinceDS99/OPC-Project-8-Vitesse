package com.example.vitesse

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.vitesse.data.AppDatabase
import com.example.vitesse.data.Candidate
import com.example.vitesse.data.CandidateRepository
import kotlinx.coroutines.launch

class AddCandidateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CandidateRepository

    private val _saveSuccess = MutableLiveData<Boolean>()
    val saveSuccess: LiveData<Boolean> = _saveSuccess

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        val candidateDao = AppDatabase.getDatabase(application, viewModelScope).candidateDao()
        repository = CandidateRepository(candidateDao)
    }

    fun saveCandidate(
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        dateOfBirth: String,
        expectedSalary: String,
        notes: String,
        profilePhotoUri: String?
    ) {
        // Validation
        when {
            firstName.isBlank() -> {
                _errorMessage.value = getApplication<Application>().getString(R.string.error_first_name_required)
                return
            }
            lastName.isBlank() -> {
                _errorMessage.value = getApplication<Application>().getString(R.string.error_last_name_required)
                return
            }
            phone.isBlank() -> {
                _errorMessage.value = getApplication<Application>().getString(R.string.error_phone_required)
                return
            }
            email.isBlank() -> {
                _errorMessage.value = getApplication<Application>().getString(R.string.error_email_required)
                return
            }
            !isValidEmail(email) -> {
                _errorMessage.value = getApplication<Application>().getString(R.string.error_email_invalid)
                return
            }
            dateOfBirth.isBlank() -> {
                _errorMessage.value = getApplication<Application>().getString(R.string.error_date_required)
                return
            }
            expectedSalary.isBlank() -> {
                _errorMessage.value = getApplication<Application>().getString(R.string.error_salary_required)
                return
            }
            notes.isBlank() -> {
                _errorMessage.value = getApplication<Application>().getString(R.string.error_notes_required)
                return
            }
        }

        // Créer le candidat
        val candidate = Candidate(
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            phoneNumber = phone.trim(),
            email = email.trim(),
            dateOfBirth = dateOfBirth,
            expectedSalary = expectedSalary.toDoubleOrNull() ?: 0.0,
            notes = notes.trim(),
            profilePhotoUrl = profilePhotoUri,
            isFavorite = false
        )

        // Sauvegarder dans la base de données
        viewModelScope.launch {
            try {
                repository.insert(candidate)
                _saveSuccess.value = true
            } catch (e: Exception) {
                _errorMessage.value = e.message
                _saveSuccess.value = false
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun clearError() {
        _errorMessage.value = null
    }
}