import retrofit2.Call
import retrofit2.http.GET

interface ApiService {
    @GET("getProductes")
    fun getProducts(): Call<List<Product>>
}