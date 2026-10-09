package com.orchidia.fashion

object ProductRepository {

    val products = listOf(

        Product(
            id = "floral_jeans",
            name = "Floral Jeans",
            category = "PANTS",
            price = 239000,
            imageRes = R.drawable.floral_jeans
        ),

        Product(
            id = "melisa_shirt",
            name = "Melisa Top",
            category = "TOP",
            price = 259000,
            imageRes = R.drawable.melisa_shirt
        ),

        Product(
            id = "oneset_michi",
            name = "Oneset Michi",
            category = "DRESS",
            price = 378000,
            imageRes = R.drawable.oneset_michi
        ),

        Product(
            id = "heart_mirror_bag",
            name = "Heart Mirror Shoulder Bag",
            category = "BAG",
            price = 175000,
            imageRes = R.drawable.heartmirror_bag
        ),

        Product(
            id = "baby_cream_dress",
            name = "Baby Cream Dress",
            category = "DRESS",
            price = 285000,
            imageRes = R.drawable.babycream_dress
        ),

        Product(
            id = "hannah_blazer",
            name = "hannah_blazer",
            category = "TOP",
            price = 275000,
            imageRes = R.drawable.hannah_blazer
        ),

        Product(
            id = "ribbon_baby_tee",
            name = "Ribbon Baby Tee",
            category = "TOP",
            price = 129000,
            imageRes = R.drawable.ribbon_tee
        ),

        Product(
            id = "Amara_Skirt",
            name = "Amara Skirt",
            category = "SKIRT",
            price = 189000,
            imageRes = R.drawable.amara_skirt
        )
    )

    fun getAll(): List<Product> {
        return products
    }

    fun getById(id: String): Product? {
        return products.find { it.id == id }
    }

    fun getByCategory(category: String): List<Product> {
        return products.filter {
            it.category.equals(category, ignoreCase = true)
        }
    }

    fun search(keyword: String): List<Product> {

        if (keyword.isBlank()) {
            return products
        }

        return products.filter {
            it.name.contains(keyword, ignoreCase = true) ||
                    it.category.contains(keyword, ignoreCase = true)
        }
    }
}