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
    private var shipping = REGULAR_COST
    private var shippingMethod = REGULAR

    companion object {
        const val REGULAR = "REGULAR"
        const val EXPRESS = "EXPRESS"
        const val REGULAR_COST = 15000
        const val EXPRESS_COST = 30000
        private const val ADDRESS_PREFS = "orchidia_address"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_checkout)

        selectedIds = intent.getStringArrayListExtra("SELECTED_PRODUCTS")
            ?: arrayListOf()

        if (selectedIds.isEmpty()) {
            Toast.makeText(
                this,
                "Data produk checkout tidak ditemukan",
                Toast.LENGTH_LONG
            ).show()
            finish()
            return
        }

        // Baca pilihan ongkir SEBELUM menampilkan ringkasan.
        val prefs = getSharedPreferences(ADDRESS_PREFS, MODE_PRIVATE)
        shippingMethod = intent.getStringExtra("SHIPPING_METHOD")
            ?: prefs.getString("shipping_method", REGULAR)
                    ?: REGULAR

        shipping = when (shippingMethod) {
            EXPRESS -> EXPRESS_COST
            else -> REGULAR_COST
        }

        // Jika halaman sebelumnya mengirim biaya ongkir yang valid,
        // gunakan nilai tersebut.
        val sentShipping = intent.getIntExtra("ORDER_SHIPPING", -1)
        if (sentShipping >= 0) {
            shipping = sentShipping
            shippingMethod = if (sentShipping == EXPRESS_COST) EXPRESS else REGULAR
        }

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        showAddress()
        showProducts()
        showSummary()

        findViewById<Button>(R.id.btnPayment).setOnClickListener {
            val paymentIntent = Intent(this, PaymentActivity::class.java).apply {
                putStringArrayListExtra("SELECTED_PRODUCTS", selectedIds)
                putExtra("ORDER_SUBTOTAL", subtotal)
                putExtra("ORDER_SHIPPING", shipping)
                putExtra("SHIPPING_METHOD", shippingMethod)
                putExtra("ORDER_TOTAL", subtotal + shipping)

                putExtra("ADDRESS_NAME", getAddress("name"))
                putExtra("ADDRESS_PHONE", getAddress("phone"))
                putExtra("ADDRESS_STREET", getAddress("address"))
                putExtra("ADDRESS_CITY", getAddress("city"))
                putExtra("ADDRESS_POSTAL", getAddress("postal"))
                putExtra("ADDRESS_PROVINCE", getAddress("province"))
            }
            startActivity(paymentIntent)
        }
    }

    private fun getAddress(key: String): String {
        return getSharedPreferences(ADDRESS_PREFS, MODE_PRIVATE)
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
                setPadding(0, dp(12), 0, dp(12))
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
                text = "${formatRupiah(product.price)} × $quantity = " +
                        formatRupiah(product.price * quantity)
                textSize = 12f
                setTextColor(android.graphics.Color.rgb(240, 42, 135))
            })

            row.addView(
                details,
                LinearLayout.LayoutParams(
                    0,
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    1f
                )
            )
            container.addView(row)
        }

        if (container.childCount == 0) {
            Toast.makeText(
                this,
                "Produk di Bag sudah tidak tersedia",
                Toast.LENGTH_LONG
            ).show()
            finish()
        }
    }

    private fun showSummary() {
        findViewById<TextView>(R.id.tvCheckoutSubtotal).text =
            formatRupiah(subtotal)

        findViewById<TextView>(R.id.tvCheckoutShipping).text =
            "${if (shippingMethod == EXPRESS) "Express" else "Regular"} • ${formatRupiah(shipping)}"

        findViewById<TextView>(R.id.tvCheckoutTotal).text =
            formatRupiah(subtotal + shipping)
    }

    private fun formatRupiah(value: Int): String =
        NumberFormat.getNumberInstance(Locale("id", "ID"))
            .format(value).let { "Rp $it" }

    private fun dp(value: Int): Int =
        (value * resources.displayMetrics.density).toInt()
}