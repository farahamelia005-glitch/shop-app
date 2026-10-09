package com.orchidia.fashion

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.orchidia.fashion.data.remote.SupabaseCartRepository
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class ProductDetailActivity : AppCompatActivity() {

    private var productId: String? = null
    private var currentProduct: Product? = null
    private var selectedSize: String? = null
    private var isAddingToBag = false

    private lateinit var btnWishlist: TextView

    private val pink = Color.parseColor("#FF5DA2")
    private val white = Color.parseColor("#FDFDFB")
    private val dark = Color.parseColor("#2C2F30")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product_detail)

        productId = intent.getStringExtra("PRODUCT_ID")
        currentProduct = productId?.let {
            ProductRepository.getById(it)
        }

        if (currentProduct == null) {
            Toast.makeText(
                this,
                "Product tidak ditemukan",
                Toast.LENGTH_SHORT
            ).show()
            finish()
            return
        }

        btnWishlist = findViewById(R.id.btnWishlist)

        displayProduct()
        setupSizeButtons()
        setupButtons()
        updateWishlistIcon()
    }

    private fun displayProduct() {
        val product = currentProduct ?: return

        findViewById<ImageView>(R.id.imgProduct)
            .setImageResource(product.imageRes)

        findViewById<TextView>(R.id.tvProductName)
            .text = product.name

        findViewById<TextView>(R.id.tvProductPrice)
            .text = formatRupiah(product.price)

        findViewById<TextView>(R.id.tvProductCategory)
            .text = product.category
    }

    private fun setupSizeButtons() {
        val sizeButtons = listOf(
            R.id.sizeS to "S",
            R.id.sizeM to "M",
            R.id.sizeL to "L"
        )

        fun updateSizeAppearance() {
            sizeButtons.forEach { (id, size) ->
                val button = findViewById<TextView>(id)
                val selected = size == selectedSize

                button.setBackgroundColor(
                    if (selected) Color.parseColor("#F02A87")
                    else Color.WHITE
                )

                button.setTextColor(
                    if (selected) Color.WHITE
                    else Color.parseColor("#555555")
                )
            }
        }

        sizeButtons.forEach { (id, size) ->
            findViewById<View>(id).setOnClickListener {
                selectedSize = size
                updateSizeAppearance()
            }
        }

        findViewById<View>(R.id.tvSizeGuide).setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Orchidia Size Guide ♡")
                .setMessage(
                    "S — Small\n\nM — Medium\n\nL — Large\n\n" +
                            "Pilih ukuran yang paling nyaman untukmu."
                )
                .setPositiveButton("Got it", null)
                .show()
        }

        updateSizeAppearance()
    }

    private fun setupButtons() {
        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        btnWishlist.setOnClickListener {
            val product = currentProduct ?: return@setOnClickListener

            WishlistManager.toggleProduct(product.id)
            updateWishlistIcon()

            val message =
                if (WishlistManager.isWishlisted(product.id)) {
                    "${product.name} added to wishlist ♡"
                } else {
                    "${product.name} removed from wishlist"
                }

            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        findViewById<View>(R.id.btnAddToBag)?.setOnClickListener {
            addProductToBag(goToCart = false)
        }

        findViewById<View>(R.id.btnBuyNow)?.setOnClickListener {
            addProductToBag(goToCart = true)
        }
    }

    private fun addProductToBag(goToCart: Boolean) {
        val product = currentProduct ?: return

        if (!validateSize() || isAddingToBag) return

        isAddingToBag = true

        lifecycleScope.launch {
            try {
                val success = SupabaseCartRepository.add(
                    this@ProductDetailActivity,
                    product.id
                )

                if (success) {
                    // Perbarui keranjang lokal setelah server berhasil.
                    CartManager.addProduct(product.id)

                    val sizeMessage =
                        selectedSize?.let { " (Size $it)" } ?: ""

                    if (goToCart) {
                        startActivity(
                            Intent(
                                this@ProductDetailActivity,
                                CartActivity::class.java
                            )
                        )
                    } else {
                        Toast.makeText(
                            this@ProductDetailActivity,
                            "${product.name}$sizeMessage added to bag",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Toast.makeText(
                        this@ProductDetailActivity,
                        "Gagal menyimpan ke keranjang. Coba lagi.",
                        Toast.LENGTH_LONG
                    ).show()
                }
            } finally {
                isAddingToBag = false
            }
        }
    }

    private fun validateSize(): Boolean {
        val product = currentProduct ?: return false

        if (product.category.equals("BAG", ignoreCase = true)) {
            return true
        }

        if (selectedSize == null) {
            Toast.makeText(
                this,
                "Please select a size first ♡",
                Toast.LENGTH_SHORT
            ).show()
            return false
        }

        return true
    }

    private fun updateWishlistIcon() {
        val product = currentProduct ?: return
        btnWishlist.text =
            if (WishlistManager.isWishlisted(product.id)) "♥" else "♡"
    }

    private fun formatRupiah(value: Int): String {
        return NumberFormat
            .getNumberInstance(Locale("id", "ID"))
            .format(value)
            .let { "Rp $it" }
    }
}