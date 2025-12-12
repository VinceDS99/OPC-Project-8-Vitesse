package com.example.vitesse

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.vitesse.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val viewModel: CandidatesViewModel by viewModels()
    private lateinit var adapter: CandidateAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Activer le mode Edge-to-Edge
        WindowCompat.setDecorFitsSystemWindows(window, false)

        Log.d("MainActivity", "Application démarrée")

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Gérer les insets système (barre de statut, navigation)
        setupWindowInsets()

        setupRecyclerView()
        setupTabs()
        setupObservers()

        // Observer les changements de candidats
        viewModel.observeCandidates()
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

    private fun setupRecyclerView() {
        adapter = CandidateAdapter { candidate ->
            // TODO: Gérer le clic sur un candidat (navigation vers les détails)
            Log.d("MainActivity", "Candidat cliqué: ${candidate.firstName} ${candidate.lastName}")
        }

        binding.rvCandidates.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = this@MainActivity.adapter
        }
    }

    private fun setupTabs() {
        // Clic sur l'onglet "Tous"
        binding.tabAll.setOnClickListener {
            viewModel.setActiveTab(TabType.ALL)
        }

        // Clic sur l'onglet "Favoris"
        binding.tabFavorites.setOnClickListener {
            viewModel.setActiveTab(TabType.FAVORITES)
        }
    }

    private fun setupObservers() {
        // Observer l'état de chargement
        viewModel.isLoading.observe(this) { isLoading ->
            Log.d("MainActivity", "État de chargement: $isLoading")

            // Afficher/masquer le loader
            binding.loadingLayout.isVisible = isLoading

            // Si le chargement est terminé, afficher le contenu
            if (!isLoading) {
                updateContentVisibility()
            }
        }

        // Observer l'onglet actif
        viewModel.activeTab.observe(this) { tab ->
            updateTabUI(tab)
        }

        // Observer les candidats affichés
        viewModel.displayedCandidates.observe(this) { candidates ->
            Log.d("MainActivity", "Nombre de candidats: ${candidates.size}")

            // Soumettre toujours la liste à l'adapter
            adapter.submitList(candidates)

            // Mettre à jour la visibilité si le chargement est terminé
            if (viewModel.isLoading.value == false) {
                updateContentVisibility()
            }
        }
    }

    private fun updateContentVisibility() {
        val candidates = viewModel.displayedCandidates.value ?: emptyList()
        val isLoading = viewModel.isLoading.value ?: false

        // Ne rien afficher si encore en chargement
        if (isLoading) {
            binding.rvCandidates.isVisible = false
            binding.tvNoCandidate.isVisible = false
            return
        }

        // Afficher selon le contenu
        val hasNoCandidates = candidates.isEmpty()
        binding.rvCandidates.isVisible = !hasNoCandidates
        binding.tvNoCandidate.isVisible = hasNoCandidates

        Log.d("MainActivity", "Visibilité mise à jour - Candidats: ${candidates.size}, RecyclerView visible: ${!hasNoCandidates}")
    }

    private fun updateTabUI(activeTab: TabType) {
        when (activeTab) {
            TabType.ALL -> {
                // Style "Tous" actif
                binding.tvTabAll.apply {
                    textSize = 16f
                    setTextColor(getColor(R.color.primary))
                }
                binding.indicatorAll.isVisible = true

                // Style "Favoris" inactif
                binding.tvTabFavorites.apply {
                    textSize = 16f
                    setTextColor(getColor(android.R.color.darker_gray))
                }
                binding.indicatorFavorites.isVisible = false
            }
            TabType.FAVORITES -> {
                // Style "Tous" inactif
                binding.tvTabAll.apply {
                    textSize = 16f
                    setTextColor(getColor(android.R.color.darker_gray))
                }
                binding.indicatorAll.isVisible = false

                // Style "Favoris" actif
                binding.tvTabFavorites.apply {
                    textSize = 16f
                    setTextColor(getColor(R.color.primary))
                }
                binding.indicatorFavorites.isVisible = true
            }
        }
    }
}