package com.example.creditbook.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.creditbook.R
import com.example.creditbook.data.EntryType
import com.example.creditbook.data.LedgerEntry
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CustomerDetailActivity : AppCompatActivity() {

    private val viewModel: CustomerDetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_customer_detail)

        // Identify the customer selected on the customer list.
        val customerId = intent.getLongExtra(EXTRA_CUSTOMER_ID, -1L)

        if (customerId <= 0L) {
            finish()
            return
        }

        val nameText = findViewById<TextView>(R.id.textCustomerName)
        val phoneText = findViewById<TextView>(R.id.textCustomerPhone)
        val balanceText = findViewById<TextView>(R.id.textCustomerBalance)
        val historyText = findViewById<TextView>(R.id.textCustomerHistory)
        val emptyText = findViewById<TextView>(R.id.textHistoryEmpty)

        val addDebtButton = findViewById<Button>(R.id.buttonAddDebt)
        val addPaymentButton = findViewById<Button>(R.id.buttonAddPayment)
        val backButton = findViewById<Button>(R.id.buttonBack)

        // Prevent adding entries until the customer has been loaded.
        addDebtButton.isEnabled = false
        addPaymentButton.isEnabled = false

        viewModel.loadCustomer(customerId)

        viewModel.customer.observe(this) { customer ->
            if (customer == null) {
                nameText.text = "Customer unavailable"
                phoneText.text = ""
                addDebtButton.isEnabled = false
                addPaymentButton.isEnabled = false
            } else {
                nameText.text = customer.name
                phoneText.text = customer.phone.ifBlank {
                    "No phone number provided"
                }
                addDebtButton.isEnabled = true
                addPaymentButton.isEnabled = true
            }
        }

        viewModel.balanceNgwee.observe(this) { balance ->
            balanceText.text = "Balance: ${balance.toKwacha()}"
        }

        viewModel.entries.observe(this) { entries ->
            emptyText.isVisible = entries.isEmpty()
            historyText.isVisible = entries.isNotEmpty()

            historyText.text = entries
                .sortedByDescending { it.createdAt }
                .joinToString(separator = "\n\n") { entry ->
                    formatEntry(entry)
                }
        }

        addDebtButton.setOnClickListener {
            openEntryForm(customerId, EntryType.DEBT)
        }

        addPaymentButton.setOnClickListener {
            openEntryForm(customerId, EntryType.PAYMENT)
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun openEntryForm(customerId: Long, entryType: String) {
        val intent = Intent(this, AddEntryActivity::class.java).apply {
            putExtra(AddEntryActivity.EXTRA_CUSTOMER_ID, customerId)
            putExtra(EXTRA_ENTRY_TYPE, entryType)
        }
        startActivity(intent)
    }

    private fun formatEntry(entry: LedgerEntry): String {
        val dateFormat = SimpleDateFormat(
            "d MMM yyyy",
            Locale.getDefault()
        )

        val typeLabel = when (entry.type) {
            EntryType.DEBT -> "Debt"
            EntryType.PAYMENT -> "Payment"
            else -> "Entry"
        }

        return buildString {
            append("$typeLabel: ${entry.amountNgwee.toKwacha()}")
            append("\nRecorded: ${dateFormat.format(Date(entry.createdAt))}")

            if (entry.note.isNotBlank()) {
                append("\nNote: ${entry.note}")
            }

            if (entry.type == EntryType.DEBT) {
                entry.dueDate?.let { dueDate ->
                    append("\nDue: ${dateFormat.format(Date(dueDate))}")
                }
            }
        }
    }

    companion object {
        const val EXTRA_CUSTOMER_ID = "customerId"
        const val EXTRA_ENTRY_TYPE = "entryType"
    }
}