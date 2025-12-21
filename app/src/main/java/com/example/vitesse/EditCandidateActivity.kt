package com.example.vitesse

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.example.vitesse.databinding.ActivityAddCandidateBinding
import java.util.Calendar

class EditCandidateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCandidateBinding
    private val viewModel: EditCandidateViewModel by viewModels()
    private var selectedPhotoUri: Uri? = null
    private var selectedDate: String = ""
    private var candidateId: Long = -1L
    private var currentIsFavorite: Boolean = false

    companion object {
        const val EXTRA_CANDIDATE_ID = "candidate_id"
    }

    // Photo picker launcher (ActivityResult API)
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("EditCandidate", "Photo sélectionnée: $uri")
            selectedPhotoUri = uri

            // Afficher la photo sélectionnée
            binding.ivProfilePhoto.setImageURI(uri)

            // IMPORTANT : Obtenir la permission permanente pour cette URI
            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                Log.d("EditCandidate", "Permission persistante obtenue pour: $uri")
            } catch (e: SecurityException) {
                Log.e("EditCandidate", "Impossible d'obtenir la permission persistante", e)
            }
        } else {
            Log.d("EditCandidate", "Aucune photo sélectionnée")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Activer le mode Edge-to-Edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        Log.d("EditCandidate", "Activity créée")

        binding = ActivityAddCandidateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Récupérer l'ID du candidat
        candidateId = intent.getLongExtra(EXTRA_CANDIDATE_ID, -1L)
        if (candidateId == -1L) {
            Log.e("EditCandidate", "ID candidat invalide")
            finish()
            return
        }

        // Changer le titre
        binding.tvTitle.text = getString(R.string.edit_candidate_title)

        // Gérer les insets système
        setupWindowInsets()

        setupListeners()
        setupFieldValidation()
        setupObservers()

        // Charger les données du candidat
        viewModel.loadCandidate(candidateId)
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())

            // Ajouter un padding en haut pour éviter la barre de statut
            view.setPadding(
                insets.left,
                insets.top,
                insets.right,
                insets.bottom
            )

            WindowInsetsCompat.CONSUMED
        }
    }

    private fun setupListeners() {
        // Bouton retour (flèche)
        binding.ivBack.setOnClickListener {
            Log.d("EditCandidate", "Bouton retour cliqué")
            finish() // Retour à l'écran précédent
        }

        // Sélection de photo de profil
        binding.ivProfilePhoto.setOnClickListener {
            Log.d("EditCandidate", "Photo de profil cliquée - Ouverture du sélecteur")
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Sélection de date - Clic sur le champ de texte
        binding.etDate.setOnClickListener {
            Log.d("EditCandidate", "Champ date cliqué")
            showDatePicker()
        }

        // Sélection de date - Clic sur l'icône calendrier
        binding.ivCalendar.setOnClickListener {
            Log.d("EditCandidate", "Icône calendrier cliqué")
            showDatePicker()
        }

        // Bouton sauvegarder
        binding.btnSave.setOnClickListener {
            Log.d("EditCandidate", "Bouton sauvegarder cliqué")
            updateCandidate()
        }
    }

    private fun setupFieldValidation() {
        // Effacer l'erreur du prénom dès qu'on tape
        binding.etFirstName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilFirstName.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Effacer l'erreur du nom dès qu'on tape
        binding.etLastName.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilLastName.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Effacer l'erreur du téléphone dès qu'on tape
        binding.etPhone.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilPhone.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        // Effacer l'erreur de l'email dès qu'on tape
        binding.etEmail.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.tilEmail.error = null
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Format: JJ/MM/AAAA
                selectedDate = String.format("%02d/%02d/%04d", selectedDay, selectedMonth + 1, selectedYear)
                binding.etDate.setText(selectedDate)

                // Effacer l'erreur dès qu'une date est sélectionnée
                binding.tilDate.error = null

                Log.d("EditCandidate", "Date sélectionnée: $selectedDate")
            },
            year,
            month,
            day
        )

        // Limiter la date maximale à aujourd'hui (pas de date future)
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        datePickerDialog.show()
    }

    private fun updateCandidate() {
        // Récupérer les valeurs des champs
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val phone = binding.etPhone.text.toString()
        val email = binding.etEmail.text.toString()
        val dateOfBirth = binding.etDate.text.toString()
        val salary = binding.etSalary.text.toString()
        val notes = binding.etNotes.text.toString()
        val photoUri = selectedPhotoUri?.toString()

        Log.d("EditCandidate", "Tentative de modification:")
        Log.d("EditCandidate", "  - Prénom: $firstName")
        Log.d("EditCandidate", "  - Nom: $lastName")
        Log.d("EditCandidate", "  - Téléphone: $phone")
        Log.d("EditCandidate", "  - Email: $email")
        Log.d("EditCandidate", "  - Date: $dateOfBirth")
        Log.d("EditCandidate", "  - Salaire: $salary")
        Log.d("EditCandidate", "  - Photo URI: $photoUri")

        // Appeler le ViewModel pour valider et sauvegarder
        viewModel.updateCandidate(
            candidateId = candidateId,
            firstName = firstName,
            lastName = lastName,
            phone = phone,
            email = email,
            dateOfBirth = dateOfBirth,
            expectedSalary = salary,
            notes = notes,
            profilePhotoUri = photoUri,
            isFavorite = currentIsFavorite
        )
    }

    private fun setupObservers() {
        // Observer le candidat chargé
        viewModel.candidate.observe(this) { candidate ->
            candidate?.let {
                fillFormWithCandidateData(it)
            }
        }

        // Observer le succès de la modification
        viewModel.updateSuccess.observe(this) { success ->
            if (success) {
                Log.d("EditCandidate", "Candidat modifié avec succès")
                Toast.makeText(this, R.string.candidate_updated, Toast.LENGTH_SHORT).show()
                finish() // Retour à l'écran d'accueil
            }
        }

        // Observer les erreurs pour chaque champ
        viewModel.firstNameError.observe(this) { error ->
            binding.tilFirstName.error = error
        }

        viewModel.lastNameError.observe(this) { error ->
            binding.tilLastName.error = error
        }

        viewModel.phoneError.observe(this) { error ->
            binding.tilPhone.error = error
        }

        viewModel.emailError.observe(this) { error ->
            binding.tilEmail.error = error
        }

        viewModel.dateError.observe(this) { error ->
            binding.tilDate.error = error
        }
    }

    private fun fillFormWithCandidateData(candidate: com.example.vitesse.data.Candidate) {
        // Remplir les champs avec les données existantes
        binding.etFirstName.setText(candidate.firstName)
        binding.etLastName.setText(candidate.lastName)
        binding.etPhone.setText(candidate.phoneNumber)
        binding.etEmail.setText(candidate.email)
        binding.etDate.setText(candidate.dateOfBirth)
        binding.etSalary.setText(candidate.expectedSalary.toInt().toString())
        binding.etNotes.setText(candidate.notes)

        // Sauvegarder la date
        selectedDate = candidate.dateOfBirth

        // Sauvegarder le statut favori
        currentIsFavorite = candidate.isFavorite

        // Photo de profil
        if (candidate.profilePhotoUrl != null) {
            try {
                val uri = Uri.parse(candidate.profilePhotoUrl)
                selectedPhotoUri = uri
                binding.ivProfilePhoto.setImageURI(uri)
            } catch (e: Exception) {
                binding.ivProfilePhoto.setImageResource(R.drawable.default_add_profile_picture)
            }
        } else {
            binding.ivProfilePhoto.setImageResource(R.drawable.default_add_profile_picture)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d("EditCandidate", "Activity détruite")
    }
}