data class Order(
    val order_id: Int,
    val user_id: Int,
    val product_id: Int,
    val order_date: String,
    val status: String,
    val total: Double
)