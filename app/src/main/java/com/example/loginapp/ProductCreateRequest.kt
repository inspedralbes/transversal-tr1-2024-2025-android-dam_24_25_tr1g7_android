data class ProductCreateRequest(
    val product_name: String,
    val description: String,
    val price: Double,
    val stock: Int,
    val image_file: String
)
