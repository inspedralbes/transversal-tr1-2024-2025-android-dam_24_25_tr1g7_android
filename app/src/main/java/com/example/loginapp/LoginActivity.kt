package com.example.loginapp

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.proyecte01.R
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {

    private lateinit var usernameInput: EditText
    private lateinit var passwordInput: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val registerText = findViewById<TextView>(R.id.registerText)
        usernameInput = findViewById(R.id.username)
        passwordInput = findViewById(R.id.password)
        val loginButton = findViewById<Button>(R.id.loginButton)

        registerText.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString()
            val password = passwordInput.text.toString()

            if (username.isNotBlank() && password.isNotBlank()) {
                login(username, password)
            } else {
                Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun login(username: String, password: String) {
        RetrofitClient.instance.getUsers().enqueue(object : Callback<List<User>> {
            override fun onResponse(call: Call<List<User>>, response: Response<List<User>>) {
                if (response.isSuccessful && response.body() != null) {
                    val users = response.body()!!
                    val user = users.find { it.username == username && it.password == password }

                    if (user != null) {
                        saveUserSession(user)

                        Toast.makeText(this@LoginActivity, "Inicio de sesión exitoso!", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@LoginActivity, "Usuario o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Error en la respuesta del servidor", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<User>>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUserSession(user: User) {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putInt("user_id", user.user_id)
            putString("username", user.username)
            putString("first_name", user.first_name)
            putString("last_name", user.last_name)
            putString("email", user.email)
            putString("created_at", user.created_at)
            apply()
        }
    }
}
