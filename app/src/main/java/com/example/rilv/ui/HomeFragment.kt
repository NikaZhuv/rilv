package com.example.rilv.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.example.rilv.databinding.FragmentHomeBinding
import com.example.rilv.R
import com.example.rilv.adapters.HorizontalMovieAdapter
import com.example.rilv.movie.presentation.MovieListViewModel
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rilv.adapters.BannerAdapter
import com.example.rilv.movie.presentation.MovieListUiEvent
import com.example.rilv.utils.Category
import com.example.rilv.watchlists.bottomsheet.WatchlistPickerBottomSheet
import com.google.android.material.tabs.TabLayoutMediator
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import androidx.viewpager2.widget.ViewPager2
import kotlin.getValue
import kotlin.math.abs

@AndroidEntryPoint
class HomeFragment : Fragment(R.layout.fragment_home) {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val movieListViewModel: MovieListViewModel by activityViewModels()

    // ======
    private lateinit var bannerAdapter: BannerAdapter
    private val bannerHandler = Handler(Looper.getMainLooper())
    private var bannerRunnable: Runnable? = null
    private val bannerAutoScrollDelay = 3000L
    private var userIsInteracting = false
    // ======

    private lateinit var newAdapter: HorizontalMovieAdapter
    private lateinit var trendsAdapter: HorizontalMovieAdapter
    private lateinit var upcomingAdapter: HorizontalMovieAdapter

    // Listener для пагинации
    private var scrollListener: ViewTreeObserver.OnScrollChangedListener? = null


    // page callback to track user interaction
    private val pageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageScrollStateChanged(state: Int) {
            super.onPageScrollStateChanged(state)
            userIsInteracting = state != ViewPager2.SCROLL_STATE_IDLE
            if (userIsInteracting) {
                stopAutoScroll()
            } else {
                // restart after short delay
                startAutoScrollWithDelay()
            }
        }

        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            // update custom tabs
            updateTabDots(position)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return super.onCreateView(inflater, container, savedInstanceState)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentHomeBinding.bind(view)

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

        setupBanner()
        setupAdapters()
        observeState()
        setupPagination()
    }

    /// ------------

    private fun setupBanner() {
        val banners = listOf(
            R.drawable.avatar_fire_banner,
            R.drawable.zootopia_banner,
            R.drawable.five_nights_banner
        )

        bannerAdapter = BannerAdapter(banners)

        binding.viewPager2.apply {
            adapter = bannerAdapter
            offscreenPageLimit = 3

            // remove clipping so we can see peek of adjacent pages
            (getChildAt(0) as? RecyclerView)?.let { rv ->
                rv.clipToPadding = false
                rv.clipChildren = false
                rv.setPadding(24, 0, 24, 0)
                rv.overScrollMode = RecyclerView.OVER_SCROLL_NEVER
            }


            // page transformer: scale center and translate to show adjacent edges
            setPageTransformer { page, position ->
                val scale = 0.9f + (1 - abs(position)) * 0.1f
                page.scaleY = scale
                page.scaleX = scale

                page.alpha = 0.6f + (1 - abs(position)) * 0.4f

                page.translationX = -position * 40
            }


            registerOnPageChangeCallback(pageChangeCallback)
        }

        // TabLayout Mediator with custom tabs
        TabLayoutMediator(binding.tabIndicator, binding.viewPager2) { tab, pos ->
            val tabView = layoutInflater.inflate(R.layout.tab_dot, null)
            val dot = tabView.findViewById<ImageView>(R.id.dotImage)
            dot.setImageResource(R.drawable.dot_unselected)
            tab.customView = tabView
        }.attach()

        // set initial selected dot
        updateTabDots(binding.viewPager2.currentItem)

        // start autoplay
        startAutoScroll()
    }

    // helper to update dots
    private fun updateTabDots(selectedPosition: Int) {
        val tabCount = binding.tabIndicator.tabCount
        for (i in 0 until tabCount) {
            val tab = binding.tabIndicator.getTabAt(i)
            val custom = tab?.customView
            val dot = custom?.findViewById<ImageView>(R.id.dotImage)
            if (i == selectedPosition) {
                dot?.setImageResource(R.drawable.dot_selected)
            } else {
                dot?.setImageResource(R.drawable.dot_unselected)
            }
        }
    }

    // Auto-scroll
    private fun startAutoScroll() {
        stopAutoScroll()

        bannerRunnable = object : Runnable {
            override fun run() {
                val b = _binding ?: return

                if (bannerAdapter.itemCount <= 1) return
                if (userIsInteracting) {
                    bannerHandler.postDelayed(this, bannerAutoScrollDelay)
                    return
                }

                val next = (b.viewPager2.currentItem + 1) % bannerAdapter.itemCount
                b.viewPager2.setCurrentItem(next, true)

                bannerHandler.postDelayed(this, bannerAutoScrollDelay)
            }
        }
        bannerHandler.postDelayed(bannerRunnable!!, bannerAutoScrollDelay)
    }


    // start with delay
    private fun startAutoScrollWithDelay(delay: Long = bannerAutoScrollDelay) {
        bannerHandler.removeCallbacksAndMessages(null)
        bannerHandler.postDelayed({ startAutoScroll() }, delay)
    }

    private fun stopAutoScroll() {
        bannerRunnable?.let { bannerHandler.removeCallbacks(it) }
        bannerRunnable = null
    }

    //// ------------

    private fun setupAdapters() {

        // NEW MOVIES
        newAdapter = HorizontalMovieAdapter(
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
        binding.recyclerViewNewMov.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = newAdapter
        }

        // TRENDS MOVIES
        trendsAdapter = HorizontalMovieAdapter(
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
        binding.recyclerViewTopMov.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = trendsAdapter
        }

        // UPCOMING MOVIES
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
        binding.recyclerViewUpcomMov.apply {
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            adapter = upcomingAdapter
        }

    }

    private fun observeState() {
        viewLifecycleOwner.lifecycleScope.launch {
            movieListViewModel.movieListState.collect { state ->
                // Movies
                newAdapter.submitList(state.nowPlayingMovieList)
                trendsAdapter.submitList(state.popularMovieList)
                upcomingAdapter.submitList(state.upcomingMovieList)

                // ProgressBars
//                binding.progressBarBanner.visibility =
//                    if (state.popularMovieList.isEmpty() && state.isLoading) View.VISIBLE else View.GONE
                binding.progressBarNew.visibility =
                    if (state.nowPlayingMovieList.isEmpty() && state.isLoading) View.VISIBLE else View.GONE
                binding.progressBarTop.visibility =
                    if (state.popularMovieList.isEmpty() && state.isLoading) View.VISIBLE else View.GONE
                binding.progressBarUpcom.visibility =
                    if (state.upcomingMovieList.isEmpty() && state.isLoading) View.VISIBLE else View.GONE
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
        stopAutoScroll()

        bannerHandler.removeCallbacksAndMessages(null)

        // удаление listener
        scrollListener?.let {
            binding.scrollView.viewTreeObserver.removeOnScrollChangedListener(it)
        }
        scrollListener = null

        binding.viewPager2.unregisterOnPageChangeCallback(pageChangeCallback)

        super.onDestroyView()
        _binding = null
    }

    override fun onPause() {
        super.onPause()
        bannerRunnable?.let { bannerHandler.removeCallbacks(it) }
        stopAutoScroll()
    }

}