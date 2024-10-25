package com.example.loginapp
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyecte01.R

class ProductAdapter(
    private var products: List<Product>,
    private val onAddToCartClicked: (Product) -> Unit,
    private val showAddToCartButton: Boolean,
    private val onProductClicked: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private var filteredProducts = products

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val addToCartButton: Button = itemView.findViewById(R.id.addToCartButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = filteredProducts[position]
        holder.productName.text = product.product_name
        holder.productPrice.text = "${product.price} €"


        val imageUrl = "http://dam.inspedralbes.cat:21345/sources/Imatges/${product.image_file}"
        Log.d("ProductAdapter", "Loading image from URL: $imageUrl")


        Glide.with(holder.itemView.context)
            .load(imageUrl)
            .into(holder.productImage)


        holder.addToCartButton.visibility = if (showAddToCartButton) View.VISIBLE else View.GONE


        holder.itemView.setOnClickListener {
            onProductClicked(product)
        }


        holder.addToCartButton.setOnClickListener {
            if (showAddToCartButton) {
                onAddToCartClicked(product)
            }
        }
    }

    override fun getItemCount(): Int {
        return filteredProducts.size
    }

    fun filter(query: String) {
        filteredProducts = if (query.isEmpty()) {
            products
        } else {
            products.filter {
                it.product_name.contains(query, ignoreCase = true)
            }
        }
        notifyDataSetChanged()
    }

    fun updateProducts(newProducts: List<Product>) {
        products = newProducts
        filteredProducts = newProducts
        notifyDataSetChanged()
    }
}