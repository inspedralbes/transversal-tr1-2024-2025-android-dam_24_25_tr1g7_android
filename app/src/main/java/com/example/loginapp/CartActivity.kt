
package com.example.loginapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecte01.R

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var totalPriceTextView: TextView
    private lateinit var pickupTimeEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)


        recyclerView = findViewById(R.id.recyclerViewCart)
        totalPriceTextView = findViewById(R.id.totalPrice)
        pickupTimeEditText = findViewById(R.id.pickupTime)


        val cartProducts: List<Product> = intent.getParcelableArrayListExtra("cart_products") ?: emptyList()


        productAdapter = ProductAdapter(cartProducts, {}, false, {})
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = productAdapter


        val totalPrice = cartProducts.sumOf { it.price }
        totalPriceTextView.text = String.format("Precio total: %.2f €", totalPrice)


        val cancelButton: Button = findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {

            finish()
        }


        val checkoutButton: Button = findViewById(R.id.checkoutButton)
        checkoutButton.setOnClickListener {
            val pickupTime = pickupTimeEditText.text.toString().trim()
            if (pickupTime.isEmpty()) {

                pickupTimeEditText.error = "Por favor, indica la hora de recogida"
            } else {

                Toast.makeText(this, "Hora de recogida: $pickupTime", Toast.LENGTH_SHORT).show()
            }
        }
    }
}