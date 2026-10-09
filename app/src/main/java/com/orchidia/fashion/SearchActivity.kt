package com.orchidia.fashion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class SearchActivity : AppCompatActivity() {

    private lateinit var adapter: ProductAdapter

    private lateinit var etSearch: EditText
    private lateinit var tvSearchTitle: TextView
    private lateinit var tvProductCount: TextView
    private lateinit var tvEmpty: TextView

    override fun onCreate(
        savedInstanceState: Bundle?
    ) {
        super.onCreate(savedInstanceState)

        setContentView(
            R.layout.activity_search
        )

        etSearch =
            findViewById(R.id.etSearch)

        tvSearchTitle =
            findViewById(R.id.tvSearchTitle)

        tvProductCount =
            findViewById(R.id.tvProductCount)

        tvEmpty =
            findViewById(R.id.tvEmpty)

        val rvProducts =
            findViewById<RecyclerView>(
                R.id.rvProducts
            )

        adapter = ProductAdapter(
            emptyList()
        ) { product ->

            val intent =
                Intent(
                    this,
                    ProductDetailActivity::class.java
                )

            intent.putExtra(
                "PRODUCT_ID",
                product.id
            )

            startActivity(intent)
        }

        rvProducts.layoutManager =
            GridLayoutManager(
                this,
                2
            )

        rvProducts.adapter = adapter

        findViewById<View>(
            R.id.btnBack
        ).setOnClickListener {
            finish()
        }

        findViewById<View>(
            R.id.btnBag
        ).setOnClickListener {

            startActivity(
                Intent(
                    this,
                    CartActivity::class.java
                )
            )
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

                    searchProducts(
                        s.toString()
                    )
                }

                override fun afterTextChanged(
                    s: android.text.Editable?
                ) {}
            }
        )

        searchProducts("")
    }

    private fun searchProducts(
        keyword: String
    ) {

        val products =
            ProductRepository.search(
                keyword
            )

        adapter.updateProducts(
            products
        )

        if (keyword.isBlank()) {

            tvSearchTitle.text =
                "Recommended For You"

        } else {

            tvSearchTitle.text =
                "Search Results"

        }

        tvProductCount.text =
            "${products.size} products found"

        tvEmpty.visibility =
            if (products.isEmpty()) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }
}