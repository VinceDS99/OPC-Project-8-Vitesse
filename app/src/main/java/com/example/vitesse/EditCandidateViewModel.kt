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

class EditCandidateViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: CandidateRepository

    private val _candidate = MutableLiveData<Candidate?>()
    val candidate: LiveData<Candidate?> = _candidate

    private val _updateSuccess = MutableLiveData<Boolean>()
    val updateSuccess: LiveData<Boolean> = _updateSuccess

    // Erreurs pour chaque champ
    private val _firstNameError = MutableLiveData<String?>()
    val firstNameError: LiveData<String?> = _firstNameError

    private val _lastNameError = MutableLiveData<String?>()
    val lastNameError: LiveData<String?> = _lastNameError

    private val _phoneError = MutableLiveData<String?>()
    val phoneError: LiveData<String?> = _phoneError

    private val _emailError = MutableLiveData<String?>()
    val emailError: LiveData<String?> = _emailError

    private val _dateError = MutableLiveData<String?>()
    val dateError: LiveData<String?> = _dateError

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

    fun updateCandidate(
        candidateId: Long,
        firstName: String,
        lastName: String,
        phone: String,
        email: String,
        dateOfBirth: String,
        expectedSalary: String,
        notes: String,
        profilePhotoUri: String?,
        isFavorite: Boolean
    ) {
        // Réinitialiser toutes les erreurs
        clearAllErrors()

        var hasError = false

        // Validation : Prénom obligatoire
        if (firstName.isBlank()) {
            _firstNameError.value = getApplication<Application>().getString(R.string.mandatory_field)
            hasError = true
        }

        // Validation : Nom obligatoire
        if (lastName.isBlank()) {
            _lastNameError.value = getApplication<Application>().getString(R.string.mandatory_field)
            hasError = true
        }

        // Validation : Téléphone obligatoire
        if (phone.isBlank()) {
            _phoneError.value = getApplication<Application>().getString(R.string.mandatory_field)
            hasError = true
        }

        // Validation : Email obligatoire
        if (email.isBlank()) {
            _emailError.value = getApplication<Application>().getString(R.string.mandatory_field)
            hasError = true
        } else if (!isValidEmail(email)) {
            // Validation : Format email invalide
            _emailError.value = getApplication<Application>().getString(R.string.invalid_format)
            hasError = true
        }

        // Validation : Date de naissance obligatoire
        if (dateOfBirth.isBlank()) {
            _dateError.value = getApplication<Application>().getString(R.string.mandatory_field)
            hasError = true
        }

        // Si au moins une erreur, ne pas sauvegarder
        if (hasError) {
            _updateSuccess.value = false
            return
        }

        // Créer le candidat mis à jour
        val updatedCandidate = Candidate(
            id = candidateId,
            firstName = firstName.trim(),
            lastName = lastName.trim(),
            phoneNumber = phone.trim(),
            email = email.trim(),
            dateOfBirth = dateOfBirth,
            expectedSalary = expectedSalary.toDoubleOrNull() ?: 0.0,
            notes = notes.trim(),
            profilePhotoUrl = profilePhotoUri,
            isFavorite = isFavorite
        )

        // Sauvegarder dans la base de données
        viewModelScope.launch {
            try {
                repository.update(updatedCandidate)
                _updateSuccess.value = true
            } catch (e: Exception) {
                _updateSuccess.value = false
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    private fun clearAllErrors() {
        _firstNameError.value = null
        _lastNameError.value = null
        _phoneError.value = null
        _emailError.value = null
        _dateError.value = null
    }
}