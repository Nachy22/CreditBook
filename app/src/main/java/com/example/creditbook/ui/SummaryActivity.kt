package com.example.creditbook.ui.summary

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.example.creditbook.databinding.ActivitySummaryBinding
import com.example.creditbook.ui.CustomerDetailActivity
import kotlinx.coroutines.launch
import com.example.creditbook.ui.toKwacha

class SummaryActivity : AppCompatActivity() {

    private val viewModel: SummaryViewModel by viewModels()
    private lateinit var binding: ActivitySummaryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySummaryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        title = "Summary"

        val adapter = DebtorAdapter { customer ->
            startActivity(
                Intent(this, CustomerDetailActivity::class.java)
                    .putExtra(CustomerDetailActivity.EXTRA_CUSTOMER_ID, customer.id)
            )
        }
        binding.debtorList.adapter = adapter

        // Updates automatically when data changes; the ViewModel keeps data through rotation.
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    val hasDebtors = !state.isLoading && state.debtorCount > 0
                    binding.loading.visibility = if (state.isLoading) View.VISIBLE else View.GONE
                    binding.emptyText.visibility =
                        if (!state.isLoading && state.debtorCount == 0) View.VISIBLE else View.GONE
                    binding.content.visibility = if (hasDebtors) View.VISIBLE else View.GONE

                    binding.totalOwed.text = state.totalOwedNgwee.toKwacha()
                    binding.debtorCount.text =
                        if (state.debtorCount == 1) "1 customer owes you"
                        else "${state.debtorCount} customers owe you"
                    adapter.submitList(state.topDebtors)
                }
            }
        }
    }
}
