package com.orchidia.fashion.data.remote

import android.content.Context
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object SupabaseCartRepository {

    suspend fun getCart(context: Context): List<CartItemRemote>? =
        withContext(Dispatchers.IO) {
            try {
                val sessionId = CartSession.getSessionId(context)

                val response = SupabaseApi.cartService.getCart(
                    sessionFilter = "eq.$sessionId"
                )

                if (response.isSuccessful) {
                    response.body().orEmpty()
                } else {
                    Log.e("ORCHIDIA_CART", "Get gagal: HTTP ${response.code()}")
                    null
                }
            } catch (e: Exception) {
                Log.e("ORCHIDIA_CART", "Get gagal: ${e.message}", e)
                null
            }
        }

    suspend fun add(
        context: Context,
        productId: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val sessionId = CartSession.getSessionId(context)

            // Ambil jumlah lama terlebih dahulu.
            val currentResponse = SupabaseApi.cartService.getCart(
                sessionFilter = "eq.$sessionId"
            )

            if (!currentResponse.isSuccessful) {
                Log.e("ORCHIDIA_CART", "Get sebelum add gagal")
                return@withContext false
            }

            val currentQuantity = currentResponse.body()
                .orEmpty()
                .find { it.productId == productId }
                ?.quantity ?: 0

            val response = SupabaseApi.cartService.insertCartItem(
                CartItemRequest(
                    sessionId = sessionId,
                    productId = productId,
                    quantity = currentQuantity + 1
                )
            )

            if (!response.isSuccessful) {
                Log.e(
                    "ORCHIDIA_CART",
                    "Add gagal: HTTP ${response.code()} ${response.errorBody()?.string()}"
                )
            }

            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ORCHIDIA_CART", "Add gagal: ${e.message}", e)
            false
        }
    }

    suspend fun update(
        context: Context,
        productId: String,
        quantity: Int
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            if (quantity <= 0) {
                return@withContext remove(context, productId)
            }

            val sessionId = CartSession.getSessionId(context)

            val response = SupabaseApi.cartService.updateQuantity(
                sessionFilter = "eq.$sessionId",
                productFilter = "eq.$productId",
                body = mapOf("quantity" to quantity)
            )

            if (!response.isSuccessful) {
                Log.e(
                    "ORCHIDIA_CART",
                    "Update gagal: HTTP ${response.code()}"
                )
            }

            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ORCHIDIA_CART", "Update gagal: ${e.message}", e)
            false
        }
    }

    suspend fun remove(
        context: Context,
        productId: String
    ): Boolean = withContext(Dispatchers.IO) {
        try {
            val sessionId = CartSession.getSessionId(context)

            val response = SupabaseApi.cartService.deleteCartItem(
                sessionFilter = "eq.$sessionId",
                productFilter = "eq.$productId"
            )

            if (!response.isSuccessful) {
                Log.e(
                    "ORCHIDIA_CART",
                    "Delete gagal: HTTP ${response.code()}"
                )
            }

            response.isSuccessful
        } catch (e: Exception) {
            Log.e("ORCHIDIA_CART", "Delete gagal: ${e.message}", e)
            false
        }
    }
}