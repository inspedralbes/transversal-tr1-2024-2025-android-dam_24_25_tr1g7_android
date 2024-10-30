package com.example.loginapp

import Product
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyecte01.R

class TiendaProductAdapter(
    private var products: List<Product>,
    private val context: Context,
    private val onAddToCartClicked: (Product) -> Unit
) : RecyclerView.Adapter<TiendaProductAdapter.ViewHolder>() {

    private var filteredProducts: List<Product> = products

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val productImage: ImageView = view.findViewById(R.id.fullScreenProductImage)
        val productName: TextView = view.findViewById(R.id.fullScreenProductName)
        val productPrice: TextView = view.findViewById(R.id.fullScreenProductPrice)
        val productStock: TextView = view.findViewById(R.id.fullScreenProductStock)
        val addToCartButton: ImageButton = view.findViewById(R.id.addToCartButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_fullscreen_product, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val product = filteredProducts[position]
        holder.productName.text = product.product_name
        holder.productPrice.text = "${product.price} €"
        holder.productStock.text = "Stock: ${product.stock}"

        Glide.with(holder.itemView.context)
            .load("http://dam.inspedralbes.cat:21345/sources/Imatges/${product.image_file}")
            .into(holder.productImage)

        holder.addToCartButton.setOnClickListener {
            if (product.quantityInCart < product.stock) {
                onAddToCartClicked(product)
            } else {
                Toast.makeText(context, "No hay más stock disponible de ${product.product_name}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount() = filteredProducts.size

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        filteredProducts = newProducts
        notifyDataSetChanged()
    }

    fun filter(query: String) {
        filteredProducts = if (query.isEmpty()) {
            products
        } else {
            products.filter { it.product_name.contains(query, ignoreCase = true) } // Filtrar por nombre
        }
        notifyDataSetChanged()
    }
}

