package com.example.creditbook.ui

import android.app.DatePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.RadioGroup
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import com.example.creditbook.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEntryActivity : AppCompatActivity() {

    private val viewModel: AddEntryViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_entry)

        // Which customer is this for? Sent by the screen that opened us.
        val customerId = intent.getLongExtra(EXTRA_CUSTOMER_ID, -1L)
        if (customerId == -1L) { finish(); return }

        val title = findViewById<TextView>(R.id.textTitle)
        val typeGroup = findViewById<RadioGroup>(R.id.radioType)
        val amountLayout = findViewById<TextInputLayout>(R.id.layoutAmount)
        val amountInput = findViewById<TextInputEditText>(R.id.inputAmount)
        val noteInput = findViewById<TextInputEditText>(R.id.inputNote)
        val dueDateButton = findViewById<Button>(R.id.buttonDueDate)
        val dueDateErrorText = findViewById<TextView>(R.id.textDueDateError)
        val saveButton = findViewById<Button>(R.id.buttonSave)

        fun isDebt() = typeGroup.checkedRadioButtonId == R.id.radioDebt

        // Only debts have a due date
        fun showOrHideDueDate() {
            dueDateButton.isVisible = isDebt()
            if (!isDebt()) dueDateErrorText.isVisible = false
        }
        typeGroup.setOnCheckedChangeListener { _, _ -> showOrHideDueDate() }
        showOrHideDueDate()

        viewModel.customerName(customerId).observe(this) { name ->
            title.text = "New entry for $name"
        }

        dueDateButton.setOnClickListener {
            val today = Calendar.getInstance()
            val dialog = DatePickerDialog(this, { _, year, month, day ->
                // End of that day, so "due today" isn't overdue until tomorrow
                val picked = Calendar.getInstance().apply { set(year, month, day, 23, 59, 59) }
                viewModel.dueDate.value = picked.timeInMillis
            }, today.get(Calendar.YEAR), today.get(Calendar.MONTH), today.get(Calendar.DAY_OF_MONTH))
            dialog.datePicker.minDate = today.timeInMillis - 1000   // no past dates
            dialog.show()
        }

        viewModel.dueDate.observe(this) { date ->
            dueDateButton.text = if (date == null) "Choose due date"
            else "Due: " + SimpleDateFormat("d MMM yyyy", Locale.getDefault()).format(Date(date))
        }
        viewModel.amountError.observe(this) { amountLayout.error = it }
        viewModel.dueDateError.observe(this) { error ->
            dueDateErrorText.text = error
            dueDateErrorText.isVisible = error != null
        }

        saveButton.setOnClickListener {
            viewModel.save(
                customerId,
                isDebt(),
                amountInput.text.toString(),
                noteInput.text.toString()
            )
        }
        viewModel.saved.observe(this) { done -> if (done) finish() }
    }

    companion object {
        const val EXTRA_CUSTOMER_ID = "customerId"
    }
}