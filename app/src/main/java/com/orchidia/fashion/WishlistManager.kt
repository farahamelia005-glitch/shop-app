package com.orchidia.fashion

object WishlistManager {

    private val wishlistIds = mutableSetOf<String>()

    fun addProduct(productId: String) {
        wishlistIds.add(productId)
    }

    fun removeProduct(productId: String) {
        wishlistIds.remove(productId)
    }

    fun toggleProduct(productId: String) {
        if (wishlistIds.contains(productId)) {
            wishlistIds.remove(productId)
        } else {
            wishlistIds.add(productId)
        }
    }

    fun isWishlisted(productId: String): Boolean {
        return wishlistIds.contains(productId)
    }

    fun getWishlistProducts(): List<Product> {
        return wishlistIds.mapNotNull {
            ProductRepository.getById(it)
        }
    }

    fun clear() {
        wishlistIds.clear()
    }
}