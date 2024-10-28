import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Product(
    val product_id: Int,
    val product_name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val image_file: String,
    val active: Int,
    var quantityInCart: Int = 0  // Nueva propiedad
) : Parcelable {
    fun isActive(): Boolean {
        return active == 1
    }

    fun canAddToCart(): Boolean {
        return quantityInCart < stock
    }
}