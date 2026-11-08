package com.example.creditbook.ui


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import com.example.creditbook.data.AppDatabase
import com.example.creditbook.data.CreditRepository
import com.example.creditbook.data.CustomerWithBalance

class CustomerListViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = CreditRepository(AppDatabase.get(app).creditDao())

    // Updates automatically whenever a customer, debt or payment is added
    val customers: LiveData<List<CustomerWithBalance>> = repo.customers.asLiveData()
}