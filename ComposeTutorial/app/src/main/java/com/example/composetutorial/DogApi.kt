package com.example.composetutorial

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET

data class DogResponse(
    val url: String
)

private val apiKeyInterceptor = Interceptor { chain ->
    val request = chain.request().newBuilder()
        .addHeader("x-api-key", BuildConfig.DOG_API_KEY)
        .build()
    chain.proceed(request)
}

private val okHttpClient = OkHttpClient.Builder()
    .addInterceptor(apiKeyInterceptor)
    .build()

interface DogApi {
    @GET("images/search")
    suspend fun getRandomDog(): List<DogResponse>
}
val retrofit = Retrofit.Builder()
    .baseUrl("https://api.thecatapi.com/v1/")
    .addConverterFactory(MoshiConverterFactory.create())
    .client(okHttpClient)
    .build()

val dogApi = retrofit.create(DogApi::class.java)