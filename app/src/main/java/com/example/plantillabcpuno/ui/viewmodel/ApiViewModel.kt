package com.example.plantillabcp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.plantillabcp.data.repository.ApiRepository
import com.example.plantillabcp.ui.state.ApiUiState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


class ApiViewModel(private val repository: ApiRepository = ApiRepository()) : ViewModel() {

    private val _uiState = MutableStateFlow<ApiUiState>(ApiUiState.Loading)
    val uiState: StateFlow<ApiUiState> = _uiState.asStateFlow()

    init {
        loadPosts()
    }

    fun loadPosts() {
        viewModelScope.launch {
            _uiState.value = ApiUiState.Loading
            repository.fetchPosts()
                .onSuccess { posts ->
                    _uiState.value = ApiUiState.Success(posts)
                }
                .onFailure { error ->
                    _uiState.value = ApiUiState.Error(error.localizedMessage ?: "Error de red")
                }
        }
    }
}