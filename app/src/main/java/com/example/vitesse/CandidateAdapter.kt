package com.example.vitesse

import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.vitesse.data.Candidate
import com.example.vitesse.databinding.ItemCandidateBinding
import java.util.Locale

class CandidateAdapter(
    private val onCandidateClick: (Candidate) -> Unit
) : ListAdapter<Candidate, CandidateAdapter.CandidateViewHolder>(CandidateDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CandidateViewHolder {
        val binding = ItemCandidateBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return CandidateViewHolder(binding, onCandidateClick)
    }

    override fun onBindViewHolder(holder: CandidateViewHolder, position: Int) {
        val candidate = getItem(position)
        holder.bind(candidate)
    }

    class CandidateViewHolder(
        private val binding: ItemCandidateBinding,
        private val onCandidateClick: (Candidate) -> Unit
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(candidate: Candidate) {
            binding.apply {
                // Nom complet : Prénom + NOM DE FAMILLE EN MAJUSCULES
                tvName.text = "${candidate.firstName} ${candidate.lastName.uppercase(Locale.getDefault())}"

                // Notes
                tvNotes.text = candidate.notes

                // Photo de profil
                if (candidate.profilePhotoUrl != null) {
                    try {
                        // Charger l'image depuis l'URI
                        val uri = Uri.parse(candidate.profilePhotoUrl)
                        ivProfilePhoto.setImageURI(uri)
                        Log.d("CandidateAdapter", "Image chargée depuis URI: $uri")
                    } catch (e: Exception) {
                        Log.e("CandidateAdapter", "Erreur lors du chargement de l'image: ${e.message}")
                        // En cas d'erreur, utiliser l'image par défaut
                        ivProfilePhoto.setImageResource(R.drawable.default_profile_picture)
                    }
                } else {
                    // Photo par défaut si aucune URI
                    ivProfilePhoto.setImageResource(R.drawable.default_profile_picture)
                }

                // Click listener
                root.setOnClickListener {
                    onCandidateClick(candidate)
                }
            }
        }
    }

    class CandidateDiffCallback : DiffUtil.ItemCallback<Candidate>() {
        override fun areItemsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Candidate, newItem: Candidate): Boolean {
            return oldItem == newItem
        }
    }
}