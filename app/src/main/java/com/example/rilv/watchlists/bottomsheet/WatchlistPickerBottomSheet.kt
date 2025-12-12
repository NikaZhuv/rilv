package com.example.rilv.watchlists.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rilv.databinding.DialogWatchlistSelectorBinding
import com.example.rilv.movie.domain.model.Movie
import com.example.rilv.watchlists.wadapters.WatchlistSelectorAdapter
import com.example.rilv.moviewatch.WatchlistMovieRepository
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class WatchlistPickerBottomSheet(
    private val movie: Movie
) : BottomSheetDialogFragment() {

    private var _binding: DialogWatchlistSelectorBinding? = null
    private val binding get() = _binding!!
    @Inject lateinit var movieRepo: WatchlistMovieRepository

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogWatchlistSelectorBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val vm: WatchlistsPickerViewModel by viewModels()

        val fullPosterUrl = movie.posterPath?.let { "https://image.tmdb.org/t/p/w500$it" }

        val movieIdLong: Long = movie.id?.toLong() ?: run {
            Toast.makeText(requireContext(), "Movie id missing", Toast.LENGTH_SHORT).show()
            return
        }

        val adapter = WatchlistSelectorAdapter { wl ->
            vm.toggleMovie(
                watchlistId = wl.id,
                movieId = movieIdLong,
                poster = fullPosterUrl,
                title = movie.title,
                overview = movie.overview,
                releaseDate = movie.releaseDate,
                rating = movie.voteAverage
            )
        }

        binding.watchlistSelectRecycler.layoutManager = LinearLayoutManager(requireContext())
        binding.watchlistSelectRecycler.adapter = adapter


        // список вотчлистов
        vm.watchlists.observe(viewLifecycleOwner) { lists ->
            adapter.submitLists(lists)
        }

        // выбранные вотчлисты
        vm.getSelectedWatchlists(movie.id.toLong())
            .observe(viewLifecycleOwner) { selected ->
                adapter.setSelected(selected)
            }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
