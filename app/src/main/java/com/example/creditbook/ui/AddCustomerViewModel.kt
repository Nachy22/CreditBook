package com.example.creditbook.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.creditbook.data.AppDatabase
import com.example.creditbook.data.CreditRepository
import kotlinx.coroutines.launch

class AddCustomerViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = CreditRepository(AppDatabase.get(app).creditDao())

    val saved = MutableLiveData(false)

    fun nameError(name: String): String? =
        if (name.isBlank()) "Please enter the customer's name" else null

    fun phoneError(phone: String): String? =
        if (PHONE.matches(phone.replace(" ", ""))) null
        else "Enter a valid number, e.g. 0977123456"

    fun save(name: String, phone: String) {
        viewModelScope.launch {
            repo.addCustomer(name, phone.replace(" ", ""))
            saved.value = true
        }
    }

    companion object {
        // Zambian mobile: 09XXXXXXXX, 07XXXXXXXX or +2609XXXXXXXX / +2607XXXXXXXX
        private val PHONE = Regex("^(\\+260|0)[79]\\d{8}$")
    }
}