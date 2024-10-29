package com.example.loginapp

import Product
import android.content.Context
import android.content.Intent
import android.os.Bundle
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class FullScreenProductActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var productAdapter: FullScreenProductAdapter

    override fun onResume() {
        super.onResume()
        loadUserDetailsInDrawer()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fullscreen_product)

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

        loadUserDetailsInDrawer()

        val recyclerView: RecyclerView = findViewById(R.id.fullScreenRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        productAdapter = FullScreenProductAdapter(emptyList(), this) { product ->
            if (product.quantityInCart < product.stock) {
                product.quantityInCart++
                Toast.makeText(this, "${product.product_name} añadido al carrito", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "No hay más stock disponible de ${product.product_name}", Toast.LENGTH_SHORT).show()
            }
        }
        recyclerView.adapter = productAdapter

        loadProductsFromServer()
    }

    private fun loadUserDetailsInDrawer() {
        val headerView = navigationView.getHeaderView(0)
        val userNameTextView: TextView = headerView.findViewById(R.id.user_name)
        val userEmailTextView: TextView = headerView.findViewById(R.id.user_email)

        val sharedPref = getSharedPreferences("UserSession", Context.MODE_PRIVATE)
        userNameTextView.text = sharedPref.getString("username", "N/A")
        userEmailTextView.text = sharedPref.getString("email", "N/A")
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_profile -> startActivity(Intent(this, PerfilActivity::class.java))
            R.id.nav_my_products -> startActivity(Intent(this, MainActivity::class.java))
            R.id.nav_cart -> startActivity(Intent(this, CartActivity::class.java).apply {
                putParcelableArrayListExtra("cart_products", ArrayList(productAdapter.cartProducts))
            })
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
                    Toast.makeText(this@FullScreenProductActivity, "Error al cargar productos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@FullScreenProductActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
