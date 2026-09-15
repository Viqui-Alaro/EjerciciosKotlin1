package com.example.plantillabcp.ui.state

import com.example.plantillabcp.model.PostDto

sealed interface ApiUiState {
    object Loading : ApiUiState
    data class Success(val posts: List<PostDto>) : ApiUiState
    data class Error(val message: String) : ApiUiState
}