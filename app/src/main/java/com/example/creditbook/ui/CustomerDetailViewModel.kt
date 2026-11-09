package com.example.creditbook.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.map
import androidx.lifecycle.switchMap
import com.example.creditbook.data.AppDatabase
import com.example.creditbook.data.CreditRepository
import com.example.creditbook.data.Customer
import com.example.creditbook.data.EntryType
import com.example.creditbook.data.LedgerEntry

class CustomerDetailViewModel(app: Application) : AndroidViewModel(app) {

    private val repo = CreditRepository(
        AppDatabase.get(app).creditDao()
    )

    private val selectedCustomerId = MutableLiveData<Long>()

    // Observe the selected customer's information.
    val customer: LiveData<Customer?> =
        selectedCustomerId.switchMap { customerId ->
            repo.customer(customerId).asLiveData()
        }

    // Observe all debts and payments for this customer.
    val entries: LiveData<List<LedgerEntry>> =
        selectedCustomerId.switchMap { customerId ->
            repo.entries(customerId).asLiveData()
        }

    // Recalculate the balance whenever the history changes.
    val balanceNgwee: LiveData<Long> = entries.map { history ->
        history.fold(0L) { balance, entry ->
            when (entry.type) {
                EntryType.DEBT -> balance + entry.amountNgwee
                EntryType.PAYMENT -> balance - entry.amountNgwee
                else -> balance
            }
        }
    }

    fun loadCustomer(customerId: Long) {
        if (customerId <= 0L) return

        // Avoid restarting observation after a screen rotation.
        if (selectedCustomerId.value != customerId) {
            selectedCustomerId.value = customerId
        }
    }
}