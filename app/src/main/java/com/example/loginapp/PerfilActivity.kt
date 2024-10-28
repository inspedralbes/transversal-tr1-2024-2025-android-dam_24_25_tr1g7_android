package com.example.loginapp

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.MotionEvent
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecte01.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PerfilActivity : AppCompatActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var createdAtEditText: EditText
    private lateinit var backButton: Button
    private lateinit var saveChangesButton: Button
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        createdAtEditText = findViewById(R.id.createdAtEditText)
        backButton = findViewById(R.id.backButton)
        saveChangesButton = findViewById(R.id.saveChangesButton)

        loadUserProfile()

        backButton.setOnClickListener {
            finish()
        }

        saveChangesButton.setOnClickListener {
            saveUserProfile()
        }

        passwordEditText.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_UP) {
                if (event.rawX >= (passwordEditText.right - passwordEditText.compoundDrawables[2].bounds.width())) {
                    togglePasswordVisibility()
                    true
                } else false
            } else false
        }
    }

    private fun togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible
        passwordEditText.inputType = if (isPasswordVisible) {
            InputType.TYPE_CLASS_TEXT
        } else {
            InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
        }
        passwordEditText.setSelection(passwordEditText.text.length)
    }

    private fun loadUserProfile() {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        // Cargar los valores guardados de SharedPreferences
        usernameEditText.setText(sharedPref.getString("username", "N/A"))
        emailEditText.setText(sharedPref.getString("email", "N/A"))
        firstNameEditText.setText(sharedPref.getString("first_name", "N/A"))
        lastNameEditText.setText(sharedPref.getString("last_name", "N/A"))
        passwordEditText.setText(sharedPref.getString("password", ""))
        createdAtEditText.setText(sharedPref.getString("created_at", "N/A"))
    }

    private fun saveUserProfile() {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        val userId = try {
            sharedPref.getInt("user_id", -1).toString()
        } catch (e: ClassCastException) {
            sharedPref.getString("user_id", null)
        }

        if (userId == null || userId == "-1") {
            Toast.makeText(this, "No se encontró el ID del usuario", Toast.LENGTH_SHORT).show()
            return
        }

        val params = mapOf(
            "user_id" to userId,
            "username" to usernameEditText.text.toString(),
            "password" to passwordEditText.text.toString(),
            "first_name" to firstNameEditText.text.toString(),
            "last_name" to lastNameEditText.text.toString(),
            "email" to emailEditText.text.toString()
        )

        RetrofitClient.instance.updateUser(params).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    with(sharedPref.edit()) {
                        putString("username", usernameEditText.text.toString())
                        putString("email", emailEditText.text.toString())
                        putString("first_name", firstNameEditText.text.toString())
                        putString("last_name", lastNameEditText.text.toString())
                        putString("password", passwordEditText.text.toString())
                        apply()
                    }
                    Toast.makeText(this@PerfilActivity, "Perfil actualizado", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(
                        this@PerfilActivity,
                        "Error al actualizar perfil en el servidor",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@PerfilActivity, "Error: ${t.message}", Toast.LENGTH_SHORT)
                    .show()
            }
        })
    }
}