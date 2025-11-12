package com.example.juevesprogramacion.data.network

import com.example.juevesprogramacion.data.model.NewsResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsApiService {

    // Endpoint oficial de GNews con paginación
    @GET("top-headlines")
    fun getTopHeadlines(
        @Query("lang") lang: String = "es",
        @Query("country") country: String = "ar",
        @Query("max") max: Int = 10,
        @Query("page") page: Int = 1, // 👈 agregado para la paginación
        @Query("token") token: String
    ): Call<NewsResponse>
}
