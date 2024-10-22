package com.example.proyecte01

import Product
import ProductAdapter
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
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

        recyclerView = findViewById(R.id.recyclerViewCart)
        totalPriceTextView = findViewById(R.id.totalPrice)
        pickupTimeEditText = findViewById(R.id.pickupTime)

        val cartProducts: List<Product> = intent.getParcelableArrayListExtra("cart_products") ?: emptyList()
        productAdapter = ProductAdapter(cartProducts, {}, false)

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = productAdapter

        val totalPrice = cartProducts.sumOf { it.price }
        totalPriceTextView.text = "Precio total: ${totalPrice} €"

        val cancelButton: Button = findViewById(R.id.cancelButton)
        cancelButton.setOnClickListener {
            // Cancelar y volver a la actividad anterior
            finish()
        }

        val checkoutButton: Button = findViewById(R.id.checkoutButton)
        checkoutButton.setOnClickListener {
            val pickupTime = pickupTimeEditText.text.toString()
            // Lógica para continuar con el pago, pasar a la siguiente actividad o mostrar un mensaje
            if (pickupTime.isEmpty()) {
                // Mostrar mensaje de error o advertencia
                pickupTimeEditText.error = "Por favor, indica la hora de recogida"
            } else {
                // Aquí puedes implementar la lógica para el pago
                // Por ejemplo, iniciar una nueva actividad para completar la compra
                // val intent = Intent(this, PaymentActivity::class.java)
                // intent.putExtra("pickup_time", pickupTime)
                // startActivity(intent)
                // finish()
            }
        }
    }
}
