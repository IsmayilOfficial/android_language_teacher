package com.rusdili.pro.core.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.OkHttpClient

data class GdeltResponse(val articles: List<GdeltArticle> = emptyList())

data class GdeltArticle(
    val title: String?,
    val url: String?,
    val seendate: String?,
    val domain: String?,
    val socialimage: String?
)

interface NewsApiService {
    @GET("api/v2/doc/doc")
    suspend fun searchArticles(
        @Query("query") query: String,
        @Query("mode") mode: String = "ArtList",
        @Query("format") format: String = "json",
        @Query("maxrecords") maxRecords: Int = 20
    ): GdeltResponse
}

object NewsNetwork {
    val api: NewsApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.gdeltproject.org/")
            .client(OkHttpClient.Builder().build())
            .addConverterFactory(
                MoshiConverterFactory.create(
                    Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
                )
            )
            .build()
            .create(NewsApiService::class.java)
    }
}
