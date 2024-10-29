package com.example.loginapp

import Product
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecte01.R

class CarritoActivity : AppCompatActivity() {
    private lateinit var cartAdapter: CarritoAdapter
    private var cartProducts: MutableList<Product> = mutableListOf()
    private lateinit var totalPriceTextView: TextView
    private lateinit var pickupTimeEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        val recyclerView: RecyclerView = findViewById(R.id.cartRecyclerView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        pickupTimeEditText = findViewById(R.id.pickupTimeEditText)
        val checkoutButton: Button = findViewById(R.id.checkoutButton)
        val cancelButton: Button = findViewById(R.id.cancelButton)

        cartProducts = intent.getParcelableArrayListExtra<Product>("cart_products")?.toMutableList() ?: mutableListOf()

        cartAdapter = CarritoAdapter(
            cartProducts,
            onIncreaseQuantity = { product -> updateProductQuantity(product, increase = true) },
            onDecreaseQuantity = { product -> updateProductQuantity(product, increase = false) },
            onRemoveProduct = { product -> removeProduct(product) }
        )

        recyclerView.apply {
            adapter = cartAdapter
            layoutManager = LinearLayoutManager(this@CarritoActivity)
        }

        updateTotalPrice()

        checkoutButton.setOnClickListener { processCheckout() }
        cancelButton.setOnClickListener { finish() }
    }

    private fun updateProductQuantity(product: Product, increase: Boolean) {
        when {
            increase && product.quantityInCart < product.stock -> {
                product.quantityInCart++
                updateCartAndUI()
            }
            !increase && product.quantityInCart > 1 -> {
                product.quantityInCart--
                updateCartAndUI()
            }
            !increase && product.quantityInCart == 1 -> {
                removeProduct(product)
            }
            else -> {
                Toast.makeText(this, "No hay más stock disponible", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun removeProduct(product: Product) {
        cartProducts.remove(product)
        updateCartAndUI()
        Toast.makeText(this, "${product.product_name} eliminado del carrito", Toast.LENGTH_SHORT).show()
    }

    private fun updateCartAndUI() {
        cartAdapter.notifyDataSetChanged()
        updateTotalPrice()
    }

    private fun updateTotalPrice() {
        val total = cartProducts.sumOf { it.price * it.quantityInCart }
        totalPriceTextView.text = String.format("Total: %.2f €", total)
    }

    private fun processCheckout() {
        val pickupTime = pickupTimeEditText.text.toString()
        if (pickupTime.isBlank()) {
            Toast.makeText(this, "Por favor, indica la hora de recogida", Toast.LENGTH_SHORT).show()
            return
        }

        // Aquí iría la lógica para procesar el pedido
        Toast.makeText(this, "Procesando pedido para recoger a las $pickupTime", Toast.LENGTH_SHORT).show()
        // Implementar la lógica real de checkout aquí
    }
}