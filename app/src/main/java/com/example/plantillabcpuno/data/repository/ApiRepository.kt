package com.example.plantillabcp.data.repository

import com.example.plantillabcp.model.PostDto
import com.example.plantillabcp.remote.ApiService
import com.example.plantillabcp.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ApiRepository(private val apiService: ApiService = RetrofitClient.apiService) {

    suspend fun fetchPosts(): Result<List<PostDto>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getPosts()
                Result.success(response)
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}