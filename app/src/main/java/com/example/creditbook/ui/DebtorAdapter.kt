package com.example.creditbook.ui.summary

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.creditbook.data.CustomerWithBalance
import com.example.creditbook.databinding.ItemDebtorBinding
import com.example.creditbook.ui.toKwacha

class DebtorAdapter(
    private val onClick: (CustomerWithBalance) -> Unit
) : ListAdapter<CustomerWithBalance, DebtorAdapter.ViewHolder>(Diff) {

    class ViewHolder(val binding: ItemDebtorBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        ViewHolder(ItemDebtorBinding.inflate(LayoutInflater.from(parent.context), parent, false))

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val customer = getItem(position)
        with(holder.binding) {
            rank.text = "${position + 1}."
            name.text = customer.name
            phone.text = customer.phone
            phone.visibility = if (customer.phone.isBlank()) android.view.View.GONE else android.view.View.VISIBLE
            balance.text = customer.balanceNgwee.toKwacha()
            root.setOnClickListener { onClick(customer) }
        }
    }

    private object Diff : DiffUtil.ItemCallback<CustomerWithBalance>() {
        override fun areItemsTheSame(a: CustomerWithBalance, b: CustomerWithBalance) = a.id == b.id
        override fun areContentsTheSame(a: CustomerWithBalance, b: CustomerWithBalance) = a == b
    }
}
