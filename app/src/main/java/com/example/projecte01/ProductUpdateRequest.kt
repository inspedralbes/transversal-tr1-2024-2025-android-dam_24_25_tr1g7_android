data class ProductUpdateRequest(
    val product_id: Int,
    val product_name: String,
    val description: String,
    val material: String,
    val price: Double,
    val stock: Int,
    val string_imatge: String,
    val owner_id: Int
)