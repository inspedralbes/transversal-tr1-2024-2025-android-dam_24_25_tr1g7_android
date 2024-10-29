package com.example.loginapp

import Product
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.QueryMap

interface ApiService {
    @GET("/getUsuaris")
    fun getUsers(): Call<List<User>>

    @POST("/createUsuari")
    fun createUser(@QueryMap params: Map<String, String>): Call<Void>

    @GET("/getProductes")
    fun getProducts(): Call<List<Product>>

    @PUT("/updateUsuari")
    fun updateUser(@QueryMap params: Map<String, String>): Call<Void>
}
