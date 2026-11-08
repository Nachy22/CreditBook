package com.example.creditbook.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.creditbook.R
import com.example.creditbook.data.CustomerWithBalance

class CustomerAdapter(
    private val onClick: (CustomerWithBalance) -> Unit
) : ListAdapter<CustomerWithBalance, CustomerAdapter.Holder>(Diff) {

    class Holder(view: View) : RecyclerView.ViewHolder(view) {
        val name: TextView = view.findViewById(R.id.textName)
        val phone: TextView = view.findViewById(R.id.textPhone)
        val balance: TextView = view.findViewById(R.id.textBalance)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_customer, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val customer = getItem(position)
        holder.name.text = customer.name
        holder.phone.text = customer.phone
        holder.balance.text = customer.balanceNgwee.toKwacha()
        holder.itemView.setOnClickListener { onClick(customer) }
    }

    object Diff : DiffUtil.ItemCallback<CustomerWithBalance>() {
        override fun areItemsTheSame(a: CustomerWithBalance, b: CustomerWithBalance) = a.id == b.id
        override fun areContentsTheSame(a: CustomerWithBalance, b: CustomerWithBalance) = a == b
    }
}