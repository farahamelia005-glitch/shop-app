package com.orchidia.fashion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orchidia.fashion.data.remote.CartItemRemote
import com.orchidia.fashion.data.remote.SupabaseCartRepository
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private val selectedIds = mutableSetOf<String>()

    private lateinit var adapter: CartAdapter
    private lateinit var tvSelectedItems: TextView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvEmptyBag: TextView
    private lateinit var layoutSummary: LinearLayout

    private var isSyncing = false
    private val shipping = 15000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        tvSelectedItems = findViewById(R.id.tvSelectedItems)
        tvSubtotal = findViewById(R.id.tvSubtotal)
        tvTotal = findViewById(R.id.tvTotal)
        tvEmptyBag = findViewById(R.id.tvEmptyBag)
        layoutSummary = findViewById(R.id.layoutSummary)

        val rvCart = findViewById<RecyclerView>(R.id.rvCart)

        adapter = CartAdapter(
            CartManager.getItems().keys.mapNotNull {
                ProductRepository.getById(it)
            },
            selectedIds,
            {
                updateTotal()
            },
            { product, quantity ->
                syncCartChange(product, quantity)
            }
        )

        rvCart.layoutManager = LinearLayoutManager(this)
        rvCart.adapter = adapter

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnCheckout).setOnClickListener {
            proceedToCheckout()
        }

        updateEmptyState()
        updateTotal()
    }

    override fun onResume() {
        super.onResume()
        loadCartFromSupabase()
    }

    private fun loadCartFromSupabase() {
        if (isSyncing) return
        isSyncing = true

        lifecycleScope.launch {
            try {
                val remoteItems = SupabaseCartRepository.getCart(this@CartActivity)

                if (remoteItems == null) {
                    Toast.makeText(
                        this@CartActivity,
                        "Gagal memuat keranjang dari server",
                        Toast.LENGTH_SHORT
                    ).show()
                    return@launch
                }

                applyRemoteCart(remoteItems)
            } catch (e: Exception) {
                Toast.makeText(
                    this@CartActivity,
                    "Terjadi kesalahan saat memuat keranjang",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                isSyncing = false
            }
        }
    }

    private fun applyRemoteCart(remoteItems: List<CartItemRemote>) {
        val validItems = remoteItems.mapNotNull { item ->
            val productExists =
                ProductRepository.getById(item.productId) != null

            if (productExists && item.quantity > 0) {
                item.productId to item.quantity
            } else {
                null
            }
        }.toMap()

        CartManager.replaceItems(validItems)
        selectedIds.retainAll(validItems.keys)

        adapter.refresh()
        updateEmptyState()
        updateTotal()
    }

    private fun syncCartChange(product: Product, quantity: Int) {
        lifecycleScope.launch {
            val success = if (quantity <= 0) {
                SupabaseCartRepository.remove(
                    this@CartActivity,
                    product.id
                )
            } else {
                SupabaseCartRepository.update(
                    this@CartActivity,
                    product.id,
                    quantity
                )
            }

            if (!success) {
                Toast.makeText(
                    this@CartActivity,
                    "Perubahan belum tersimpan. Memuat ulang keranjang...",
                    Toast.LENGTH_SHORT
                ).show()

                loadCartFromSupabase()
            }
        }
    }

    private fun updateTotal() {
        val subtotal = CartManager.calculateTotal(selectedIds)

        val selectedCount = selectedIds.sumOf {
            CartManager.getQuantity(it)
        }

        tvSelectedItems.text = "Selected Items: $selectedCount"
        tvSubtotal.text = formatRupiah(subtotal)

        val total = if (selectedCount > 0) {
            subtotal + shipping
        } else {
            0
        }

        tvTotal.text = formatRupiah(total)
    }

    private fun updateEmptyState() {
        val isEmpty = CartManager.getItems().isEmpty()

        tvEmptyBag.visibility =
            if (isEmpty) View.VISIBLE else View.GONE

        layoutSummary.visibility =
            if (isEmpty) View.GONE else View.VISIBLE
    }

    private fun proceedToCheckout() {
        if (selectedIds.isEmpty()) {
            Toast.makeText(
                this,
                "Pilih produk yang ingin checkout terlebih dahulu",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val validIds = selectedIds.filter { id ->
            CartManager.getQuantity(id) > 0 &&
                    ProductRepository.getById(id) != null
        }

        if (validIds.isEmpty()) {
            Toast.makeText(
                this,
                "Produk terpilih tidak tersedia",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val intent = Intent(this, ShippingAddressActivity::class.java)
        intent.putStringArrayListExtra(
            "SELECTED_PRODUCTS",
            ArrayList(validIds)
        )
        startActivity(intent)
    }

    private fun formatRupiah(value: Int): String {
        return NumberFormat.getNumberInstance(Locale("id", "ID"))
            .format(value)
            .let { "Rp $it" }
    }
}