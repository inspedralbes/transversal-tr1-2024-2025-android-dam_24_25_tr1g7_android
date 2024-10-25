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
    val active: Int // Cambiado de Boolean a Int
) : Parcelable {

    // Método para obtener el estado activo como Boolean
    fun isActive(): Boolean {
        return active == 1 // Devuelve true si active es 1, false si es 0
    }
}