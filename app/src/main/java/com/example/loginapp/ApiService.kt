package com.example.loginapp

import Order
import Product
import ProductCreateRequest
import ProductUpdateRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface ApiService {
    @GET("/getUsuaris")
    fun getUsers(): Call<List<User>>

    @POST("/createUsuari")
    fun createUser(@QueryMap params: Map<String, String>): Call<Void>

    @GET("/getProductes")
    fun getProducts(): Call<List<Product>>

    @PUT("/updateUsuari")
    fun updateUsuari(@QueryMap params: Map<String, String>): Call<Void>

    @POST("/orders")
    suspend fun createOrder(@Body order: Order): Response<Order>

    @GET("/getComandes")
    suspend fun getOrders(): Response<List<Order>>

    @GET("/getComandes")
    suspend fun getOrder(@Query("order_id") orderId: Int): Response<Order>


    @POST("/createComanda")
    suspend fun createOrder(
        @Query("user_id") userId: Int,
        @Query("product_id") productId: Int,
        @Query("total") total: Double
    ): Response<ResponseBody>



    @DELETE("/deleteComanda")
    suspend fun deleteOrder(@Query("order_id") orderId: Int): Response<Void>

    @PUT("/waiting")
    suspend fun setWaitingStatus(@Query("order_id") orderId: Int): Response<Void>

    @PUT("/pending")
    suspend fun setPendingStatus(@Query("order_id") orderId: Int): Response<Void>

    @PUT("/shipped")
    suspend fun setShippedStatus(@Query("order_id") orderId: Int): Response<Void>

    @PUT("/verified")
    suspend fun setVerifiedStatus(@Query("order_id") orderId: Int): Response<Void>

    @PUT("/confirmed")
    suspend fun setConfirmedStatus(@Query("order_id") orderId: Int): Response<Void>

    @PUT("/canceled")
    suspend fun setCanceledStatus(@Query("order_id") orderId: Int): Response<Void>



    @POST("/createProducte")
    fun createProduct(@Body product: ProductCreateRequest): Call<String>


        @DELETE("deleteProducte")
        fun deleteProduct(@Query("product_id") productId: Int): Call<Void>


    @PUT("/updateProducte")
    fun updateProduct(@Body product: ProductUpdateRequest): Call<Product>
}
