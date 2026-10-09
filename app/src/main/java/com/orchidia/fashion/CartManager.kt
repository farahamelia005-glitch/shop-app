package com.orchidia.fashion

object CartManager {

    private val items = mutableMapOf<String, Int>()

    fun addProduct(productId: String, quantity: Int = 1) {
        if (quantity <= 0) return
        items[productId] = (items[productId] ?: 0) + quantity
    }

    fun removeProduct(productId: String) {
        items.remove(productId)
    }

    fun increase(productId: String) {
        items[productId] = (items[productId] ?: 0) + 1
    }

    fun decrease(productId: String) {
        val current = items[productId] ?: return

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

    fun replaceItems(remoteItems: Map<String, Int>) {
        items.clear()
        items.putAll(remoteItems.filterValues { it > 0 })
    }

    fun clear() {
        items.clear()
    }

    fun calculateTotal(selectedIds: Set<String>): Int {
        return selectedIds.sumOf { id ->
            val product = ProductRepository.getById(id)
            val quantity = getQuantity(id)

            if (product != null) product.price * quantity else 0
        }
    }
}