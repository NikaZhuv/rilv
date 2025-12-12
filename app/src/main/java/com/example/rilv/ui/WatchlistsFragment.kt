package com.example.rilv.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.rilv.R
import com.example.rilv.databinding.FragmentWatchlistsBinding
import dagger.hilt.android.AndroidEntryPoint
import com.example.rilv.watchlists.wadapters.WatchlistsAdapter
import com.example.rilv.watchlists.WatchlistsViewModel
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rilv.moviewatch.WatchlistMovieRepository
import javax.inject.Inject


@AndroidEntryPoint
class WatchlistsFragment : Fragment(R.layout.fragment_watchlists){
    private var _binding: FragmentWatchlistsBinding? = null
    private val binding get() = _binding!!

    private val vm: WatchlistsViewModel by viewModels()
    private lateinit var adapter: WatchlistsAdapter

    @Inject lateinit var movieRepo: WatchlistMovieRepository


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        _binding = FragmentWatchlistsBinding.bind(view)


        adapter = WatchlistsAdapter(
            lists = emptyList(),
            scope = viewLifecycleOwner.lifecycleScope,
            movieRepo = movieRepo
        ) { wl ->
            val action = WatchlistsFragmentDirections
                .actionWatchlistsFragmentToWatchlistDetailFragment(wl.id)

            findNavController().navigate(action)
        }


        binding.watchlistsRecycler.adapter = adapter
        binding.watchlistsRecycler.layoutManager =
            LinearLayoutManager(requireContext())

        binding.addWatchlistBtn.setOnClickListener {
            showCreateDialog()
        }

        vm.watchlists.observe(viewLifecycleOwner) { list ->
            adapter.submit(list)
        }


        // відступ над навігаційною панеллю
        val bottomNav = requireActivity().findViewById<View>(R.id.bottomNavView)
        bottomNav.post {
            binding.watchlistsRoot.setPadding(
                binding.watchlistsRoot.paddingLeft,
                binding.watchlistsRoot.paddingTop,
                binding.watchlistsRoot.paddingRight,
                bottomNav.height
            )
        }

    }
    private fun showCreateDialog() {
        vm.create("New List", null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

