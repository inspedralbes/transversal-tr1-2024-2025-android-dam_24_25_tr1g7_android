package com.example.loginapp

import android.content.Context
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecte01.R

class ProfileActivity : AppCompatActivity() {

    private lateinit var usernameTextView: TextView
    private lateinit var emailTextView: TextView
    private lateinit var firstNameTextView: TextView
    private lateinit var lastNameTextView: TextView
    private lateinit var createdAtTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_profile)

        usernameTextView = findViewById(R.id.usernameTextView)
        emailTextView = findViewById(R.id.emailTextView)
        firstNameTextView = findViewById(R.id.firstNameTextView)
        lastNameTextView = findViewById(R.id.lastNameTextView)
        createdAtTextView = findViewById(R.id.createdAtTextView)

        loadUserProfile()
    }

    private fun loadUserProfile() {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        usernameTextView.text = sharedPref.getString("username", "N/A")
        emailTextView.text = sharedPref.getString("email", "N/A")
        firstNameTextView.text = sharedPref.getString("first_name", "N/A")
        lastNameTextView.text = sharedPref.getString("last_name", "N/A")
        createdAtTextView.text = sharedPref.getString("created_at", "N/A")
    }
}
