package com.example.loginapp

import Product
import android.content.Intent
import android.os.Bundle
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecte01.R
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MisProductosActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private lateinit var searchView: SearchView
    private val cartProducts = mutableListOf<Product>()
    private lateinit var apiService: ApiService
    private lateinit var backButton: ImageButton

    companion object {
        private const val BASE_URL = "http://dam.inspedralbes.cat:21345/"
        private const val TAG = "MainActivity"

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_productos)



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
        productAdapter = ProductAdapter(
            listOf(),
            { product -> addToCart(product) },
            true,
            { product -> openProductDetail(product) }
        )
        recyclerView.adapter = productAdapter

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        apiService = retrofit.create(ApiService::class.java)

        loadProductsFromServer()

        val cartFab: FloatingActionButton = findViewById(R.id.cartFab)
        cartFab.setOnClickListener {
            openCart()
        }

        backButton = findViewById(R.id.backButton)
        backButton.setOnClickListener {
            onBackPressed()
        }
    }


    private fun loadProductsFromServer() {
        val call = apiService.getProducts()
        call.enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val products = response.body() ?: listOf()
                    productAdapter.updateProducts(products)
                } else {
                    Toast.makeText(this@MisProductosActivity, "Error al cargar productos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@MisProductosActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun addToCart(product: Product) {
        val existingProduct = cartProducts.find { it.product_id == product.product_id }
        if (existingProduct == null) {
            if (product.stock > 0) {
                product.quantityInCart = 1
                cartProducts.add(product)
                Toast.makeText(this, "${product.product_name} añadido al carrito", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No hay stock disponible de ${product.product_name}", Toast.LENGTH_SHORT).show()
            }
        } else {
            if (existingProduct.quantityInCart < existingProduct.stock) {
                existingProduct.quantityInCart++
                Toast.makeText(this, "Cantidad de ${product.product_name} incrementada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No hay más stock disponible de ${product.product_name}", Toast.LENGTH_SHORT).show()
            }
        }
        productAdapter.notifyDataSetChanged()
    }

    private fun openProductDetail(product: Product) {
        val intent = Intent(this, ProductDetailActivity::class.java)
        intent.putExtra("product", product)
        startActivity(intent)
    }

    private fun openCart() {
        val intent = Intent(this, CarritoActivity::class.java)
        intent.putParcelableArrayListExtra("cart_products", ArrayList(cartProducts))
        startActivity(intent)
    }



    private fun logout() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }
}