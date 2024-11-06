package com.example.loginapp

import Product
import ProductCreateRequest
import ProductUpdateRequest
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageButton
import android.widget.SearchView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
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
    private lateinit var misProductosAdapter: MisProductosAdapter
    private lateinit var searchView: SearchView
    private lateinit var apiService: ApiService
    private lateinit var backButton: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mis_productos)

        initializeViews()
        setupRecyclerView()
        setupApiService()
        loadProductsFromServer()

        val addProductFab: FloatingActionButton = findViewById(R.id.addProduct)
        addProductFab.setOnClickListener {
            AddProduct()
        }

        backButton.setOnClickListener {
            onBackPressed()
        }
    }

    private fun initializeViews() {
        searchView = findViewById(R.id.searchView)
        recyclerView = findViewById(R.id.recyclerView)
        backButton = findViewById(R.id.backButton)

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                misProductosAdapter.filter(newText ?: "")
                return true
            }
        })
    }

    private fun setupRecyclerView() {
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        misProductosAdapter = MisProductosAdapter(
            listOf(),
            { product -> editProduct(product) },
            { product -> showDeleteConfirmationDialog(product.product_id) }
        )
        recyclerView.adapter = misProductosAdapter
    }

    private fun setupApiService() {
        apiService = RetrofitClient.instance
    }

    private fun loadProductsFromServer() {
        val userId = getUserId()
        apiService.getProducts().enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful) {
                    val allProducts = response.body() ?: listOf()
                    val userProducts = allProducts.filter { it.owner_id == userId }
                    misProductosAdapter.updateProducts(userProducts)

                    // Log para depuración
                    Log.d("MisProductos", "Productos cargados: ${userProducts.size} de ${allProducts.size}")
                    Log.d("MisProductos", "User ID: $userId")
                } else {
                    Toast.makeText(this@MisProductosActivity, "Error al cargar productos", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(this@MisProductosActivity, "Error de red: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun AddProduct() {
        val dialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_product, null)
        dialog.setView(dialogView)

        val nameEditText = dialogView.findViewById<EditText>(R.id.productNameEditText)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.productDescriptionEditText)
        val materialEditText = dialogView.findViewById<EditText>(R.id.productMaterialEditText)
        val priceEditText = dialogView.findViewById<EditText>(R.id.productPriceEditText)
        val stockEditText = dialogView.findViewById<EditText>(R.id.productStockEditText)
        val imageUrlEditText = dialogView.findViewById<EditText>(R.id.productImageUrlEditText)

        dialog.setPositiveButton("Añadir") { _, _ ->
            val newProduct = ProductCreateRequest(
                product_name = nameEditText.text.toString(),
                description = descriptionEditText.text.toString(),
                material = materialEditText.text.toString(),
                price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0,
                stock = stockEditText.text.toString().toIntOrNull() ?: 0,
                image_file = imageUrlEditText.text.toString(),
                owner_id = getUserId()
            )
            addProductToDatabase(newProduct)
        }

        dialog.setNegativeButton("Cancelar", null)
        dialog.show()
    }

    private fun addProductToDatabase(product: ProductCreateRequest) {
        apiService.createProduct(product).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    val newProduct = response.body()
                    if (newProduct != null) {
                        Toast.makeText(this@MisProductosActivity, "Producto añadido con éxito", Toast.LENGTH_SHORT).show()
                        loadProductsFromServer() // Recarga la lista de productos
                    } else {
                        Log.e("MisProductos", "Respuesta exitosa pero cuerpo nulo")
                        Toast.makeText(this@MisProductosActivity, "Error: Respuesta vacía del servidor", Toast.LENGTH_LONG).show()
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("MisProductos", "Error en la respuesta: ${response.code()}, Body: $errorBody")
                    Toast.makeText(this@MisProductosActivity, "Error al añadir el producto: ${response.code()}", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                loadProductsFromServer()
            }
        })
    }
    private fun editProduct(product: Product) {
        val dialog = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.dialog_edit_product, null)
        dialog.setView(dialogView)

        val nameEditText = dialogView.findViewById<EditText>(R.id.editProductNameEditText)
        val descriptionEditText = dialogView.findViewById<EditText>(R.id.editProductDescriptionEditText)
        val materialEditText = dialogView.findViewById<EditText>(R.id.editProductMaterialEditText)
        val priceEditText = dialogView.findViewById<EditText>(R.id.editProductPriceEditText)
        val stockEditText = dialogView.findViewById<EditText>(R.id.editProductStockEditText)
        val imageUrlEditText = dialogView.findViewById<EditText>(R.id.editProductImageUrlEditText)

        nameEditText.setText(product.product_name)
        descriptionEditText.setText(product.description)
        materialEditText.setText(product.material)
        priceEditText.setText(product.price.toString())
        stockEditText.setText(product.stock.toString())
        imageUrlEditText.setText(product.image_file)

        dialog.setPositiveButton("Actualizar") { _, _ ->
            val updatedProduct = ProductUpdateRequest(
                product_id = product.product_id,
                product_name = nameEditText.text.toString(),
                description = descriptionEditText.text.toString(),
                material = materialEditText.text.toString(),
                price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0,
                stock = stockEditText.text.toString().toIntOrNull() ?: 0,
                image_file = imageUrlEditText.text.toString(),
                owner_id = product.owner_id
            )
            updateProductInDatabase(updatedProduct)
        }

        dialog.setNegativeButton("Cancelar", null)

        dialog.setNeutralButton("Eliminar") { _, _ ->
            showDeleteConfirmationDialog(product.product_id)
        }

        dialog.show()
    }

    private fun updateProductInDatabase(product: ProductUpdateRequest) {
        apiService.updateProduct(product).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MisProductosActivity, "Producto actualizado con éxito", Toast.LENGTH_SHORT).show()
                    loadProductsFromServer()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@MisProductosActivity, "Error al actualizar el producto: $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                loadProductsFromServer()
            }
        })
    }

    private fun showDeleteConfirmationDialog(productId: Int) {
        AlertDialog.Builder(this)
            .setTitle("Eliminar producto")
            .setMessage("¿Estás seguro de que quieres eliminar este producto?")
            .setPositiveButton("Sí") { _, _ ->
                deleteProductFromDatabase(productId)
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun deleteProductFromDatabase(productId: Int) {
        apiService.deleteProduct(productId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(this@MisProductosActivity, "Producto eliminado con éxito", Toast.LENGTH_SHORT).show()
                    loadProductsFromServer()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(this@MisProductosActivity, "Error al eliminar el producto: $errorBody", Toast.LENGTH_LONG).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                loadProductsFromServer()
            }
        })
    }

    private fun openProductDetail(product: Product) {
        val intent = Intent(this, DetalleProductoAcivity::class.java)
        intent.putExtra("product", product)
        startActivity(intent)
    }

    private fun getUserId(): Int {
        val sharedPref = getSharedPreferences("UserSession", MODE_PRIVATE)
        return sharedPref.getInt("user_id", -1)
    }
}