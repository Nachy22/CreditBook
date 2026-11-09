package com.example.creditbook.ui.summary

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.creditbook.data.AppDatabase
import com.example.creditbook.data.CreditRepository
import com.example.creditbook.data.CustomerWithBalance
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

/** Everything the Summary screen needs to draw itself. */
data class SummaryUiState(
    val isLoading: Boolean = true,
    val totalOwedNgwee: Long = 0,
    val debtorCount: Int = 0,
    val topDebtors: List<CustomerWithBalance> = emptyList()
)

class SummaryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = CreditRepository(AppDatabase.get(application).creditDao())

    /** Recalculates automatically whenever a debt or payment is saved. */
    val uiState: StateFlow<SummaryUiState> = repository.customers
        .map { customers ->
            // Only customers who actually owe money (balance above zero).
            val debtors = customers.filter { it.balanceNgwee > 0 }
            SummaryUiState(
                isLoading = false,
                totalOwedNgwee = debtors.sumOf { it.balanceNgwee },
                debtorCount = debtors.size,
                topDebtors = debtors.sortedByDescending { it.balanceNgwee }.take(5)
            )
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), SummaryUiState())
}
