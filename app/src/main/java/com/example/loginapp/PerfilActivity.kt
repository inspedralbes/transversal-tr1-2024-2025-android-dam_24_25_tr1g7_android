package com.example.loginapp

import android.content.Context
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecte01.R

class PerfilActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var backButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        backButton = findViewById(R.id.backButton)

        loadUserProfile()

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun loadUserProfile() {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        usernameEditText.setText(sharedPref.getString("username", "N/A"))
        emailEditText.setText(sharedPref.getString("email", "N/A"))
        firstNameEditText.setText(sharedPref.getString("first_name", "N/A"))
        lastNameEditText.setText(sharedPref.getString("last_name", "N/A"))
        passwordEditText.setText(sharedPref.getString("password", "******"))

        usernameEditText.isEnabled = false
        emailEditText.isEnabled = false
        firstNameEditText.isEnabled = false
        lastNameEditText.isEnabled = false
        passwordEditText.isEnabled = false
    }
}
