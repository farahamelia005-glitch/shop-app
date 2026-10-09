package com.orchidia.fashion

object CartManager {

    private val items = mutableMapOf<String, Int>()

    fun addProduct(
        productId: String,
        quantity: Int = 1
    ) {
        items[productId] =
            (items[productId] ?: 0) + quantity
    }

    fun removeProduct(productId: String) {
        items.remove(productId)
    }

    fun increase(productId: String) {
        items[productId] =
            (items[productId] ?: 0) + 1
    }

    fun decrease(productId: String) {

        val current =
            items[productId] ?: return

        if (current <= 1) {
            items.remove(productId)
        } else {
            items[productId] = current - 1
        }
    }

    fun getQuantity(productId: String): Int {
        return items[productId] ?: 0
    }

    fun getItems(): Map<String, Int> {
        return items.toMap()
    }

    fun clear() {
        items.clear()
    }

    fun calculateTotal(
        selectedIds: Set<String>
    ): Int {

        var total = 0

        for (id in selectedIds) {

            val product =
                ProductRepository.getById(id)

            val quantity =
                getQuantity(id)

            if (product != null) {
                total +=
                    product.price * quantity
            }
        }

        return total
    }
}