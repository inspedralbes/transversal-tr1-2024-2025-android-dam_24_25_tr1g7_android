package com.example.proyecte01

import ApiService
import Product
import ProductAdapter
import android.content.Intent
import android.os.Bundle
import android.util.Log // Importar Log para depuración
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val cartProducts = mutableListOf<Product>()
    private lateinit var searchView: SearchView
    private lateinit var apiService: ApiService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        searchView = findViewById(R.id.searchView)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                productAdapter.filter(newText ?: "")
                return true
            }
        })

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = GridLayoutManager(this, 2)

        // Inicializar el adaptador vacío antes de cargar los productos
        productAdapter = ProductAdapter(listOf(), { product ->
            addToCart(product)
        }, true)
        recyclerView.adapter = productAdapter

        // Inicializar Retrofit
        val retrofit = Retrofit.Builder()
            .baseUrl("HTTP://DAM.INSPEDRALBES.CAT:21345/") // Cambia la URL por la de tu servidor
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)

        // Cargar productos del servidor
        loadProductsFromServer()

        val cartIcon: ImageView = findViewById(R.id.cartIcon)
        cartIcon.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.putParcelableArrayListExtra("cart_products", ArrayList(cartProducts))
            startActivity(intent)
        }
    }

    private fun loadProductsFromServer() {
        val call = apiService.getProducts()

        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val products = response.body() ?: listOf()
                    Log.d("MainActivity", "Productos recibidos: ${products.size}") // Log para ver cuántos productos se recibieron
                    productAdapter.updateProducts(products)
                } else {
                    Log.e("MainActivity", "Error en la respuesta: ${response.code()}") // Log en caso de error
                    Toast.makeText(this@MainActivity, "Error al cargar productos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.e("MainActivity", "Error de red: ${t.message}") // Log en caso de fallo de red
                Toast.makeText(this@MainActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addToCart(product: Product) {
        cartProducts.add(product)
        Toast.makeText(this, "${product.product_name} añadido al carrito", Toast.LENGTH_SHORT).show()
    }
}
