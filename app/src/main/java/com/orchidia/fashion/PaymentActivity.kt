package com.orchidia.fashion

import android.content.Intent
import android.os.Bundle
import android.os.CountDownTimer
import android.provider.Settings
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.orchidia.fashion.data.remote.OrderItemRequest
import com.orchidia.fashion.data.remote.OrderRequest
import com.orchidia.fashion.data.remote.SupabaseApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Locale

class PaymentActivity : AppCompatActivity() {

    private var selectedPayment = ""
    private var processing = false
    private var countdownTimer: CountDownTimer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

        val totalPayment = intent.getIntExtra("ORDER_TOTAL", 0)

        findViewById<TextView>(R.id.tvPaymentTotal).text =
            "Rp%,d".format(Locale("id", "ID"), totalPayment)

        findViewById<Button>(R.id.btnPay).text =
            "PAY NOW • Rp %,d".format(Locale("id", "ID"), totalPayment)

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<LinearLayout>(R.id.paymentQris).setOnClickListener {
            selectedPayment = "QRIS"
            showSelected("QRIS")
        }

        findViewById<LinearLayout>(R.id.paymentWallet).setOnClickListener {
            selectedPayment = "E-Wallet"
            showSelected("E-Wallet")
        }

        findViewById<LinearLayout>(R.id.paymentBank).setOnClickListener {
            selectedPayment = "Bank Transfer"
            showSelected("Bank Transfer")
        }

        findViewById<LinearLayout>(R.id.paymentCod).setOnClickListener {
            selectedPayment = "COD"
            showSelected("COD")
        }

        findViewById<Button>(R.id.btnPay).setOnClickListener {
            if (processing) return@setOnClickListener

            if (selectedPayment.isEmpty()) {
                Toast.makeText(
                    this,
                    "Pilih metode pembayaran terlebih dahulu",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            if (selectedPayment == "Bank Transfer") {
                showVirtualAccount()
            } else {
                completePayment()
            }
        }
    }

    private fun showSelected(method: String) {
        Toast.makeText(
            this,
            "Metode pembayaran: $method",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun showVirtualAccount() {
        val total = intent.getIntExtra("ORDER_TOTAL", 0)
        val dialogView = layoutInflater.inflate(
            R.layout.dialog_virtual_account,
            null
        )

        val dialog = android.app.AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialogView.findViewById<TextView>(R.id.tvVAAmount).text =
            "Rp%,d".format(Locale("id", "ID"), total)

        dialogView.findViewById<TextView>(R.id.tvVACountdown).text = "15:00"

        dialogView.findViewById<View>(R.id.btnCheckPayment).setOnClickListener {
            countdownTimer?.cancel()
            dialog.dismiss()
            completePayment()
        }

        dialogView.findViewById<View>(R.id.btnCloseVA).setOnClickListener {
            countdownTimer?.cancel()
            dialog.dismiss()
        }

        dialog.show()
        dialog.window?.setBackgroundDrawableResource(
            android.R.color.transparent
        )

        countdownTimer = object : CountDownTimer(15 * 60 * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                val minutes = millisUntilFinished / 60000
                val seconds = (millisUntilFinished % 60000) / 1000

                dialogView.findViewById<TextView>(R.id.tvVACountdown).text =
                    String.format(
                        Locale.getDefault(),
                        "%02d:%02d",
                        minutes,
                        seconds
                    )
            }

            override fun onFinish() {
                if (!isFinishing && dialog.isShowing) {
                    Toast.makeText(
                        this@PaymentActivity,
                        "Waktu pembayaran habis",
                        Toast.LENGTH_LONG
                    ).show()
                    dialog.dismiss()
                }
            }
        }.start()
    }

    private fun completePayment() {
        if (processing) return
        processing = true

        val payButton = findViewById<Button>(R.id.btnPay)
        payButton.isEnabled = false
        payButton.text = "MENYIMPAN PESANAN..."

        // Snapshot keranjang sebelum OrderManager mengosongkannya.
        val cartSnapshot = CartManager.getItems()
        val selectedIds = intent
            .getStringArrayListExtra("SELECTED_PRODUCTS")
            ?.toSet()
            ?: cartSnapshot.keys

        val selectedItems = cartSnapshot.filterKeys { it in selectedIds }

        if (selectedItems.isEmpty()) {
            processing = false
            payButton.isEnabled = true
            payButton.text = "PAY NOW"
            Toast.makeText(
                this,
                "Keranjang kosong. Silakan kembali ke keranjang.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        lifecycleScope.launch {
            try {
                val orderId = "ORC-${System.currentTimeMillis()}"
                val total = intent.getIntExtra("ORDER_TOTAL", 0)
                val shipping = intent.getIntExtra("ORDER_SHIPPING", 15000)
                val subtotal = intent.getIntExtra(
                    "ORDER_SUBTOTAL",
                    total - shipping
                )

                val deviceSession = Settings.Secure.getString(
                    contentResolver,
                    Settings.Secure.ANDROID_ID
                ) ?: "orchidia-demo"

                val addressName =
                    intent.getStringExtra("ADDRESS_NAME").orEmpty()
                val addressPhone =
                    intent.getStringExtra("ADDRESS_PHONE").orEmpty()
                val addressStreet =
                    intent.getStringExtra("ADDRESS_STREET").orEmpty()
                val addressCity =
                    intent.getStringExtra("ADDRESS_CITY").orEmpty()
                val addressPostal =
                    intent.getStringExtra("ADDRESS_POSTAL").orEmpty()
                val addressProvince =
                    intent.getStringExtra("ADDRESS_PROVINCE").orEmpty()
                val shippingMethod =
                    intent.getStringExtra("SHIPPING_METHOD") ?: "REGULAR"

                val products = selectedItems.mapNotNull { (productId, quantity) ->
                    val product = ProductRepository.getById(productId)
                        ?: return@mapNotNull null

                    OrderItemRequest(
                        orderId = orderId,
                        productId = product.id,
                        productName = product.name,
                        price = product.price,
                        quantity = quantity
                    )
                }

                if (products.isEmpty()) {
                    throw IllegalStateException(
                        "Detail produk tidak ditemukan."
                    )
                }

                val order = OrderRequest(
                    id = orderId,
                    sessionId = deviceSession,
                    customerName = addressName,
                    customerPhone = addressPhone,
                    addressStreet = addressStreet,
                    addressCity = addressCity,
                    addressPostal = addressPostal,
                    addressProvince = addressProvince,
                    shippingMethod = shippingMethod,
                    subtotal = subtotal,
                    shippingCost = shipping,
                    total = total,
                    paymentMethod = selectedPayment,
                    status = "Paid"
                )

                withContext(Dispatchers.IO) {
                    val orderResponse = SupabaseApi.orderService
                        .insertOrder(order)

                    if (!orderResponse.isSuccessful) {
                        throw IllegalStateException(
                            "Gagal menyimpan pesanan: HTTP ${orderResponse.code()}"
                        )
                    }

                    val itemsResponse = SupabaseApi.orderService
                        .insertOrderItems(products)

                    if (!itemsResponse.isSuccessful) {
                        throw IllegalStateException(
                            "Gagal menyimpan detail item: HTTP ${itemsResponse.code()}"
                        )
                    }
                }

                // Simpan juga ke riwayat lokal yang sudah ada.
                OrderManager.saveOrder(this@PaymentActivity, intent, selectedPayment)

                val successIntent = Intent(
                    this@PaymentActivity,
                    PaymentSuccessActivity::class.java
                ).apply {
                    putExtra("ORDER_ID", orderId)
                    putExtra("ORDER_TOTAL", total)
                    putExtra("ADDRESS_NAME", addressName)
                    putExtra("ADDRESS_PHONE", addressPhone)
                    putExtra("ADDRESS_STREET", addressStreet)
                    putExtra("ADDRESS_CITY", addressCity)
                    putExtra("ADDRESS_POSTAL", addressPostal)
                    putExtra("ADDRESS_PROVINCE", addressProvince)
                }

                startActivity(successIntent)
                finish()

            } catch (e: Exception) {
                processing = false
                payButton.isEnabled = true
                payButton.text = "PAY NOW • Rp %,d".format(
                    Locale("id", "ID"),
                    intent.getIntExtra("ORDER_TOTAL", 0)
                )

                Toast.makeText(
                    this@PaymentActivity,
                    "Gagal menyimpan pesanan: ${e.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    override fun onDestroy() {
        countdownTimer?.cancel()
        super.onDestroy()
    }
}