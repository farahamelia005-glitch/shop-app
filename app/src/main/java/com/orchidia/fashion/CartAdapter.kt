package com.orchidia.fashion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class CartAdapter(
    private var products: List<Product>,
    private val selectedIds: MutableSet<String>,
    private val onSelectionChanged: () -> Unit,
    private val onCartChanged: (Product, Int) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    class CartViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView) {

        val checkProduct: CheckBox =
            itemView.findViewById(R.id.checkProduct)

        val imgProduct: ImageView =
            itemView.findViewById(R.id.imgProduct)

        val tvProductName: TextView =
            itemView.findViewById(R.id.tvProductName)

        val tvProductPrice: TextView =
            itemView.findViewById(R.id.tvProductPrice)

        val tvQuantity: TextView =
            itemView.findViewById(R.id.tvQuantity)

        val btnDecrease: Button =
            itemView.findViewById(R.id.btnDecrease)

        val btnIncrease: Button =
            itemView.findViewById(R.id.btnIncrease)

        val btnDelete: ImageButton =
            itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CartViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_cart, parent, false)

        return CartViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: CartViewHolder,
        position: Int
    ) {
        val product = products[position]

        holder.imgProduct.setImageResource(product.imageRes)
        holder.tvProductName.text = product.name
        holder.tvProductPrice.text = formatRupiah(product.price)
        holder.tvQuantity.text =
            CartManager.getQuantity(product.id).toString()

        holder.checkProduct.setOnCheckedChangeListener(null)
        holder.checkProduct.isChecked =
            selectedIds.contains(product.id)

        holder.checkProduct.setOnCheckedChangeListener { _, checked ->
            if (checked) {
                selectedIds.add(product.id)
            } else {
                selectedIds.remove(product.id)
            }

            onSelectionChanged()
        }

        holder.btnIncrease.setOnClickListener {
            CartManager.increase(product.id)

            holder.tvQuantity.text =
                CartManager.getQuantity(product.id).toString()

            onSelectionChanged()
            onCartChanged(
                product,
                CartManager.getQuantity(product.id)
            )
        }

        holder.btnDecrease.setOnClickListener {
            CartManager.decrease(product.id)

            val quantity = CartManager.getQuantity(product.id)

            if (quantity == 0) {
                selectedIds.remove(product.id)
                products = getCartProducts()
                notifyDataSetChanged()
            } else {
                holder.tvQuantity.text = quantity.toString()
            }

            onSelectionChanged()
            onCartChanged(product, quantity)
        }

        holder.btnDelete.setOnClickListener {
            CartManager.removeProduct(product.id)
            selectedIds.remove(product.id)

            products = getCartProducts()
            notifyDataSetChanged()

            onSelectionChanged()
            onCartChanged(product, 0)
        }
    }

    override fun getItemCount(): Int = products.size

    fun refresh() {
        products = getCartProducts()
        notifyDataSetChanged()
    }

    private fun getCartProducts(): List<Product> {
        return CartManager.getItems().keys.mapNotNull { id ->
            ProductRepository.getById(id)
        }
    }

    private fun formatRupiah(value: Int): String {
        return NumberFormat.getNumberInstance(Locale("id", "ID"))
            .format(value)
            .let { "Rp $it" }
    }
}