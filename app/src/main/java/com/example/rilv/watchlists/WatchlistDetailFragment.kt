package com.example.rilv.watchlists

import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.rilv.R
import com.example.rilv.databinding.FragmentWatchlistDetailBinding
import dagger.hilt.android.AndroidEntryPoint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rilv.watchlists.wadapters.WatchlistDetailMoviesAdapter
import com.bumptech.glide.Glide

@AndroidEntryPoint
class WatchlistDetailFragment : Fragment(R.layout.fragment_watchlist_detail) {

    private var _binding: FragmentWatchlistDetailBinding? = null
    private val binding get() = _binding!!

    private val detailVm: WatchlistDetailViewModel by viewModels()
    private val listsVm: WatchlistsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentWatchlistDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWatchlistDetailBinding.bind(view)

        // відступ нав меню
        val bottomNav = requireActivity().findViewById<View>(R.id.bottomNavView)
        bottomNav.post {
            binding.scrollView.setPadding(
                binding.scrollView.paddingLeft,
                binding.scrollView.paddingTop,
                binding.scrollView.paddingRight,
                bottomNav.height
            )
        }

        //----------------------
        detailVm.movies.observe(viewLifecycleOwner) { movies ->

            val posters = listOf(binding.poster1, binding.poster2, binding.poster3, binding.poster4)

            posters.forEach {
                it.setImageDrawable(null)
                it.setBackgroundResource(R.drawable.rounded_imagesmode_24)
            }

            movies.take(4).forEachIndexed { i, m ->
                Glide.with(requireContext())
                    .load(m.posterUrl)
                    .into(posters[i])
            }
        }

        val moviesAdapter = WatchlistDetailMoviesAdapter { movie ->
            detailVm.removeMovie(movie.watchlistId, movie.movieId)
        }

        binding.watchlistMoviesRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.watchlistMoviesRecycler.adapter = moviesAdapter

        detailVm.movies.observe(viewLifecycleOwner) { movies ->
            moviesAdapter.submitList(movies)
        }
        //----------------------

        val id = detailVm.watchlistId

        listsVm.watchlists.observe(viewLifecycleOwner) { list ->
            val wl = list.firstOrNull { it.id == id }

            if (wl != null) {
                binding.watchlistTitle.text = wl.name
                binding.watchlistDescription.text = wl.description ?: "No description"
            }
        }

        binding.backButton.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.deleteButton.setOnClickListener {
            showDeleteConfirmDialog()
        }
    }

    private fun showDeleteConfirmDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete watchlist")
            .setMessage("Are you sure you want to delete this watchlist?")
            .setPositiveButton("Delete") { _, _ ->

                detailVm.deleteWatchlist(
                    onSuccess = {
                        findNavController().popBackStack()
                    },
                    onFailed = {
                        Toast.makeText(
                            requireContext(),
                            "Не можна видалити останній вотчліст!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )

            }
            .setNegativeButton("Cancel", null)
            .show()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
