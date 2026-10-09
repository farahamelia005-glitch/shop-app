package com.orchidia.fashion.data.remote

import com.google.gson.annotations.SerializedName
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query

// Data produk yang diterima dari Supabase
data class SupabaseProduct(
    val id: String,
    val name: String,
    val category: String,
    val price: Int,
    @SerializedName("image_url")
    val imageUrl: String?,
    val rating: Double?,
    val description: String?
)

// Endpoint REST API Supabase
interface SupabaseApiService {

    @GET("rest/v1/products")
    suspend fun getProducts(
        @Query("select") select: String = "*"
    ): Response<List<SupabaseProduct>>
}

// Konfigurasi koneksi Supabase
object SupabaseApi {

    // Ganti dengan Project URL milikmu, pastikan diakhiri tanda /
    private const val BASE_URL = "https://oluvjhjnxgzgjijfawmp.supabase.co/"

    // Ganti dengan Publishable Key milikmu
    private const val PUBLISHABLE_KEY = "sb_publishable__KIXFwMEbCjXdGyIHPU9sg_e7xRIYJH"

    private val client: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(Interceptor { chain ->
                val request = chain.request()
                    .newBuilder()
                    .addHeader("apikey", PUBLISHABLE_KEY)
                    .addHeader("Authorization", "Bearer $PUBLISHABLE_KEY")
                    .addHeader("Accept", "application/json")
                    .build()

                chain.proceed(request)
            })
            .build()
    }

    val service: SupabaseApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SupabaseApiService::class.java)
    }
}