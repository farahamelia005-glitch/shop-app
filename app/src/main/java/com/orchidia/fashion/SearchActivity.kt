
package com.orchidia.fashion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orchidia.fashion.data.remote.SupabaseApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SearchActivity : AppCompatActivity() {

    private lateinit var adapter: ProductAdapter
    private lateinit var etSearch: EditText
    private lateinit var tvSearchTitle: TextView
    private lateinit var tvProductCount: TextView
    private lateinit var tvEmpty: TextView

    private var allProducts: List<Product> =
        ProductRepository.getAll()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        etSearch = findViewById(R.id.etSearch)
        tvSearchTitle = findViewById(R.id.tvSearchTitle)
        tvProductCount = findViewById(R.id.tvProductCount)
        tvEmpty = findViewById(R.id.tvEmpty)

        val rvProducts =
            findViewById<RecyclerView>(R.id.rvProducts)

        adapter = ProductAdapter(emptyList()) { product ->
            val intent = Intent(
                this,
                ProductDetailActivity::class.java
            )
            intent.putExtra("PRODUCT_ID", product.id)
            startActivity(intent)
        }

        rvProducts.layoutManager = GridLayoutManager(this, 2)
        rvProducts.adapter = adapter

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnBag).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        etSearch.addTextChangedListener(
            object : android.text.TextWatcher {

                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {}

                override fun onTextChanged(
                    s: CharSequence?,
                    start: Int,
                    before: Int,
                    count: Int
                ) {
                    searchProducts(s.toString())
                }

                override fun afterTextChanged(
                    s: android.text.Editable?
                ) {}
            }
        )

        searchProducts("")
        loadProductsFromSupabase()
    }

    private fun loadProductsFromSupabase() {
        lifecycleScope.launch {
            try {
                val response = withContext(Dispatchers.IO) {
                    SupabaseApi.service.getProducts()
                }

                if (response.isSuccessful) {
                    val remoteProducts = response.body().orEmpty()
                    val matchedProducts =
                        ProductRepository.fromSupabase(remoteProducts)

                    if (matchedProducts.isNotEmpty()) {
                        allProducts = matchedProducts
                        searchProducts(etSearch.text.toString())
                    } else {
                        Log.w(
                            "ORCHIDIA_SEARCH",
                            "Tidak ada produk Supabase yang cocok dengan produk lokal"
                        )
                    }
                } else {
                    Log.e(
                        "ORCHIDIA_SEARCH",
                        "HTTP ${response.code()}: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    "ORCHIDIA_SEARCH",
                    "Gagal memuat produk: ${e.message}",
                    e
                )
            }
        }
    }

    private fun searchProducts(keyword: String) {
        val products = if (keyword.isBlank()) {
            allProducts
        } else {
            allProducts.filter {
                it.name.contains(keyword, ignoreCase = true) ||
                        it.category.contains(keyword, ignoreCase = true)
            }
        }

        adapter.updateProducts(products)

        tvSearchTitle.text = if (keyword.isBlank()) {
            "Recommended For You"
        } else {
            "Search Results"
        }

        tvProductCount.text = "${products.size} products found"

        tvEmpty.visibility = if (products.isEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }
}