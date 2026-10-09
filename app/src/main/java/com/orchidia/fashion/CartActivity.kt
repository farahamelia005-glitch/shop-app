package com.orchidia.fashion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class CartActivity : AppCompatActivity() {

    private val selectedIds =
        mutableSetOf<String>()

    private lateinit var adapter: CartAdapter

    private lateinit var tvSelectedItems: TextView
    private lateinit var tvSubtotal: TextView
    private lateinit var tvTotal: TextView
    private lateinit var tvEmptyBag: TextView
    private lateinit var layoutSummary: LinearLayout

    private val shipping = 15000

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_cart
        )

        tvSelectedItems =
            findViewById(
                R.id.tvSelectedItems
            )

        tvSubtotal =
            findViewById(
                R.id.tvSubtotal
            )

        tvTotal =
            findViewById(
                R.id.tvTotal
            )

        tvEmptyBag =
            findViewById(
                R.id.tvEmptyBag
            )

        layoutSummary =
            findViewById(
                R.id.layoutSummary
            )

        val rvCart =
            findViewById<RecyclerView>(
                R.id.rvCart
            )

        adapter = CartAdapter(
            getCartProducts(),
            selectedIds,
            {
                updateTotal()
            },
            {
                updateEmptyState()
            }
        )

        rvCart.layoutManager =
            LinearLayoutManager(this)

        rvCart.adapter = adapter

        findViewById<View>(
            R.id.btnBack
        ).setOnClickListener {

            finish()
        }

        findViewById<View>(
            R.id.btnCheckout
        ).setOnClickListener {

            proceedToCheckout()
        }

        updateEmptyState()
        updateTotal()
    }

    override fun onResume() {
        super.onResume()

        adapter.refresh()

        updateEmptyState()
        updateTotal()
    }

    private fun getCartProducts(): List<Product> {

        return CartManager
            .getItems()
            .keys
            .mapNotNull {
                ProductRepository.getById(it)
            }
    }

    private fun updateTotal() {

        val subtotal =
            CartManager.calculateTotal(
                selectedIds
            )

        val selectedCount =
            selectedIds.sumOf {
                CartManager.getQuantity(it)
            }

        tvSelectedItems.text =
            "Selected Items: $selectedCount"

        tvSubtotal.text =
            formatRupiah(subtotal)

        val total =
            if (selectedCount > 0) {
                subtotal + shipping
            } else {
                0
            }

        tvTotal.text =
            formatRupiah(total)
    }

    private fun updateEmptyState() {

        val isEmpty =
            CartManager
                .getItems()
                .isEmpty()

        if (isEmpty) {

            tvEmptyBag.visibility =
                View.VISIBLE

            layoutSummary.visibility =
                View.GONE

        } else {

            tvEmptyBag.visibility =
                View.GONE

            layoutSummary.visibility =
                View.VISIBLE
        }
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

        val validIds = selectedIds.filter {
            CartManager.getQuantity(it) > 0 &&
                    ProductRepository.getById(it) != null
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

    private fun formatRupiah(
        value: Int
    ): String {

        return NumberFormat
            .getNumberInstance(
                Locale("id", "ID")
            )
            .format(value)
            .let {
                "Rp $it"
            }
    }
}