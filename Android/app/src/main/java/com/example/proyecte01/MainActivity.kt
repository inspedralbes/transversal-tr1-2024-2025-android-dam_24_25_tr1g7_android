package com.example.proyecte01

import Product
import ProductAdapter
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.appcompat.widget.Toolbar

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var productAdapter: ProductAdapter
    private val cartProducts = mutableListOf<Product>()
    private lateinit var searchView: SearchView

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

        val dummyProducts = listOf(
            Product(1, "Skogsvärdet (The Forest Blade)", "Straight from the heart of the Scandinavian wilderness...", 999.99, 10, "https://encrypted-tbn0.gstatic.com/images?q=tbn:ANd9GcQGGQJeHVKNeQXSYNwvo9XcNjMEW3P6ykFjLQ&s", true),
            Product(2, "Bebida", "Refresco frío", 1.50, 20, "https://example.com/image2.jpg", true),
            Product(3, "Ensalada", "Ensalada fresca y saludable", 4.50, 15, "https://example.com/image3.jpg", true),
            Product(4, "Bebida", "Refresco frío", 1.50, 20, "https://example.com/image2.jpg", true),
            Product(5, "Bebida", "Refresco frío", 1.50, 20, "https://example.com/image2.jpg", true),
            Product(6, "Bebida", "Refresco frío", 1.50, 20, "https://example.com/image2.jpg", true),
            Product(7, "Bebida", "Refresco frío", 1.50, 20, "https://example.com/image2.jpg", true),
            Product(8, "Bebida", "Refresco frío", 1.50, 20, "https://example.com/image2.jpg", true),
            Product(9, "Bebida", "Refresco frío", 1.50, 20, "https://example.com/image2.jpg", true),
            Product(10, "Bebida", "Refresco frío", 1.50, 20, "https://example.com/image2.jpg", true),
            Product(11, "Bebida", "Refresco frío", 1.50, 20, "https://example.com/image2.jpg", true),
            Product(12, "Bebida", "Refresco frío", 1.50, 20, "https://example.com/image2.jpg", true),
            Product(13, "Bebida", "Refresco frío", 1.50, 20, "https://example.com/image2.jpg", true)
        )

        productAdapter = ProductAdapter(dummyProducts, { product ->
            addToCart(product)
        }, true)

        recyclerView.adapter = productAdapter

        val cartIcon: ImageView = findViewById(R.id.cartIcon)
        cartIcon.setOnClickListener {
            val intent = Intent(this, CartActivity::class.java)
            intent.putParcelableArrayListExtra("cart_products", ArrayList(cartProducts))
            startActivity(intent)
        }
    }

    private fun addToCart(product: Product) {
        cartProducts.add(product)
        Toast.makeText(this, "${product.product_name} añadido al carrito", Toast.LENGTH_SHORT).show()
    }
}
