package com.orchidia.fashion.data.remote

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PATCH
import retrofit2.http.Query

data class CartItemRequest(
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("product_id")
    val productId: String,
    val quantity: Int
)

data class CartItemRemote(
    val id: String,
    @SerializedName("session_id")
    val sessionId: String,
    @SerializedName("product_id")
    val productId: String,
    val quantity: Int
)

interface SupabaseCartService {

    @GET("rest/v1/cart_items")
    suspend fun getCart(
        @Query("select") select: String = "*",
        @Query("session_id") sessionFilter: String,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<CartItemRemote>>


    @POST("rest/v1/cart_items")
    suspend fun insertCartItem(
        @Body item: CartItemRequest,
        @Query("on_conflict") onConflict: String = "session_id,product_id",
        @Header("Prefer") prefer: String =
            "return=representation,resolution=merge-duplicates"
    ): Response<List<CartItemRemote>>

    @PATCH("rest/v1/cart_items")
    suspend fun updateQuantity(
        @Query("session_id") sessionFilter: String,
        @Query("product_id") productFilter: String,
        @Body body: Map<String, Int>,
        @Header("Prefer") prefer: String = "return=representation"
    ): Response<List<CartItemRemote>>

    @DELETE("rest/v1/cart_items")
    suspend fun deleteCartItem(
        @Query("session_id") sessionFilter: String,
        @Query("product_id") productFilter: String
    ): Response<Void>
}