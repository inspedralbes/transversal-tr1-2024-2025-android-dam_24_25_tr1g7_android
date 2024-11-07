package com.example.loginapp

import Product
import ProductCreateRequest
import ProductUpdateRequest
import android.app.Activity
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

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.provider.MediaStore
import android.util.Base64
import android.view.View
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.contract.ActivityResultContracts
import com.google.gson.Gson
import java.io.ByteArrayOutputStream



class MisProductosActivity : AppCompatActivity() {



    private lateinit var recyclerView: RecyclerView
    private lateinit var misProductosAdapter: MisProductosAdapter
    private lateinit var searchView: SearchView
    private lateinit var apiService: ApiService
    private lateinit var backButton: ImageButton
    private var imageBase64: String = ""
    private lateinit var imagePreview: ImageView

    private val getContent = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            val imageBitmap = data?.extras?.get("data") as? Bitmap
            if (imageBitmap != null) {
                imagePreview.setImageBitmap(imageBitmap)
                imagePreview.visibility = View.VISIBLE
                imageBase64 = convertBitmapToBase64(imageBitmap)
                Log.d("string_image", "Base64 length: ${imageBase64.length}")
            }
        }
    }
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
        val buttonSelectImage = dialogView.findViewById<Button>(R.id.buttonSelectImage)
        imagePreview = dialogView.findViewById(R.id.imagePreview)

        buttonSelectImage.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            getContent.launch(takePictureIntent)
        }

        dialog.setPositiveButton("Añadir") { _, _ ->
            val newProduct = ProductCreateRequest(
                product_name = nameEditText.text.toString(),
                description = descriptionEditText.text.toString(),
                material = materialEditText.text.toString(),
                price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0,
                stock = stockEditText.text.toString().toIntOrNull() ?: 0,
                string_imatge = imageBase64, // Usar la imagen en Base64
                owner_id = getUserId()
            )
            addProductToDatabase(newProduct)
            Log.d("image_file", newProduct.string_imatge)
        }

        dialog.setNegativeButton("Cancelar", null)
        dialog.show()
    }

    private fun convertBitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
        outputStream.flush()
        val byteArray = outputStream.toByteArray()
        outputStream.close()
        Log.d("ImageConversion", "Base64 length: ${imageBase64.length}")
        return Base64.encodeToString(byteArray, Base64.NO_WRAP)
    }



    private fun addProductToDatabase(product: ProductCreateRequest) {
        Log.d("ProductUpload", "Image size before serialization: ${product.string_imatge.length} characters")
        apiService.createProduct(product).enqueue(object : Callback<String> {
            override fun onResponse(call: Call<String>, response: Response<String>) {
                if (response.isSuccessful) {
                    val responseBody = response.body()
                    Log.d("ProductUpload", "Server response: $responseBody")
                    Toast.makeText(this@MisProductosActivity, "Producto añadido con éxito", Toast.LENGTH_SHORT).show()
                    loadProductsFromServer()
                } else {
                    val errorBody = response.errorBody()?.string()
                    Log.e("ProductUpload", "Error response: $errorBody")
                    Toast.makeText(this@MisProductosActivity, "Error al añadir el producto", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<String>, t: Throwable) {
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
        val buttonSelectImage = dialogView.findViewById<Button>(R.id.buttonEditSelectImage)
        val imagePreview = dialogView.findViewById<ImageView>(R.id.editImagePreview)


        nameEditText.setText(product.product_name)
        descriptionEditText.setText(product.description)
        materialEditText.setText(product.material)
        priceEditText.setText(product.price.toString())
        stockEditText.setText(product.stock.toString())


        if (product.image_file.isNotEmpty()) {

            val decodedString = Base64.decode(product.image_file, Base64.DEFAULT)
            val decodedByte = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
            imagePreview.setImageBitmap(decodedByte)
            imagePreview.visibility = View.VISIBLE
        }

        buttonSelectImage.setOnClickListener {
            val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            getContent.launch(takePictureIntent)
        }

        dialog.setPositiveButton("Actualizar") { _, _ ->
            val updatedProduct = ProductUpdateRequest(
                product_id = product.product_id,
                product_name = nameEditText.text.toString(),
                description = descriptionEditText.text.toString(),
                material = materialEditText.text.toString(),
                price = priceEditText.text.toString().toDoubleOrNull() ?: 0.0,
                stock = stockEditText.text.toString().toIntOrNull() ?: 0,
                string_imatge = imageBase64, // Use the updated Base64 string
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