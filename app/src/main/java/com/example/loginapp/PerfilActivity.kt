package com.example.loginapp

import android.content.Context
import android.os.Bundle
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecte01.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PerfilActivity : AppCompatActivity() {

    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var changePasswordButton: Button
    private lateinit var saveChangesButton: Button
    private var userId: Int = -1
    private var username: String = ""
    private var currentPassword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_perfil)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = null

        firstNameEditText = findViewById(R.id.firstNameEditText)
        lastNameEditText = findViewById(R.id.lastNameEditText)
        emailEditText = findViewById(R.id.emailEditText)
        changePasswordButton = findViewById(R.id.changePasswordButton)
        saveChangesButton = findViewById(R.id.saveChangesButton)

        val backButton: Button = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            finish()
        }

        loadUserProfile()

        changePasswordButton.setOnClickListener {
            showChangePasswordDialog()
        }

        saveChangesButton.setOnClickListener {
            saveUserProfile()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()  // Cierra esta actividad y vuelve a la anterior
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun loadUserProfile() {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        userId = sharedPref.getInt("user_id", -1)
        username = sharedPref.getString("username", "") ?: ""
        currentPassword = sharedPref.getString("password", "") ?: ""

        firstNameEditText.setText(sharedPref.getString("first_name", ""))
        lastNameEditText.setText(sharedPref.getString("last_name", ""))
        emailEditText.setText(sharedPref.getString("email", ""))
    }

    private fun showChangePasswordDialog() {
        val dialogView = layoutInflater.inflate(R.layout.change_password, null)
        val newPasswordInput = dialogView.findViewById<EditText>(R.id.newPasswordEditText)

        AlertDialog.Builder(this)
            .setTitle("Cambiar contraseña")
            .setView(dialogView)
            .setPositiveButton("Guardar") { _, _ ->
                val newPassword = newPasswordInput.text.toString()
                if (newPassword.isNotBlank()) {
                    updatePassword(newPassword)
                } else {
                    Toast.makeText(this, "La contraseña no puede estar vacía", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    private fun updatePassword(newPassword: String) {
        val params = mapOf(
            "user_id" to userId.toString(),
            "username" to username,
            "password" to newPassword,
            "first_name" to firstNameEditText.text.toString(),
            "last_name" to lastNameEditText.text.toString(),
            "email" to emailEditText.text.toString()
        )

        RetrofitClient.instance.updateUser(params).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    currentPassword = newPassword
                    Toast.makeText(this@PerfilActivity, "Contraseña actualizada con éxito", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@PerfilActivity, "Error al actualizar la contraseña", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@PerfilActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUserProfile() {
        val params = mapOf(
            "user_id" to userId.toString(),
            "username" to username,
            "password" to currentPassword,
            "first_name" to firstNameEditText.text.toString(),
            "last_name" to lastNameEditText.text.toString(),
            "email" to emailEditText.text.toString()
        )

        RetrofitClient.instance.updateUser(params).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
                    with(sharedPref.edit()) {
                        putString("first_name", firstNameEditText.text.toString())
                        putString("last_name", lastNameEditText.text.toString())
                        putString("email", emailEditText.text.toString())
                        apply()
                    }
                    Toast.makeText(this@PerfilActivity, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this@PerfilActivity, "Error al actualizar perfil", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(this@PerfilActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
