package com.orchidia.fashion

import com.orchidia.fashion.data.remote.SupabaseProduct

object ProductRepository {

    val products = listOf(
        Product("floral_jeans", "Floral Jeans", "PANTS", 239000, R.drawable.floral_jeans),
        Product("melisa_shirt", "Melisa Top", "TOP", 259000, R.drawable.melisa_shirt),
        Product("oneset_michi", "Oneset Michi", "DRESS", 378000, R.drawable.oneset_michi),
        Product("heartmirror_bag", "Heart Mirror Shoulder Bag", "BAG", 175000, R.drawable.heartmirror_bag),
        Product("baby_cream_dress", "Baby Cream Dress", "DRESS", 285000, R.drawable.babycream_dress),
        Product("hannah_blazer", "Hannah Blazer", "TOP", 275000, R.drawable.hannah_blazer),
        Product("ribbon_tee", "Ribbon Baby Tee", "TOP", 129000, R.drawable.ribbon_tee),
        Product("Amara_Skirt", "Amara Skirt", "SKIRT", 189000, R.drawable.amara_skirt)
    )

    fun getAll(): List<Product> = products

    fun getById(id: String): Product? =
        products.find { it.id.equals(id, ignoreCase = true) }

    fun getByCategory(category: String): List<Product> =
        if (category.equals("ALL", ignoreCase = true)) {
            products
        } else {
            products.filter {
                it.category.equals(category, ignoreCase = true)
            }
        }

    fun search(keyword: String): List<Product> {
        if (keyword.isBlank()) return products

        return products.filter {
            it.name.contains(keyword, ignoreCase = true) ||
                    it.category.contains(keyword, ignoreCase = true)
        }
    }

    fun fromSupabase(remoteProducts: List<SupabaseProduct>): List<Product> {
        return remoteProducts.mapNotNull { remote ->
            val local = getById(remote.id)

            if (local != null) {
                local.copy(
                    name = remote.name,
                    category = remote.category,
                    price = remote.price
                )
            } else {
                null
            }
        }
    }
}