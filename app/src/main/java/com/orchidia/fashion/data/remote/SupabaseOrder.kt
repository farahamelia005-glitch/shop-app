package com.orchidia.fashion.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST

data class OrderRequest(
    val id: String,
    @SerializedName("session_id") val sessionId: String,
    @SerializedName("customer_name") val customerName: String,
    @SerializedName("customer_phone") val customerPhone: String,
    @SerializedName("address_street") val addressStreet: String,
    @SerializedName("address_city") val addressCity: String,
    @SerializedName("address_postal") val addressPostal: String,
    @SerializedName("address_province") val addressProvince: String,
    @SerializedName("shipping_method") val shippingMethod: String,
    val subtotal: Int,
    @SerializedName("shipping_cost") val shippingCost: Int,
    val total: Int,
    @SerializedName("payment_method") val paymentMethod: String,
    val status: String = "Paid"
)

data class OrderItemRequest(
    @SerializedName("order_id") val orderId: String,
    @SerializedName("product_id") val productId: String,
    @SerializedName("product_name") val productName: String,
    val price: Int,
    val quantity: Int
)

interface SupabaseOrderService {
    @POST("rest/v1/orders")
    suspend fun insertOrder(
        @Body order: OrderRequest,
        @Header("Prefer") prefer: String = "return=minimal"
    ): Response<Void>

    @POST("rest/v1/order_items")
    suspend fun insertOrderItems(
        @Body items: List<OrderItemRequest>,
        @Header("Prefer") prefer: String = "return=minimal"
    ): Response<Void>
}