
package com.orchidia.fashion

import android.content.Intent
import android.os.Bundle
import android.view.Gravity
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale

class CheckoutActivity : AppCompatActivity() {

    private lateinit var selectedIds: ArrayList<String>
    private var subtotal = 0
    private val shipping = 15000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        selectedIds = intent.getStringArrayListExtra("SELECTED_PRODUCTS")
            ?: arrayListOf()

        if (selectedIds.isEmpty()) {
            Toast.makeText(this, "Data produk checkout tidak ditemukan", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        showAddress()
        showProducts()
        showSummary()

        findViewById<Button>(R.id.btnPayment).setOnClickListener {
            val intent = Intent(this, PaymentActivity::class.java).apply {
                putStringArrayListExtra("SELECTED_PRODUCTS", selectedIds)
                putExtra("ORDER_SUBTOTAL", subtotal)
                putExtra("ORDER_SHIPPING", shipping)
                putExtra("ORDER_TOTAL", subtotal + shipping)
                putExtra("ADDRESS_NAME", getAddress("name"))
                putExtra("ADDRESS_PHONE", getAddress("phone"))
                putExtra("ADDRESS_STREET", getAddress("address"))
                putExtra("ADDRESS_CITY", getAddress("city"))
                putExtra("ADDRESS_POSTAL", getAddress("postal"))
                putExtra("ADDRESS_PROVINCE", getAddress("province"))
            }
            startActivity(intent)
        }
    }

    private fun getAddress(key: String): String {
        return getSharedPreferences("orchidia_address", MODE_PRIVATE)
            .getString(key, "") ?: ""
    }

    private fun showAddress() {
        findViewById<TextView>(R.id.tvAddressName).text =
            "${getAddress("name")} • ${getAddress("phone")}"

        findViewById<TextView>(R.id.tvAddressStreet).text =
            getAddress("address")

        findViewById<TextView>(R.id.tvAddressCity).text =
            "${getAddress("city")}, ${getAddress("province")} ${getAddress("postal")}"
    }

    private fun showProducts() {
        val container = findViewById<LinearLayout>(R.id.layoutCheckoutItems)
        container.removeAllViews()
        subtotal = 0

        selectedIds.forEach { id ->
            val product = ProductRepository.getById(id) ?: return@forEach
            val quantity = CartManager.getQuantity(id)
            if (quantity <= 0) return@forEach

            subtotal += product.price * quantity

            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(0, 12, 0, 12)
            }

            val image = ImageView(this).apply {
                setImageResource(product.imageRes)
                scaleType = ImageView.ScaleType.CENTER_CROP
                contentDescription = product.name
            }
            row.addView(image, LinearLayout.LayoutParams(dp(76), dp(88)))

            val details = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(dp(12), 0, 0, 0)
            }

            details.addView(TextView(this).apply {
                text = product.name
                textSize = 14f
                setTextColor(android.graphics.Color.rgb(31, 31, 31))
                setTypeface(null, android.graphics.Typeface.BOLD)
            })

            details.addView(TextView(this).apply {
                text = "${product.category} • Qty $quantity"
                textSize = 12f
                setTextColor(android.graphics.Color.GRAY)
            })

            details.addView(TextView(this).apply {
                text = "${formatRupiah(product.price)} × $quantity = ${formatRupiah(product.price * quantity)}"
                textSize = 12f
                setTextColor(android.graphics.Color.rgb(240, 42, 135))
            })

            row.addView(
                details,
                LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f)
            )
            container.addView(row)
        }

        if (container.childCount == 0) {
            Toast.makeText(this, "Produk di Bag sudah tidak tersedia", Toast.LENGTH_LONG).show()
            finish()
        }
    }

    private fun showSummary() {
        findViewById<TextView>(R.id.tvCheckoutSubtotal).text = formatRupiah(subtotal)
        findViewById<TextView>(R.id.tvCheckoutShipping).text = formatRupiah(shipping)
        findViewById<TextView>(R.id.tvCheckoutTotal).text = formatRupiah(subtotal + shipping)
    }

    private fun formatRupiah(value: Int): String =
        NumberFormat.getNumberInstance(Locale("id", "ID"))
            .format(value).let { "Rp $it" }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}