package com.example.loginapp

import Product
import android.os.Bundle
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.proyecte01.R

class ProductDetailActivity : AppCompatActivity() {

    private lateinit var backButton: ImageButton
    private lateinit var productName: TextView
    private lateinit var productDescription: TextView
    private lateinit var productPrice: TextView
    private lateinit var productImage: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        backButton = findViewById(R.id.backButton)
        productName = findViewById(R.id.productName)
        productDescription = findViewById(R.id.product_description)
        productPrice = findViewById(R.id.productPrice)
        productImage = findViewById(R.id.productImage)


        backButton.setOnClickListener {
            onBackPressed()
        }


        val product: Product? = intent.getParcelableExtra("product")


        product?.let {
            productName.text = it.product_name
            productPrice.text = "${it.price} €"
            productDescription.text = it.description

            // Cargar la imagen utilizando Glide
            val imageUrl = "http://dam.inspedralbes.cat:21345/sources/Imatges/${it.image_file}"
            Glide.with(this)
                .load(imageUrl)
                .into(productImage)
        }
    }
}