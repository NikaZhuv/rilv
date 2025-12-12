package com.example.rilv.ui

import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rilv.R
import com.example.rilv.adapters.HorizontalMovieAdapter
import com.example.rilv.adapters.VerticalMovieAdapter
import com.example.rilv.databinding.FragmentTrendsBinding
import com.example.rilv.movie.presentation.MovieListUiEvent
import com.example.rilv.movie.presentation.MovieListViewModel
import com.example.rilv.utils.Category
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.getValue
import com.example.rilv.watchlists.bottomsheet.WatchlistPickerBottomSheet


@AndroidEntryPoint
class TrendsFragment : Fragment(R.layout.fragment_trends) {

    private var _binding: FragmentTrendsBinding? = null
    private val binding get() = _binding!!

    private val movieListViewModel: MovieListViewModel by activityViewModels()

    private lateinit var upcomingAdapter: HorizontalMovieAdapter
    private lateinit var trendsAdapter: VerticalMovieAdapter


    // сохраненние listener
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentTrendsBinding.bind(view)

        // відступ над навігаційною панеллю
        val bottomNav = requireActivity().findViewById<View>(R.id.bottomNavView)
        bottomNav.post {
            binding.scrollView.setPadding(
                binding.scrollView.paddingLeft,
                binding.scrollView.paddingTop,
                binding.scrollView.paddingRight,
                bottomNav.height
            )
        }

        setupAdapters()
        observeState()
        setupPagination()

    }

    private fun setupAdapters() {

        // Горизонтальный адаптер
        upcomingAdapter = HorizontalMovieAdapter(
            movies = emptyList(),
            onClick = { movie ->
                // переход в детали
//                val action = HomeFragmentDirections.actionHomeToDetail(movie.id)
//                findNavController().navigate(action)
            },
            onWatchlistClick = { movie ->
                WatchlistPickerBottomSheet(movie)
                    .show(childFragmentManager, "picker")
            }

        )

        binding.upcomingRecycler.apply {
            layoutManager = LinearLayoutManager(
                requireContext(),
                LinearLayoutManager.HORIZONTAL,
                false
            )
            adapter = upcomingAdapter
        }

        // Вертикальный адаптер
        trendsAdapter = VerticalMovieAdapter(
            movies = emptyList(),
            onClick = { movie ->
                // переход в детали
//                val action = HomeFragmentDirections.actionHomeToDetail(movie.id)
//                findNavController().navigate(action)
            },
            onWatchlistClick = { movie ->
                WatchlistPickerBottomSheet(movie)
                    .show(childFragmentManager, "picker")
            }

        )

        binding.trendsRecycler.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
            isNestedScrollingEnabled = false
            adapter = trendsAdapter
        }

    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            movieListViewModel.movieListState.collect { state ->
                // Upcoming (горизонталь)
                upcomingAdapter.submitList(state.upcomingMovieList)

                // Popular/Trends (вертикаль)
                trendsAdapter.submitList(state.popularMovieList)
            }
        }
    }

    private fun setupPagination() {
        // создание listener
        val listener = ViewTreeObserver.OnScrollChangedListener {
            val b = _binding ?: return@OnScrollChangedListener

            val scrollView = b.scrollView
            val child = scrollView.getChildAt(0)

            val diff = child.bottom - (scrollView.height + scrollView.scrollY)

            if (diff <= 100) {    // буфер
                if (!movieListViewModel.movieListState.value.isPaginating) {
                    movieListViewModel.onEvent(MovieListUiEvent.Paginate(Category.POPULAR))
                }
            }

        }

        scrollListener = listener
        binding.scrollView.viewTreeObserver.addOnScrollChangedListener(listener)
    }

    override fun onDestroyView() {
        // удаление listener
        scrollListener?.let {
            binding.scrollView.viewTreeObserver.removeOnScrollChangedListener(it)
        }
        scrollListener = null

        super.onDestroyView()
        _binding = null
    }
}

