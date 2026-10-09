package com.orchidia.fashion

import android.content.Context
import android.content.Intent
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class OrderHistoryItem(
    val id: String,
    val date: String,
    val paymentMethod: String,
    val total: Int,
    val status: String,
    val items: String,
    val address: String
)

object OrderManager {

    private const val PREFS = "orchidia_orders"
    private const val KEY_ORDERS = "orders"

    fun saveOrder(
        context: Context,
        intent: Intent,
        paymentMethod: String
    ): String {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val orders = JSONArray(prefs.getString(KEY_ORDERS, "[]"))

        val selectedIds = intent.getStringArrayListExtra("SELECTED_PRODUCTS")
            ?: arrayListOf()

        val itemLines = selectedIds.mapNotNull { id ->
            val product = ProductRepository.getById(id) ?: return@mapNotNull null
            val quantity = CartManager.getQuantity(id).coerceAtLeast(1)
            "${product.name} x$quantity — Rp ${
                java.text.NumberFormat.getNumberInstance(Locale("id", "ID"))
                    .format(product.price * quantity)
            }"
        }

        val total = intent.getIntExtra(
            "ORDER_TOTAL",
            intent.getIntExtra("ORDER_SUBTOTAL", 0) +
                    intent.getIntExtra("ORDER_SHIPPING", 15000)
        )

        val addressName = intent.getStringExtra("ADDRESS_NAME").orEmpty()
        val addressStreet = intent.getStringExtra("ADDRESS_STREET").orEmpty()
        val addressCity = intent.getStringExtra("ADDRESS_CITY").orEmpty()

        val address = listOf(addressName, addressStreet, addressCity)
            .filter { it.isNotBlank() }
            .joinToString(", ")
            .ifBlank { "Alamat pengiriman tidak tersedia" }

        val orderId = "ORC-${System.currentTimeMillis()}"
        val date = SimpleDateFormat(
            "dd MMM yyyy, HH:mm",
            Locale("id", "ID")
        ).format(Date())

        val order = JSONObject().apply {
            put("id", orderId)
            put("date", date)
            put("paymentMethod", paymentMethod)
            put("total", total)
            put("status", "Order Placed")
            put("items", itemLines.joinToString("\n").ifBlank {
                "Detail produk tidak tersedia"
            })
            put("address", address)
        }

        orders.put(order)
        prefs.edit().putString(KEY_ORDERS, orders.toString()).apply()

        selectedIds.forEach { id ->
            CartManager.removeProduct(id)
        }

        return orderId
    }

    fun getOrders(context: Context): List<OrderHistoryItem> {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val json = JSONArray(prefs.getString(KEY_ORDERS, "[]"))
        val result = mutableListOf<OrderHistoryItem>()

        for (i in json.length() - 1 downTo 0) {
            val order = json.optJSONObject(i) ?: continue

            result.add(
                OrderHistoryItem(
                    id = order.optString("id"),
                    date = order.optString("date"),
                    paymentMethod = order.optString("paymentMethod"),
                    total = order.optInt("total"),
                    status = order.optString("status"),
                    items = order.optString("items"),
                    address = order.optString("address")
                )
            )
        }

        return result
    }
}