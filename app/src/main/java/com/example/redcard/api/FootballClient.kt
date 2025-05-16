package com.example.redcard.api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object FootballClient {

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(ApiKeyInterceptor()) // Add our custom ApiKeyInterceptor
        .connectTimeout(10, TimeUnit.SECONDS) // زمان انتظار برای برقراری اتصال
        .readTimeout(10, TimeUnit.SECONDS)    // زمان انتظار برای خواندن داده پس از اتصال
        .writeTimeout(10, TimeUnit.SECONDS)
        .addInterceptor(HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY // Keep for debugging
        })
        .build()

    val api: FootballApi by lazy {
        Retrofit.Builder()
            .baseUrl(Credentials.BASE_URL) // Use BASE_URL from Credentials
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
            .create(FootballApi::class.java)
    }
}