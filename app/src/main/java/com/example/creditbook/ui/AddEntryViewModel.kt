package com.example.creditbook.ui


import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.creditbook.data.AppDatabase
import com.example.creditbook.data.CreditRepository
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class AddEntryViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = CreditRepository(AppDatabase.get(app).creditDao())

    // Kept here (not in the screen) so they survive rotation
    val dueDate = MutableLiveData<Long?>(null)
    val amountError = MutableLiveData<String?>(null)
    val dueDateError = MutableLiveData<String?>(null)
    val saved = MutableLiveData(false)
    private var saving = false

    fun customerName(customerId: Long): LiveData<String> =
        repo.customer(customerId).map { it?.name ?: "" }.asLiveData()

    fun save(customerId: Long, isDebt: Boolean, amountText: String, note: String) {
        if (saving) return
        amountError.value = null
        dueDateError.value = null

        val amount = parseKwacha(amountText)
        if (amount == null) {
            amountError.value = "Enter an amount greater than 0, e.g. 25.50"
            return
        }
        val due = dueDate.value
        if (isDebt && due == null) {
            dueDateError.value = "Choose the date the customer will pay"
            return
        }

        saving = true
        viewModelScope.launch {
            if (isDebt) {
                repo.addDebt(customerId, amount, note, due!!)
            } else {
                val owed = repo.balanceOf(customerId)
                if (owed <= 0) {
                    amountError.value = "This customer doesn't owe anything"
                    saving = false
                    return@launch
                }
                if (amount > owed) {
                    amountError.value = "They only owe ${owed.toKwacha()}"
                    saving = false
                    return@launch
                }
                repo.addPayment(customerId, amount, note)
            }
            saved.value = true
        }
    }
}