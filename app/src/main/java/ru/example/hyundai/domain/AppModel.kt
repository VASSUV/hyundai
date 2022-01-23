package ru.example.hyundai.domain

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET


object AppModel {
    val number = "9176054744"
    val hyundaiGsonApi = Retrofit.Builder()
        .baseUrl("https://showroom.hyundai.ru/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
        .create(HyundaiApiGsonService::class.java)

    val hyundaiApi = Retrofit.Builder()
        .baseUrl("https://showroom.hyundai.ru/")
        .build()
        .create(HyundaiApiService::class.java)

}

interface HyundaiApiGsonService {
    @GET("users")
    suspend fun getUsers(): List<String>
}

interface HyundaiApiService {

    @GET("users")
    suspend fun getUsers(): List<String>
}