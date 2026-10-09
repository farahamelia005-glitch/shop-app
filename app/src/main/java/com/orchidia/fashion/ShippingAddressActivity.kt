
package com.orchidia.fashion

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class ShippingAddressActivity : AppCompatActivity() {

    private lateinit var etFullName: EditText
    private lateinit var etPhone: EditText
    private lateinit var etAddress: EditText
    private lateinit var etCity: EditText
    private lateinit var etPostalCode: EditText
    private lateinit var etProvince: EditText
    private lateinit var rgShipping: RadioGroup

    private var selectedProducts = arrayListOf<String>()

    companion object {
        const val REGULAR = "REGULAR"
        const val EXPRESS = "EXPRESS"

        const val REGULAR_COST = 15000
        const val EXPRESS_COST = 30000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipping_address)

        selectedProducts =
            intent.getStringArrayListExtra("SELECTED_PRODUCTS")
                ?: arrayListOf()

        etFullName = findViewById(R.id.etFullName)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        etCity = findViewById(R.id.etCity)
        etPostalCode = findViewById(R.id.etPostalCode)
        etProvince = findViewById(R.id.etProvince)
        rgShipping = findViewById(R.id.rgShipping)

        val prefs = getSharedPreferences("orchidia_address", MODE_PRIVATE)

        etFullName.setText(prefs.getString("name", ""))
        etPhone.setText(prefs.getString("phone", ""))
        etAddress.setText(prefs.getString("address", ""))
        etCity.setText(prefs.getString("city", ""))
        etPostalCode.setText(prefs.getString("postal", ""))
        etProvince.setText(prefs.getString("province", ""))

        val savedShipping = prefs.getString("shipping_method", REGULAR)
        rgShipping.check(
            if (savedShipping == EXPRESS) R.id.rbExpress
            else R.id.rbRegular
        )

        findViewById<TextView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.btnContinue).setOnClickListener {
            continueToCheckout()
        }
    }

    private fun continueToCheckout() {
        val name = etFullName.text.toString().trim()
        val phone = etPhone.text.toString().trim()
        val address = etAddress.text.toString().trim()
        val city = etCity.text.toString().trim()
        val postal = etPostalCode.text.toString().trim()
        val province = etProvince.text.toString().trim()

        if (
            name.isBlank() || phone.isBlank() ||
            address.isBlank() || city.isBlank() ||
            postal.isBlank() || province.isBlank()
        ) {
            Toast.makeText(
                this,
                "Lengkapi semua data alamat terlebih dahulu",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (selectedProducts.isEmpty()) {
            Toast.makeText(
                this,
                "Produk checkout tidak ditemukan. Pilih produk dari Bag lagi.",
                Toast.LENGTH_LONG
            ).show()
            return
        }

        val shippingMethod = when (rgShipping.checkedRadioButtonId) {
            R.id.rbRegular -> REGULAR
            R.id.rbExpress -> EXPRESS
            else -> {
                Toast.makeText(
                    this,
                    "Pilih metode pengiriman",
                    Toast.LENGTH_SHORT
                ).show()
                return
            }
        }

        val shippingCost = if (shippingMethod == EXPRESS) {
            EXPRESS_COST
        } else {
            REGULAR_COST
        }

        getSharedPreferences("orchidia_address", MODE_PRIVATE)
            .edit()
            .putString("name", name)
            .putString("phone", phone)
            .putString("address", address)
            .putString("city", city)
            .putString("postal", postal)
            .putString("province", province)
            .putString("shipping_method", shippingMethod)
            .apply()

        val checkoutIntent = Intent(
            this,
            CheckoutActivity::class.java
        ).apply {
            putStringArrayListExtra("SELECTED_PRODUCTS", selectedProducts)
            putExtra("ADDRESS_NAME", name)
            putExtra("ADDRESS_PHONE", phone)
            putExtra("ADDRESS_STREET", address)
            putExtra("ADDRESS_CITY", city)
            putExtra("ADDRESS_POSTAL", postal)
            putExtra("ADDRESS_PROVINCE", province)
            putExtra("SHIPPING_METHOD", shippingMethod)
            putExtra("ORDER_SHIPPING", shippingCost)
        }

        startActivity(checkoutIntent)
    }
}