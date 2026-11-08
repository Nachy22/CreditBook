package com.example.creditbook

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.creditbook.ui.AddCustomerActivity
import com.example.creditbook.ui.CustomerAdapter
import com.example.creditbook.ui.CustomerListViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {

    private val viewModel: CustomerListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = findViewById<RecyclerView>(R.id.recyclerCustomers)
        val emptyText = findViewById<TextView>(R.id.textEmpty)

        val adapter = CustomerAdapter { customer ->
            // Later: open the Customer details screen (Member 4's part)
            Toast.makeText(this, customer.name, Toast.LENGTH_SHORT).show()
        }
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        viewModel.customers.observe(this) { customers ->
            adapter.submitList(customers)
            emptyText.isVisible = customers.isEmpty()   // friendly empty state
        }

        findViewById<FloatingActionButton>(R.id.fabAddCustomer).setOnClickListener {
            startActivity(Intent(this, AddCustomerActivity::class.java))
        }
    }
}