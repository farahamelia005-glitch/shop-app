package com.orchidia.fashion

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        com.orchidia.fashion.data.remote.SupabaseTest.testConnection()

        // SEARCH
        findViewById<View>(R.id.btnSearch).setOnClickListener {
            startActivity(Intent(this, SearchActivity::class.java))
        }

        // BAG
        findViewById<View>(R.id.btnBag).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        // SEE ALL CATEGORIES
        findViewById<View>(R.id.tvSeeAll).setOnClickListener {
            openCategory("ALL")
        }

        // BEST SELLER
        findViewById<View>(R.id.homeFloralJeans).setOnClickListener {
            openProduct("floral_jeans")
        }

        findViewById<View>(R.id.homeMelisaShirt).setOnClickListener {
            openProduct("melisa_shirt")
        }

        // CATEGORIES
        findViewById<View>(R.id.categoryTop).setOnClickListener {
            openCategory("TOP")
        }

        findViewById<View>(R.id.categoryPants).setOnClickListener {
            openCategory("PANTS")
        }

        findViewById<View>(R.id.categoryBag).setOnClickListener {
            openCategory("BAG")
        }

        findViewById<View>(R.id.categoryDress).setOnClickListener {
            openCategory("DRESS")
        }

        findViewById<View>(R.id.categorySkirt).setOnClickListener {
            openCategory("SKIRT")
        }

        // BOTTOM NAVIGATION
        findViewById<View>(R.id.navHome).setOnClickListener {
            // sudah di Home
        }

        findViewById<View>(R.id.navShop).setOnClickListener {
            openCategory("ALL")
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

    private fun openCategory(category: String) {
        val intent = Intent(this, CategoryActivity::class.java)
        intent.putExtra("CATEGORY", category)
        startActivity(intent)
    }

    private fun openProduct(productId: String) {
        val intent = Intent(this, ProductDetailActivity::class.java)
        intent.putExtra("PRODUCT_ID", productId)
        startActivity(intent)
    }
}