package com.example.vitesse

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.example.vitesse.databinding.ActivityCandidateDetailBinding
import java.text.NumberFormat
import java.time.LocalDate
import java.time.Period
import java.time.format.DateTimeFormatter
import java.util.Locale

class CandidateDetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCandidateDetailBinding
    private val viewModel: CandidateDetailViewModel by viewModels()

    private var phoneNumber: String = ""
    private var emailAddress: String = ""

    companion object {
        const val EXTRA_CANDIDATE_ID = "candidate_id"
        const val EUR_TO_GBP_RATE = 0.855 // Taux de conversion
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Activer le mode Edge-to-Edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        binding = ActivityCandidateDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Récupérer l'ID du candidat
        candidateId = intent.getLongExtra(EXTRA_CANDIDATE_ID, -1L)  // ← Sauvegarder l'ID
        if (candidateId == -1L) {
            Log.e("CandidateDetail", "ID candidat invalide")
            finish()
            return
        }

        setupListeners()
        setupObservers()

        // Charger les données du candidat
        viewModel.loadCandidate(candidateId)
    }

    private var candidateId: Long = -1L

    private fun setupListeners() {
        // Bouton retour
        binding.ivBack.setOnClickListener {
            finish()
        }

        // Icône favoris
        binding.ivFavorite.apply {
            alpha = 1.0f
            isClickable = true
            isFocusable = true
            setOnClickListener {
                viewModel.toggleFavorite()
            }
        }

        // Icône modifier
        binding.ivEdit.apply {
            isClickable = true
            isFocusable = true
            setOnClickListener {
                // Navigation vers l'écran de modification
                val intent = Intent(this@CandidateDetailActivity, EditCandidateActivity::class.java)
                intent.putExtra(EditCandidateActivity.EXTRA_CANDIDATE_ID, candidateId)
                startActivity(intent)
                finish() // Fermer l'écran de détails
            }
        }

        // Icône supprimer
        binding.ivDelete.apply {
            alpha = 1.0f
            isClickable = true
            isFocusable = true
            setOnClickListener {
                showDeleteConfirmationDialog()
            }
        }

        // Action Téléphone
        binding.llPhone.setOnClickListener {
            if (phoneNumber.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_DIAL).apply {
                    data = Uri.parse("tel:$phoneNumber")
                }
                startActivity(intent)
            }
        }

        // Action SMS
        binding.llSms.setOnClickListener {
            if (phoneNumber.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("smsto:$phoneNumber")
                }
                startActivity(intent)
            }
        }

        // Action Email
        binding.llEmail.setOnClickListener {
            if (emailAddress.isNotEmpty()) {
                val intent = Intent(Intent.ACTION_SENDTO).apply {
                    data = Uri.parse("mailto:$emailAddress")
                }
                startActivity(intent)
            }
        }
    }

    private fun showDeleteConfirmationDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_delete_confirmation, null)

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)
        dialog.window?.setDimAmount(0.7f)


        dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnCancel).setOnClickListener {
            dialog.dismiss()
        }

        dialogView.findViewById<com.google.android.material.button.MaterialButton>(R.id.btnConfirm).setOnClickListener {
            viewModel.deleteCandidate()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setupObservers() {
        // Observer le candidat
        viewModel.candidate.observe(this) { candidate ->
            candidate?.let {
                displayCandidateDetails(it)
            }
        }

        // Observer la suppression réussie
        viewModel.deleteSuccess.observe(this) { success ->
            if (success) {
                Toast.makeText(this, R.string.candidate_deleted, Toast.LENGTH_SHORT).show()
                finish() // Retour à l'écran d'accueil
            }
        }

        // Observer le changement de favori
        viewModel.favoriteUpdated.observe(this) { isFavorite ->
            when (isFavorite) {
                true -> {
                    Toast.makeText(this, R.string.add_to_favorites, Toast.LENGTH_SHORT).show()
                }
                false -> {
                    Toast.makeText(this, R.string.remove_from_favorites, Toast.LENGTH_SHORT).show()
                }
                null -> {
                    // Erreur
                }
            }
        }
    }

    private fun displayCandidateDetails(candidate: com.example.vitesse.data.Candidate) {
        // Nom complet
        binding.tvCandidateName.text = "${candidate.firstName} ${candidate.lastName.uppercase(Locale.getDefault())}"

        // Photo de profil
        if (candidate.profilePhotoUrl != null) {
            try {
                val uri = Uri.parse(candidate.profilePhotoUrl)
                binding.ivProfilePhoto.setImageURI(uri)
            } catch (e: Exception) {
                binding.ivProfilePhoto.setImageResource(R.drawable.default_profile_picture)
            }
        } else {
            binding.ivProfilePhoto.setImageResource(R.drawable.default_profile_picture)
        }

        // Icône favoris - Mise à jour de l'affichage
        updateFavoriteIcon(candidate.isFavorite)

        // Date de naissance et âge
        binding.tvDateOfBirth.text = candidate.dateOfBirth
        val age = calculateAge(candidate.dateOfBirth)
        binding.tvDateOfBirth.text = "${candidate.dateOfBirth} (${String.format(getString(R.string.years_old), age)})"

        // Salaire en euros
        val euroFormat = NumberFormat.getNumberInstance(Locale.FRANCE)
        binding.tvSalaryEuro.text = "${euroFormat.format(candidate.expectedSalary)} €"

        // Salaire en livres
        val salaryInPounds = candidate.expectedSalary * EUR_TO_GBP_RATE
        binding.tvSalaryPound.text = String.format(Locale.UK, "%s £ %.2f", getString(R.string.conversion), salaryInPounds)

        // Notes
        binding.tvNotes.text = candidate.notes

        // Sauvegarder pour les actions
        phoneNumber = candidate.phoneNumber
        emailAddress = candidate.email
    }

    private fun updateFavoriteIcon(isFavorite: Boolean) {
        if (isFavorite) {
            binding.ivFavorite.setImageResource(R.drawable.black_star_icon)
            binding.ivFavorite.contentDescription = getString(R.string.remove_from_favorites)
        } else {
            binding.ivFavorite.setImageResource(R.drawable.empty_star_icon)
            binding.ivFavorite.contentDescription = getString(R.string.add_to_favorites)
        }
    }

    private fun calculateAge(dateOfBirth: String): Int {
        return try {
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            val birthDate = LocalDate.parse(dateOfBirth, formatter)
            val currentDate = LocalDate.now()
            Period.between(birthDate, currentDate).years
        } catch (e: Exception) {
            Log.e("CandidateDetail", "Erreur calcul âge: ${e.message}")
            0
        }
    }
}