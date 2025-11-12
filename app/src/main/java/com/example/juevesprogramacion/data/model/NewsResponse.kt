package com.example.juevesprogramacion.data.model

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @SerializedName("totalArticles")
    val totalArticles: Int?,

    @SerializedName("articles")
    val articles: List<Article>?
)

data class Article(
    @SerializedName("title")
    val title: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("image")
    val image: String?,

    @SerializedName("url")
    val url: String?,

    @SerializedName("source")
    val source: Source?,

    @SerializedName("publishedAt")
    val publishedAt: String?,

    @SerializedName("topic")
    val topic: String? // 🔹 Nuevo campo opcional (categoría)
)

data class Source(
    @SerializedName("name")
    val name: String?
)
