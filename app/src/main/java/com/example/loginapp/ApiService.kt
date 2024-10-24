package com.example.loginapp

import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("/getUsuaris")
    fun getUsers(): Call<List<User>>
}
