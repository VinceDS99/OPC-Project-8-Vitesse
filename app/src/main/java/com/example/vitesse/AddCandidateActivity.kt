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

class AddCandidateActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddCandidateBinding
    private val viewModel: AddCandidateViewModel by viewModels()
    private var selectedPhotoUri: Uri? = null
    private var selectedDate: String = ""

    // Photo picker launcher (ActivityResult API)
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri != null) {
            Log.d("AddCandidate", "Photo sélectionnée: $uri")
            selectedPhotoUri = uri

            // Afficher la photo sélectionnée
            binding.ivProfilePhoto.setImageURI(uri)


            try {
                contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
                Log.d("AddCandidate", "Permission persistante obtenue pour: $uri")
            } catch (e: SecurityException) {
                Log.e("AddCandidate", "Impossible d'obtenir la permission persistante", e)
            }
        } else {
            Log.d("AddCandidate", "Aucune photo sélectionnée")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Activer le mode Edge-to-Edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        Log.d("AddCandidate", "Activity créée")

        binding = ActivityAddCandidateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Gérer les insets système
        setupWindowInsets()

        setupListeners()
        setupFieldValidation()
        setupObservers()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { view, windowInsets ->
            val insets = windowInsets.getInsets(WindowInsetsCompat.Type.systemBars())


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
            finish() // Retour à l'écran précédent
        }

        // Sélection de photo de profil
        binding.ivProfilePhoto.setOnClickListener {
            Log.d("AddCandidate", "Photo de profil cliquée - Ouverture du sélecteur")
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        // Sélection de date - Clic sur le champ de texte
        binding.etDate.setOnClickListener {
            Log.d("AddCandidate", "Champ date cliqué")
            showDatePicker()
        }

        // Sélection de date - Clic sur l'icône calendrier
        binding.ivCalendar.setOnClickListener {
            Log.d("AddCandidate", "Icône calendrier cliqué")
            showDatePicker()
        }

        // Bouton sauvegarder
        binding.btnSave.setOnClickListener {
            Log.d("AddCandidate", "Bouton sauvegarder cliqué")
            saveCandidate()
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

                Log.d("AddCandidate", "Date sélectionnée: $selectedDate")
            },
            year,
            month,
            day
        )

        // Limiter la date maximale à aujourd'hui (pas de date future)
        datePickerDialog.datePicker.maxDate = calendar.timeInMillis

        datePickerDialog.show()
    }

    private fun saveCandidate() {
        // Récupérer les valeurs des champs
        val firstName = binding.etFirstName.text.toString()
        val lastName = binding.etLastName.text.toString()
        val phone = binding.etPhone.text.toString()
        val email = binding.etEmail.text.toString()
        val dateOfBirth = binding.etDate.text.toString()
        val salary = binding.etSalary.text.toString()
        val notes = binding.etNotes.text.toString()
        val photoUri = selectedPhotoUri?.toString()

        Log.d("AddCandidate", "Tentative de sauvegarde:")
        Log.d("AddCandidate", "  - Prénom: $firstName")
        Log.d("AddCandidate", "  - Nom: $lastName")
        Log.d("AddCandidate", "  - Téléphone: $phone")
        Log.d("AddCandidate", "  - Email: $email")
        Log.d("AddCandidate", "  - Date: $dateOfBirth")
        Log.d("AddCandidate", "  - Salaire: $salary")
        Log.d("AddCandidate", "  - Notes: ${notes.take(50)}...")
        Log.d("AddCandidate", "  - Photo URI: $photoUri")

        // Appeler le ViewModel pour valider et sauvegarder
        viewModel.saveCandidate(
            firstName = firstName,
            lastName = lastName,
            phone = phone,
            email = email,
            dateOfBirth = dateOfBirth,
            expectedSalary = salary,
            notes = notes,
            profilePhotoUri = photoUri
        )
    }

    private fun setupObservers() {
        // Observer le succès de la sauvegarde
        viewModel.saveSuccess.observe(this) { success ->
            if (success) {
                Log.d("AddCandidate", "Candidat sauvegardé avec succès")
                Toast.makeText(this, R.string.candidate_added, Toast.LENGTH_SHORT).show()
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

    override fun onDestroy() {
        super.onDestroy()
        Log.d("AddCandidate", "Activity détruite")
    }
}