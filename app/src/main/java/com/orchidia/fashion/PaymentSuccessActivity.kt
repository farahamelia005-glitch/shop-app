package com.orchidia.fashion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.NumberFormat
import java.util.Locale

class PaymentSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_success)

        val prefs = getSharedPreferences("orchidia_address", MODE_PRIVATE)

        fun addressValue(extraKey: String, prefKey: String): String {
            return intent.getStringExtra(extraKey)
                ?.takeIf { it.isNotBlank() }
                ?: prefs.getString(prefKey, "").orEmpty()
        }

        val name = addressValue("ADDRESS_NAME", "name")
        val phone = addressValue("ADDRESS_PHONE", "phone")
        val street = addressValue("ADDRESS_STREET", "address")
        val city = addressValue("ADDRESS_CITY", "city")
        val province = addressValue("ADDRESS_PROVINCE", "province")
        val postal = addressValue("ADDRESS_POSTAL", "postal")

        val orderId = intent.getStringExtra("ORDER_ID")
            ?.takeIf { it.isNotBlank() }
            ?: "Pesanan berhasil dibuat"

        val total = intent.getIntExtra("ORDER_TOTAL", -1)

        findViewById<TextView>(R.id.tvSuccessOrderNumber).text = orderId

        findViewById<TextView>(R.id.tvSuccessTotal).text =
            if (total >= 0) formatRupiah(total) else "Lihat detail pesanan"

        findViewById<TextView>(R.id.tvSuccessAddressName).text =
            if (phone.isNotBlank()) "$name • $phone" else name.ifBlank { "Alamat belum tersedia" }

        val fullAddress = listOf(
            street,
            listOf(city, province).filter { it.isNotBlank() }.joinToString(", "),
            postal
        ).filter { it.isNotBlank() }.joinToString("\n")

        findViewById<TextView>(R.id.tvSuccessAddress).text =
            fullAddress.ifBlank { "Alamat pengiriman belum tersedia" }

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnContinueShopping).setOnClickListener {
            val homeIntent = Intent(this, HomeActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or
                        Intent.FLAG_ACTIVITY_SINGLE_TOP
            }
            startActivity(homeIntent)
            finish()
        }

        findViewById<View>(R.id.btnViewOrder).setOnClickListener {
            startActivity(Intent(this, OrderHistoryActivity::class.java))
            finish()
        }
    }

    private fun formatRupiah(value: Int): String =
        NumberFormat.getNumberInstance(Locale("id", "ID"))
            .format(value).let { "Rp $it" }
}