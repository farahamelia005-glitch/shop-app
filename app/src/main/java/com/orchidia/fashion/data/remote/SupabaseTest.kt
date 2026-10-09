package com.orchidia.fashion.data.remote

import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

object SupabaseTest {

    fun testConnection() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = SupabaseApi.service.getProducts()

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val products = response.body().orEmpty()

                        Log.d(
                            "SUPABASE_TEST",
                            "Berhasil! Jumlah produk: ${products.size}"
                        )

                        products.forEach { product ->
                            Log.d(
                                "SUPABASE_TEST",
                                "${product.name} | Rp ${product.price}"
                            )
                        }
                    } else {
                        Log.e(
                            "SUPABASE_TEST",
                            "HTTP ${response.code()}: ${response.errorBody()?.string()}"
                        )
                    }
                }
            } catch (e: Exception) {
                Log.e(
                    "SUPABASE_TEST",
                    "Koneksi gagal: ${e.message}",
                    e
                )
            }
        }
    }
}