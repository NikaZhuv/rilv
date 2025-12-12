package com.example.rilv.watchlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.rilv.watchlists.data.WatchlistRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class WatchlistsViewModel @Inject constructor(
    private val repo: WatchlistRepository
) : ViewModel() {

    val watchlists = repo.getAllWatchlists().asLiveData()

    init {
        viewModelScope.launch {
            repo.ensureDefaultExists()
        }
    }

    fun create(name: String, description: String?) {
        viewModelScope.launch {
            repo.insert(
                WatchlistEntity(
                    name = name,
                    description = description
                )
            )
        }
    }
}

