package com.example.loginapp

data class User(
    val user_id: Int,
    val username: String,
    val password: String,
    val first_name: String,
    val last_name: String,
    val email: String,
    val created_at: String
)
