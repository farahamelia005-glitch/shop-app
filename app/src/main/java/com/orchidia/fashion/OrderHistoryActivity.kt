package com.orchidia.fashion

import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale

class OrderHistoryActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()

        if (findViewById<View>(R.id.tvOrderList) != null) {
            displayOrders()
        }
    }

    private fun displayOrders() {
        val tvOrderList = findViewById<TextView>(R.id.tvOrderList)
        val orders = OrderManager.getOrders(this)

        if (orders.isEmpty()) {
            tvOrderList.text = "No orders yet ♡\nYour purchases will appear here."
            return
        }

        tvOrderList.text = orders.joinToString(
            separator = "\n\n──────────────────\n\n"
        ) { order ->
            val total = NumberFormat
                .getNumberInstance(Locale("id", "ID"))
                .format(order.total)

            """
            ORDER ${order.id}
            ${order.date}

            ${order.items}

            Total: Rp $total
            Payment: ${order.paymentMethod}
            Status: ${order.status}

            Ship to: ${order.address}
            """.trimIndent()
        }
    }
}