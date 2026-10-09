
package com.orchidia.fashion

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity

class AccountActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_account)

        findViewById<View>(R.id.btnBack).setOnClickListener {
            finish()
        }

        findViewById<View>(R.id.btnEditProfile).setOnClickListener {
            startActivity(
                Intent(this, EditProfileActivity::class.java)
            )
        }

        findViewById<View>(R.id.accountOrderHistory).setOnClickListener {
            startActivity(
                Intent(this, OrderHistoryActivity::class.java)
            )
        }

        findViewById<View>(R.id.accountSettings).setOnClickListener {
            startActivity(
                Intent(this, SettingsActivity::class.java)
            )
        }

        findViewById<View>(R.id.accountWishlist).setOnClickListener {
            startActivity(
                Intent(this, WishlistActivity::class.java)
            )
        }

        findViewById<View>(R.id.accountAddress).setOnClickListener {
            startActivity(
                Intent(this, ShippingAddressActivity::class.java)
            )
        }

        findViewById<View>(R.id.navHome).setOnClickListener {
            startActivity(Intent(this, HomeActivity::class.java))
        }

        findViewById<View>(R.id.navShop).setOnClickListener {
            startActivity(Intent(this, CategoryActivity::class.java))
        }

        findViewById<View>(R.id.navWishlist).setOnClickListener {
            startActivity(Intent(this, WishlistActivity::class.java))
        }

        findViewById<View>(R.id.navBag).setOnClickListener {
            startActivity(Intent(this, CartActivity::class.java))
        }

        findViewById<View>(R.id.navAccount).setOnClickListener {
            // Sudah berada di halaman Account.
        }
    }
}