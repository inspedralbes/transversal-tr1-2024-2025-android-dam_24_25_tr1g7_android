data class ProductUpdateRequest(
    val product_id: Int,
    val product_name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val image_file: String
)