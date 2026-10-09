package com.orchidia.fashion

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class ProductAdapter(
    private var products: List<Product>,
    private val onProductClick: (Product) -> Unit
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    class ProductViewHolder(
        itemView: View
    ) : RecyclerView.ViewHolder(itemView) {

        val imgProduct: ImageView =
            itemView.findViewById(R.id.imgProduct)

        val tvProductName: TextView =
            itemView.findViewById(R.id.tvProductName)

        val tvProductCategory: TextView =
            itemView.findViewById(R.id.tvProductCategory)

        val tvProductPrice: TextView =
            itemView.findViewById(R.id.tvProductPrice)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ProductViewHolder {

        val view = LayoutInflater
            .from(parent.context)
            .inflate(
                R.layout.item_product,
                parent,
                false
            )

        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: ProductViewHolder,
        position: Int
    ) {

        val product = products[position]

        holder.imgProduct.setImageResource(
            product.imageRes
        )

        holder.tvProductName.text =
            product.name

        holder.tvProductCategory.text =
            product.category

        holder.tvProductPrice.text =
            formatRupiah(product.price)

        holder.itemView.setOnClickListener {
            onProductClick(product)
        }
    }

    override fun getItemCount(): Int {
        return products.size
    }

    fun updateProducts(
        newProducts: List<Product>
    ) {
        products = newProducts
        notifyDataSetChanged()
    }

    private fun formatRupiah(
        value: Int
    ): String {

        return NumberFormat
            .getNumberInstance(
                Locale("id", "ID")
            )
            .format(value)
            .let {
                "Rp $it"
            }
    }
}