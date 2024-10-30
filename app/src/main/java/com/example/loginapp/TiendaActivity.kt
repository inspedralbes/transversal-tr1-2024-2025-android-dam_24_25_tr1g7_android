package com.example.loginapp

import Product
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.proyecte01.R
import com.google.android.material.navigation.NavigationView
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class TiendaActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var productAdapter: TiendaProductAdapter
    private var cartProducts = mutableListOf<Product>() // Lista de productos en el carrito

    override fun onResume() {
        super.onResume()
        cargarDetallesUsuarioMenu()
        loadCartFromStorage() // Cargar el carrito cada vez que se reanuda la actividad
        updateCartItemCount() // Actualizar el contador del carrito en el menú
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tienda_product)

        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.setNavigationItemSelectedListener(this)

        val toolbar: androidx.appcompat.widget.Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, toolbar,
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        cargarDetallesUsuarioMenu()

        val recyclerView: RecyclerView = findViewById(R.id.fullScreenRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = TiendaProductAdapter(emptyList(), this) { product ->
            addToCart(product)
        }
        recyclerView.adapter = productAdapter

        loadProductsFromServer()
    }

    private fun cargarDetallesUsuarioMenu() {
        val headerView = navigationView.getHeaderView(0)
        val userNameTextView: TextView = headerView.findViewById(R.id.user_name)
        val userEmailTextView: TextView = headerView.findViewById(R.id.user_email)

        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        userNameTextView.text = sharedPref.getString("username", "N/A")
        userEmailTextView.text = sharedPref.getString("email", "N/A")
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_home -> startActivity(Intent(this, TiendaActivity::class.java))
            R.id.nav_perfil -> startActivity(Intent(this, PerfilActivity::class.java))
            R.id.nav_mis_productos -> startActivity(Intent(this, MisProductosActivity::class.java))
            R.id.nav_mis_pedidos -> startActivity(Intent(this, MisProductosActivity::class.java))
            R.id.nav_cart -> startActivity(Intent(this, CarritoActivity::class.java))
            R.id.nav_logout -> logout()
        }
        drawerLayout.closeDrawers()
        return true
    }

    private fun logout() {
        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        sharedPref.edit().clear().apply()

        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }

    private fun loadProductsFromServer() {
        RetrofitClient.instance.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val products = response.body() ?: emptyList()
                    productAdapter.updateProducts(products)
                } else {
                    Toast.makeText(this@TiendaActivity, "Error al cargar productos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@TiendaActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
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
                return
            }
        } else {
            if (existingProduct.quantityInCart < existingProduct.stock) {
                existingProduct.quantityInCart++
                Toast.makeText(this, "Cantidad de ${product.product_name} incrementada", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No hay más stock disponible de ${product.product_name}", Toast.LENGTH_SHORT).show()
                return
            }
        }

        productAdapter.notifyDataSetChanged()

        saveCartToStorage()

        updateCartItemCount()
    }

    private fun saveCartToStorage() {
        val sharedPref = getSharedPreferences("CartPreferences", Context.MODE_PRIVATE)

        val cartJson = Gson().toJson(cartProducts)

        with(sharedPref.edit()) {
            putString("cart", cartJson)
            apply()
        }

        Log.d("TiendaActivity", "Carrito guardado: $cartJson")
    }

    private fun loadCartFromStorage() {
        val sharedPref = getSharedPreferences("CartPreferences", Context.MODE_PRIVATE)

        val cartJson = sharedPref.getString("cart", "")

        if (!cartJson.isNullOrEmpty()) {
            val type = object : TypeToken<List<Product>>() {}.type

            cartProducts.clear()
            cartProducts.addAll(Gson().fromJson(cartJson, type))

            Log.d("TiendaActivity", "Carrito cargado: ${cartProducts.size} productos")

            updateCartItemCount()

            productAdapter.notifyDataSetChanged()

        } else {
            Log.d("TiendaActivity", "No se encontró carrito guardado")
            cartProducts.clear()
        }
    }

    private fun updateCartItemCount() {
        val cartMenuItem = navigationView.menu.findItem(R.id.nav_cart)
        cartMenuItem.title = "Carrito (${cartProducts.size})"
    }
}