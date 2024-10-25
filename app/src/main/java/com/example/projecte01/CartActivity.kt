// CartActivity.kt
package com.example.proyecte01

import Product
import ProductAdapter
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var totalPriceTextView: TextView
    private lateinit var pickupTimeEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Inicializa las vistas
        recyclerView = findViewById(R.id.recyclerViewCart)
        totalPriceTextView = findViewById(R.id.totalPrice)
        pickupTimeEditText = findViewById(R.id.pickupTime)

        // Obtiene los productos del carrito desde el Intent
        val cartProducts: List<Product> = intent.getParcelableArrayListExtra("cart_products") ?: emptyList()

        // Configura el adaptador con un lambda vacío para 'onAddToCartClicked'
        productAdapter = ProductAdapter(cartProducts, {}, false, {})
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = productAdapter

        // Calcula y muestra el precio total
        val totalPrice = cartProducts.sumOf { it.price }
        totalPriceTextView.text = String.format("Precio total: %.2f €", totalPrice)

        // Configura el botón de cancelar
        val cancelButton: Button = findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            // Cerrar la actividad y volver a la anterior
            finish()
        }

        // Configura el botón de realizar el pago
        val checkoutButton: Button = findViewById(R.id.checkoutButton)
        checkoutButton.setOnClickListener {
            val pickupTime = pickupTimeEditText.text.toString().trim()
            if (pickupTime.isEmpty()) {
                // Mostrar mensaje de error si no se indica la hora de recogida
                pickupTimeEditText.error = "Por favor, indica la hora de recogida"
            } else {
                // Aquí puedes implementar la lógica para el pago
                // Por ejemplo, iniciar una nueva actividad para completar la compra
                // val intent = Intent(this, PaymentActivity::class.java)
                // intent.putExtra("pickup_time", pickupTime)
                // startActivity(intent)
                // finish()

                // Mensaje de éxito por ahora
                Toast.makeText(this, "Hora de recogida: $pickupTime", Toast.LENGTH_SHORT).show()
            }
        }
    }
}