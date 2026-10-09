
package com.orchidia.fashion

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
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

    private var selectedProducts = arrayListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shipping_address)

        selectedProducts = intent
            .getStringArrayListExtra("SELECTED_PRODUCTS")
            ?: arrayListOf()

        etFullName = findViewById(R.id.etFullName)
        etPhone = findViewById(R.id.etPhone)
        etAddress = findViewById(R.id.etAddress)
        etCity = findViewById(R.id.etCity)
        etPostalCode = findViewById(R.id.etPostalCode)
        etProvince = findViewById(R.id.etProvince)

        // Isi kembali data jika pengguna kembali dari Checkout.
        val prefs = getSharedPreferences("orchidia_address", MODE_PRIVATE)
        etFullName.setText(prefs.getString("name", ""))
        etPhone.setText(prefs.getString("phone", ""))
        etAddress.setText(prefs.getString("address", ""))
        etCity.setText(prefs.getString("city", ""))
        etPostalCode.setText(prefs.getString("postal", ""))
        etProvince.setText(prefs.getString("province", ""))

        findViewById<TextView>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<TextView>(R.id.btnContinue).setOnClickListener {
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
                return@setOnClickListener
            }

            if (selectedProducts.isEmpty()) {
                Toast.makeText(
                    this,
                    "Produk checkout tidak ditemukan. Pilih produk dari Bag lagi.",
                    Toast.LENGTH_LONG
                ).show()
                return@setOnClickListener
            }

            // Simpan alamat agar tetap terisi saat halaman dibuka kembali.
            prefs.edit()
                .putString("name", name)
                .putString("phone", phone)
                .putString("address", address)
                .putString("city", city)
                .putString("postal", postal)
                .putString("province", province)
                .apply()

            val intent = Intent(this, CheckoutActivity::class.java).apply {
                putStringArrayListExtra("SELECTED_PRODUCTS", selectedProducts)
                putExtra("ADDRESS_NAME", name)
                putExtra("ADDRESS_PHONE", phone)
                putExtra("ADDRESS_STREET", address)
                putExtra("ADDRESS_CITY", city)
                putExtra("ADDRESS_POSTAL", postal)
                putExtra("ADDRESS_PROVINCE", province)
            }

            startActivity(intent)
        }
    }
}