
package com.example.loginapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar
import com.example.proyecte01.R
import retrofit2.*
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val cartProducts = mutableListOf<Product>()
    private lateinit var searchView: SearchView
    private lateinit var apiService: ApiService

    companion object {
        private const val BASE_URL = "http://dam.inspedralbes.cat:21345/"
        private const val TAG = "MainActivity"
    }

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


        productAdapter = ProductAdapter(listOf(), { product ->
            addToCart(product)
        }, true, { product ->
            openProductDetail(product)
        })
        recyclerView.adapter = productAdapter


        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        apiService = retrofit.create(ApiService::class.java)


        loadProductsFromServer()

        val cartIcon: ImageView = findViewById(R.id.cartIcon)
        cartIcon.setOnClickListener {
            openCart()
        }
    }

    private fun loadProductsFromServer() {
        val call = apiService.getProducts()

        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val products = response.body() ?: listOf()
                    Log.d(TAG, "Productos recibidos: ${products.size}") // Log para ver cuántos productos se recibieron
                    productAdapter.updateProducts(products)
                } else {
                    Log.e(TAG, "Error en la respuesta: ${response.code()}") // Log en caso de error
                    Toast.makeText(this@MainActivity, "Error al cargar productos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Log.e(TAG, "Error de red: ${t.message}") // Log en caso de fallo de red
                Toast.makeText(this@MainActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addToCart(product: Product) {
        if (!cartProducts.contains(product)) {
            cartProducts.add(product)
            Toast.makeText(this, "${product.product_name} añadido al carrito", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "${product.product_name} ya está en el carrito", Toast.LENGTH_SHORT).show()
        }
    }

    private fun openProductDetail(product: Product) {
        val intent = Intent(this, ProductDetailActivity::class.java)
        intent.putExtra("product", product)
        startActivity(intent)
    }


    private fun openCart() {
        val intent = Intent(this, CartActivity::class.java)
        intent.putParcelableArrayListExtra("cart_products", ArrayList(cartProducts))
        startActivity(intent)
    }


}