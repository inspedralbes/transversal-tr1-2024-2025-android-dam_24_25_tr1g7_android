package com.example.loginapp

import Product
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyecte01.R

class CartAdapter(
    private val cartProducts: MutableList<Product>,
    private val onIncreaseQuantity: (Product) -> Unit,
    private val onDecreaseQuantity: (Product) -> Unit,
    private val onRemoveProduct: (Product) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.cartProductImage)
        val productName: TextView = itemView.findViewById(R.id.cartProductName)
        val productPrice: TextView = itemView.findViewById(R.id.cartProductPrice)
        val productQuantity: TextView = itemView.findViewById(R.id.cartProductQuantity)
        val increaseButton: Button = itemView.findViewById(R.id.increaseQuantityButton)
        val decreaseButton: Button = itemView.findViewById(R.id.decreaseQuantityButton)
        val removeButton: Button = itemView.findViewById(R.id.removeProductButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = cartProducts[position]

        holder.productName.text = product.product_name
        holder.productPrice.text = String.format("%.2f €", product.price)
        holder.productQuantity.text = product.quantityInCart.toString()

        // Cargar la imagen del producto usando Glide
        Glide.with(holder.itemView.context)
            .load("http://dam.inspedralbes.cat:21345/sources/Imatges/${product.image_file}")
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_dialog_alert)
            .into(holder.productImage)

        holder.increaseButton.setOnClickListener { onIncreaseQuantity(product) }
        holder.decreaseButton.setOnClickListener { onDecreaseQuantity(product) }
        holder.removeButton.setOnClickListener { onRemoveProduct(product) }

        // Deshabilitar el botón de disminuir si la cantidad es 1
        holder.decreaseButton.isEnabled = product.quantityInCart > 1
    }

    override fun getItemCount() = cartProducts.size

    fun updateProducts(newProducts: List<Product>) {
        cartProducts.clear()
        cartProducts.addAll(newProducts)
        notifyDataSetChanged()
    }
}