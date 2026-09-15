package com.example.plantillabcp.remote

import com.example.plantillabcp.model.PostDto
import retrofit2.http.GET

interface ApiService {
    @GET("posts")
    suspend fun getPosts(): List<PostDto>
}