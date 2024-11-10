
data class OrderRequest(
    val user_id: Int,
    val product_id: Int,
    val total: Double,
    val status: String = "waiting"
)
