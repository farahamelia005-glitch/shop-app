package com.orchidia.fashion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class PaymentActivity : AppCompatActivity() {

    private var selectedPayment = ""
    private var processing = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment)

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

        findViewById<View>(R.id.btnPay).setOnClickListener { button ->
            if (processing) return@setOnClickListener

            if (selectedPayment.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please select a payment method",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            processing = true
            button.isEnabled = false

            val orderId = OrderManager.saveOrder(
                this,
                intent,
                selectedPayment
            )

            val successIntent = Intent(
                this,
                PaymentSuccessActivity::class.java
            ).apply {
                putExtra("ORDER_ID", orderId)
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            }

            startActivity(successIntent)
            finish()
        }
    }

    private fun showSelected(method: String) {
        Toast.makeText(
            this,
            "$method selected",
            Toast.LENGTH_SHORT
        ).show()
    }
}