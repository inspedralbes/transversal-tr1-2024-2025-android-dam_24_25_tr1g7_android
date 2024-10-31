package com.example.loginapp

import Product
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.proyecte01.R

class CarritoAdapter(
    private val cartProducts: MutableList<Product>,
    private val onIncreaseQuantity: (Product) -> Unit,
    private val onDecreaseQuantity: (Product) -> Unit,
    private val onRemoveProduct: (Product) -> Unit
) : RecyclerView.Adapter<CarritoAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.cartProductImage)
        val productName: TextView = itemView.findViewById(R.id.cartProductName)
        val productPrice: TextView = itemView.findViewById(R.id.cartProductPrice)
        val productQuantity: TextView = itemView.findViewById(R.id.cartProductQuantity)
        val increaseButton: ImageButton = itemView.findViewById(R.id.increaseQuantityButton)
        val decreaseButton: ImageButton = itemView.findViewById(R.id.decreaseQuantityButton)
        val removeButton: ImageButton = itemView.findViewById(R.id.removeProductButton)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart_product, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val product = cartProducts[position]

        holder.productName.text = product.product_name
        holder.productPrice.text = String.format("%.2f €", product.price)
        holder.productQuantity.text = product.quantityInCart.toString()

        Glide.with(holder.itemView.context)
            .load("http://dam.inspedralbes.cat:21345/sources/Imatges/${product.image_file}")
            .placeholder(android.R.drawable.ic_menu_gallery)
            .error(android.R.drawable.ic_dialog_alert)
            .into(holder.productImage)

        holder.increaseButton.setOnClickListener {
            if (product.quantityInCart < product.stock) {
                onIncreaseQuantity(product)
            }
        }

        holder.decreaseButton.setOnClickListener {
            if (product.quantityInCart > 1) {
                onDecreaseQuantity(product)
            } else {
                onRemoveProduct(product)
            }
        }

        holder.removeButton.setOnClickListener {
            onRemoveProduct(product)
        }

        holder.decreaseButton.isEnabled = product.quantityInCart > 1
        holder.increaseButton.isEnabled = product.quantityInCart < product.stock
    }

    override fun getItemCount() = cartProducts.size

    fun updateProducts(newProducts: List<Product>) {
        cartProducts.clear()
        cartProducts.addAll(newProducts)
        notifyDataSetChanged()
    }
}
