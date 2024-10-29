package com.example.loginapp

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
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
    private lateinit var backButton: Button
    private lateinit var saveChangesButton: Button
    private lateinit var changePasswordButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        usernameEditText = findViewById(R.id.usernameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        backButton = findViewById(R.id.backButton)
        saveChangesButton = findViewById(R.id.saveChangesButton)
        changePasswordButton = findViewById(R.id.changePasswordButton)

        loadUserProfile()

        backButton.setOnClickListener {
            finish()
        }

        saveChangesButton.setOnClickListener {
            saveUserProfile()
        }

        changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }
    }

    private fun loadUserProfile() {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)

        usernameEditText.setText(sharedPref.getString("username", "N/A"))
        emailEditText.setText(sharedPref.getString("email", "N/A"))
        firstNameEditText.setText(sharedPref.getString("first_name", "N/A"))
        lastNameEditText.setText(sharedPref.getString("last_name", "N/A"))
    }

    private fun saveUserProfile() {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1).toString()

        val params = mapOf(
            "user_id" to userId,
            "username" to usernameEditText.text.toString(),
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
                        apply()
                    }
                    Toast.makeText(this@PerfilActivity, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@PerfilActivity, "Error al actualizar perfil en el servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@PerfilActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.change_password, null)
        val currentPasswordEditText = dialogView.findViewById<EditText>(R.id.currentPasswordEditText)
        val newPasswordEditText = dialogView.findViewById<EditText>(R.id.newPasswordEditText)

        AlertDialog.Builder(this)
            .setTitle("Cambiar Contraseña")
            .setView(dialogView)
            .setPositiveButton("Confirmar") { _, _ ->
                val currentPassword = currentPasswordEditText.text.toString()
                val newPassword = newPasswordEditText.text.toString()
                if (currentPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                    updateUserWithPassword(currentPassword, newPassword)
                } else {
                    Toast.makeText(this, "Por favor, ingresa ambas contraseñas.", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun updateUserWithPassword(currentPassword: String, newPassword: String) {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        val userId = sharedPref.getInt("user_id", -1).toString()

        val params = mapOf(
            "user_id" to userId,
            "username" to usernameEditText.text.toString(),
            "first_name" to firstNameEditText.text.toString(),
            "last_name" to lastNameEditText.text.toString(),
            "email" to emailEditText.text.toString(),
            "current_password" to currentPassword,
            "new_password" to newPassword
        )

        RetrofitClient.instance.updateUser(params).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@PerfilActivity, "Perfil y contraseña actualizados correctamente", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@PerfilActivity, "Error al actualizar perfil o contraseña", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@PerfilActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
