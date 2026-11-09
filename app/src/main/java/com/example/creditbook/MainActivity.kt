package com.example.creditbook

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.creditbook.ui.AddCustomerActivity
import com.example.creditbook.ui.CustomerAdapter
import com.example.creditbook.ui.CustomerDetailActivity
import com.example.creditbook.ui.CustomerListViewModel
import com.example.creditbook.ui.summary.SummaryActivity
import com.example.creditbook.work.ReminderScheduler
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val viewModel: CustomerListViewModel by viewModels()

    // Register before the activity starts.
    private val notificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { granted ->
            val message = if (granted) {
                "Notifications allowed."
            } else {
                "Notifications are off. You can still use CreditBook."
            }
            Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val list = findViewById<RecyclerView>(R.id.recyclerCustomers)
        val emptyText = findViewById<TextView>(R.id.textEmpty)

        val adapter = CustomerAdapter { customer ->
            val intent = Intent(this, CustomerDetailActivity::class.java)
            intent.putExtra(CustomerDetailActivity.EXTRA_CUSTOMER_ID, customer.id)
            startActivity(intent)
        }
        list.layoutManager = LinearLayoutManager(this)
        list.adapter = adapter

        viewModel.customers.observe(this) { customers ->
            adapter.submitList(customers)
            emptyText.isVisible = customers.isEmpty()
        }

        findViewById<FloatingActionButton>(R.id.fabAddCustomer).setOnClickListener {
            startActivity(Intent(this, AddCustomerActivity::class.java))
        }

        findViewById<Button>(R.id.buttonSummary).setOnClickListener {
            startActivity(Intent(this, SummaryActivity::class.java))
        }

        // Start the daily overdue check (does nothing if already scheduled)
        ReminderScheduler.scheduleDaily(this)


        if (savedInstanceState == null) {
            requestNotificationPermissionIfNeeded()
        }
    }

    private fun requestNotificationPermissionIfNeeded() {
        // Earlier Android versions do not use this runtime permission.
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            return
        }

        val permissionGranted = ContextCompat.checkSelfPermission(
            this,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        if (permissionGranted) return

        val preferences = getSharedPreferences(
            "notification_preferences",
            MODE_PRIVATE
        )

        // Avoid repeatedly prompting after denial or dismissal.
        if (preferences.getBoolean("permission_requested", false)) {
            return
        }

        preferences.edit()
            .putBoolean("permission_requested", true)
            .apply()

        notificationPermissionLauncher.launch(
            Manifest.permission.POST_NOTIFICATIONS
        )
    }
}