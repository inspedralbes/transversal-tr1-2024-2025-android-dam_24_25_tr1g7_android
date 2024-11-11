package com.example.projecte01

import Order
import OrderRequest
import Product
import android.app.Activity
import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.InputFilter
import android.text.TextWatcher
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecte01.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.util.regex.Pattern


class CarritoActivity : AppCompatActivity() {
    private lateinit var cartAdapter: CarritoAdapter
    private var cartProducts: MutableList<Product> = mutableListOf()
    private lateinit var totalPriceTextView: TextView
    private lateinit var pickupTimeEditText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_carrito)

        val toolbarBackButton = findViewById<ImageButton>(R.id.botonAtras)
        toolbarBackButton.setOnClickListener { finish() }

        val recyclerView: RecyclerView = findViewById(R.id.cartRecyclerView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        pickupTimeEditText = findViewById(R.id.pickupTimeEditText)
        setupTimeEditText()
        val checkoutButton: Button = findViewById(R.id.checkoutButton)
        val cancelButton: Button = findViewById(R.id.cancelButton)

        loadCartFromStorage()

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
        saveCartToStorage()
        Toast.makeText(this, "${product.product_name} eliminado del carrito", Toast.LENGTH_SHORT).show()
    }

    private fun updateCartAndUI() {
        cartAdapter.notifyDataSetChanged()
        updateTotalPrice()
        saveCartToStorage()
    }

    private fun updateTotalPrice() {
        val total = cartProducts.sumOf { it.price * it.quantityInCart }
        totalPriceTextView.text = String.format("Total: %.2f €", total)
    }

    private fun processCheckout() {
        val pickupTime = pickupTimeEditText.text.toString()
        if (!isValidTime(pickupTime)) {
            Toast.makeText(this, "Por favor, ingresa una hora válida en formato HH:MM", Toast.LENGTH_SHORT).show()
            return
        }

        val userId = getUserId()
        if (userId == -1) {
            Toast.makeText(this, "Error: Usuario no identificado", Toast.LENGTH_SHORT).show()
            return
        }

        CoroutineScope(Dispatchers.Main).launch {
            try {
                val result = createOrders(userId)
                if (result) {
                    Toast.makeText(this@CarritoActivity, "Pedidos creados con éxito.", Toast.LENGTH_SHORT).show()
                    clearCart()
                    setResult(Activity.RESULT_OK)
                    finish()
                } else {
                    Toast.makeText(this@CarritoActivity, "Pedidos creados con éxito.", Toast.LENGTH_SHORT).show()
                    clearCart()
                    setResult(Activity.RESULT_OK)
                    finish()
                }
            } catch (e: Exception) {
                Log.e("CarritoActivity", "Error al crear los pedidos", e)
                Toast.makeText(this@CarritoActivity, "Error al crear los pedidos: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private suspend fun createOrders(userId: Int): Boolean = withContext(Dispatchers.IO) {
        var allOrdersCreated = true
        cartProducts.forEach { product ->
            repeat(product.quantityInCart) {
                try {
                    val total = product.price
                    val orderRequest = OrderRequest(
                        user_id = userId,
                        product_id = product.product_id,
                        total = total
                    )

                    Log.d("CarritoActivity", "Creando orden: $orderRequest")

                    val response = RetrofitClient.instance.createOrder(orderRequest)
                    Log.d("CarritoActivity", "Respuesta recibida: ${response.raw()}")

                    if (response.isSuccessful) {
                        val responseBody = response.body()?.string()
                        Log.d("CarritoActivity", "Cuerpo de la respuesta: $responseBody")

                        // Parseamos la respuesta JSON
                        val jsonResponse = JSONObject(responseBody)
                        if (jsonResponse.has("message") && jsonResponse.getString("message").contains("afegida")) {
                            val comandaJson = jsonResponse.getJSONObject("comanda")
                            val createdOrder = Order(
                                order_id = comandaJson.getInt("order_id"),
                                user_id = comandaJson.getInt("user_id"),
                                product_id = comandaJson.getInt("product_id"),
                                order_date = "", // El servidor debería proporcionar esto, pero lo omitimos aquí
                                status = comandaJson.getString("status"),
                                total = comandaJson.getDouble("total")
                            )
                            Log.d("CarritoActivity", "Orden creada exitosamente: $createdOrder")
                        } else {
                            Log.e("CarritoActivity", "Respuesta inesperada del servidor: $responseBody")
                            allOrdersCreated = false
                        }
                    } else {
                        Log.e("CarritoActivity", "Error al crear el pedido: ${response.errorBody()?.string()}")
                        allOrdersCreated = false
                    }
                } catch (e: Exception) {
                    Log.e("CarritoActivity", "Error de red al crear el pedido", e)
                    allOrdersCreated = false
                }
            }
        }
        allOrdersCreated
    }
    private fun getUserId(): Int {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        return sharedPref.getInt("user_id", -1)
    }





    private fun clearCart() {
        cartProducts.clear()
        updateCartAndUI()
        saveEmptyCartToStorage()
    }

    private fun loadCartFromStorage() {
        val sharedPref = getSharedPreferences("CartPreferences", Context.MODE_PRIVATE)
        val cartJson = sharedPref.getString("cart", "")

        if (!cartJson.isNullOrEmpty()) {
            val type = object : TypeToken<List<Product>>() {}.type
            cartProducts.clear()
            cartProducts.addAll(Gson().fromJson(cartJson, type))
            Log.d("CarritoActivity", "Productos cargados desde almacenamiento: ${cartProducts.size}")
        } else {
            Log.d("CarritoActivity", "No se encontraron productos en almacenamiento")
            cartProducts.clear()
        }
    }

    private fun saveCartToStorage() {
        val sharedPref = getSharedPreferences("CartPreferences", Context.MODE_PRIVATE)

        val cartJson = Gson().toJson(cartProducts)

        with(sharedPref.edit()) {
            putString("cart", cartJson)
            apply()
        }

        Log.d("CarritoActivity", "Carrito guardado en almacenamiento: $cartJson")
    }

    private fun saveEmptyCartToStorage() {
        val sharedPref = getSharedPreferences("CartPreferences", Context.MODE_PRIVATE)
        with(sharedPref.edit()) {
            putString("cart", "")
            apply()
        }
        Log.d("CarritoActivity", "Carrito vacío guardado en almacenamiento")
    }

    private fun isValidTime(time: String): Boolean {
        val pattern = Pattern.compile("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")
        return pattern.matcher(time).matches()
    }

    private fun setupTimeEditText() {
        val timeFilter = InputFilter { source, start, end, dest, dstart, dend ->
            val result = StringBuilder()
            for (i in start until end) {
                val currentChar = source[i]
                if (Character.isDigit(currentChar) || currentChar == ':') {
                    result.append(currentChar)
                }
            }

            val newValue = dest.toString().substring(0, dstart) + result + dest.toString().substring(dend)
            if (newValue.length > 5) {
                return@InputFilter ""
            }

            if (newValue.length == 2 && !newValue.contains(":")) {
                return@InputFilter "$result:"
            }

            result
        }

        pickupTimeEditText.filters = arrayOf(timeFilter)

        pickupTimeEditText.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (s != null && s.length == 5) {
                    val hour = s.substring(0, 2).toIntOrNull()
                    val minute = s.substring(3, 5).toIntOrNull()
                    if (hour != null && minute != null) {
                        if (hour > 23 || minute > 59) {
                            pickupTimeEditText.error = "Hora inválida"
                        } else {
                            pickupTimeEditText.error = null
                        }
                    }
                }
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })


    }
}
