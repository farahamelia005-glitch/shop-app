package com.orchidia.fashion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class WishlistActivity : AppCompatActivity() {

    private lateinit var adapter: ProductAdapter
    private lateinit var tvEmpty: TextView
    private lateinit var tvCount: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wishlist)

        val rvWishlist = findViewById<RecyclerView>(R.id.rvWishlist)

        tvEmpty = findViewById(R.id.tvEmpty)
        tvCount = findViewById(R.id.tvCount)

        adapter = ProductAdapter(emptyList()) { product ->

            val intent = Intent(
                this,
                ProductDetailActivity::class.java
            )

            intent.putExtra(
                "PRODUCT_ID",
                product.id
            )

            startActivity(intent)
        }

        rvWishlist.layoutManager =
            GridLayoutManager(this, 2)

        rvWishlist.adapter = adapter

        setupButtons()
    }

    override fun onResume() {
        super.onResume()
        loadWishlist()
    }

    private fun loadWishlist() {

        val products = WishlistManager.getWishlistProducts()

        adapter.updateProducts(products)

        tvCount.text = "${products.size} items"

        if (products.isEmpty()) {

            tvEmpty.visibility = View.VISIBLE

        } else {

            tvEmpty.visibility = View.GONE
        }
    }


    private fun setupButtons() {
        findViewById<View>(R.id.btnBack)?.setOnClickListener {
            finish()
        }

        // Tombol header bersifat opsional jika belum ada di XML.
        findViewById<View>(R.id.btnSearch)?.setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        findViewById<View>(R.id.btnBag)?.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        findViewById<View>(R.id.navHome)?.setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.navShop)?.setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
        }

        // Wishlist adalah halaman yang sedang dibuka.
        findViewById<View>(R.id.navWishlist)?.setOnClickListener {
            // Tidak perlu membuka Activity lagi.
        }

        findViewById<View>(R.id.navBag)?.setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        findViewById<View>(R.id.navAccount)?.setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
    }
}