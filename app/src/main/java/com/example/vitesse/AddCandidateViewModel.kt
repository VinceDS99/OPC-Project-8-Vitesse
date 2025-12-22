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
            _saveSuccess.value = false
            return
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
                _saveSuccess.value = false
            }
        }
    }

    private fun isValidEmail(email: String): Boolean {
        // Essayer d'utiliser Patterns.EMAIL_ADDRESS si disponible (runtime Android)
        // Sinon utiliser une regex simple (pour les tests unitaires)
        return try {
            Patterns.EMAIL_ADDRESS?.matcher(email)?.matches() ?: isValidEmailRegex(email)
        } catch (e: Exception) {
            // Fallback sur regex si Patterns n'est pas disponible
            isValidEmailRegex(email)
        }
    }

    /**
     * Validation email par regex pour les tests unitaires
     * et comme fallback si Patterns.EMAIL_ADDRESS n'est pas disponible
     */
    private fun isValidEmailRegex(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$".toRegex()
        return emailRegex.matches(email)
    }

    private fun clearAllErrors() {
        _firstNameError.value = null
        _lastNameError.value = null
        _phoneError.value = null
        _emailError.value = null
        _dateError.value = null
    }
}