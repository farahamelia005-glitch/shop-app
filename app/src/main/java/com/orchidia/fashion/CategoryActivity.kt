
package com.orchidia.fashion

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.orchidia.fashion.data.remote.SupabaseApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoryActivity : AppCompatActivity() {

    private lateinit var adapter: ProductAdapter
    private lateinit var tvCategoryName: TextView
    private lateinit var tvProductCount: TextView

    private var allProducts: List<Product> =
        ProductRepository.getAll()

    private var selectedCategory: String = "ALL"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        tvCategoryName = findViewById(R.id.tvCategoryName)
        tvProductCount = findViewById(R.id.tvProductCount)

        val rvProducts =
            findViewById<RecyclerView>(R.id.rvProducts)

        adapter = ProductAdapter(emptyList()) { product ->
            openProduct(product.id)
        }

        rvProducts.layoutManager = GridLayoutManager(this, 2)
        rvProducts.adapter = adapter

        setupHeader()
        setupCategories()
        setupBottomNavigation()

        selectedCategory =
            intent.getStringExtra("CATEGORY") ?: "ALL"

        showCategory(selectedCategory)
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
                        val mergedProducts = ProductRepository.getAll().map { local ->
                            matchedProducts.find { remote ->
                                remote.id.equals(local.id, ignoreCase = true)
                            } ?: local
                        }

                        allProducts = mergedProducts
                        showCategory(selectedCategory)
                    } else {
                        Log.w(
                            "ORCHIDIA_API",
                            "Produk Supabase belum cocok dengan ID produk lokal"
                        )
                    }
                } else {
                    Log.e(
                        "ORCHIDIA_API",
                        "HTTP ${response.code()}: ${
                            response.errorBody()?.string()
                        }"
                    )
                }
            } catch (e: Exception) {
                Log.e(
                    "ORCHIDIA_API",
                    "Gagal mengambil produk: ${e.message}",
                    e
                )
            }
        }
    }

    private fun setupHeader() {
        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnSearch).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        findViewById<View>(R.id.btnBag).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }
    }

    private fun setupCategories() {
        findViewById<View>(R.id.categoryAll).setOnClickListener {
            showCategory("ALL")
        }

        findViewById<View>(R.id.categoryTop).setOnClickListener {
            showCategory("TOP")
        }

        findViewById<View>(R.id.categoryPants).setOnClickListener {
            showCategory("PANTS")
        }

        findViewById<View>(R.id.categoryBag).setOnClickListener {
            showCategory("BAG")
        }

        findViewById<View>(R.id.categoryDress).setOnClickListener {
            showCategory("DRESS")
        }

        findViewById<View>(R.id.categorySkirt).setOnClickListener {
            showCategory("SKIRT")
        }
    }

    private fun showCategory(category: String) {
        selectedCategory = category

        val products =
            if (category.equals("ALL", ignoreCase = true)) {
                allProducts
            } else {
                allProducts.filter {
                    it.category.equals(category, ignoreCase = true)
                }
            }

        adapter.updateProducts(products)

        tvCategoryName.text =
            if (category == "ALL") "All Products" else category

        tvProductCount.text = "${products.size} items"

        updateCategoryButton(category)
    }

    private fun updateCategoryButton(selected: String) {
        val categories = listOf(
            R.id.categoryAll to "ALL",
            R.id.categoryTop to "TOP",
            R.id.categoryPants to "PANTS",
            R.id.categoryBag to "BAG",
            R.id.categoryDress to "DRESS",
            R.id.categorySkirt to "SKIRT"
        )

        categories.forEach { (viewId, category) ->
            val view = findViewById<TextView>(viewId)

            if (category == selected) {
                view.setBackgroundResource(
                    R.drawable.bg_category_selected
                )
                view.setTextColor(android.graphics.Color.WHITE)
            } else {
                view.setBackgroundResource(
                    R.drawable.bg_category_unselected
                )
                view.setTextColor(
                    android.graphics.Color.rgb(31, 31, 31)
                )
            }
        }
    }

    private fun setupBottomNavigation() {
        findViewById<View>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
            finish()
        }

        findViewById<View>(R.id.navShop).setOnClickListener {
            showCategory("ALL")
        }

        findViewById<View>(R.id.navWishlist).setOnClickListener {
            startActivity(Intent(this, WishlistActivity::class.java))
        }

        findViewById<View>(R.id.navBag).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        findViewById<View>(R.id.navAccount).setOnClickListener {
            startActivity(Intent(this, AccountActivity::class.java))
        }
    }

    private fun openProduct(productId: String) {
        val intent = Intent(this, ProductDetailActivity::class.java)
        intent.putExtra("PRODUCT_ID", productId)
        startActivity(intent)
    }
}