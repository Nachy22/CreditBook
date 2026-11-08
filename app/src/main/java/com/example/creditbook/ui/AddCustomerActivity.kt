package com.example.creditbook.ui

import android.os.Bundle
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.creditbook.R
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

class AddCustomerActivity : AppCompatActivity() {

    private val viewModel: AddCustomerViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_customer)

        val nameLayout = findViewById<TextInputLayout>(R.id.layoutName)
        val phoneLayout = findViewById<TextInputLayout>(R.id.layoutPhone)
        val nameInput = findViewById<TextInputEditText>(R.id.inputName)
        val phoneInput = findViewById<TextInputEditText>(R.id.inputPhone)
        val saveButton = findViewById<Button>(R.id.buttonSave)

        saveButton.setOnClickListener {
            val name = nameInput.text.toString()
            val phone = phoneInput.text.toString()

            val nameErr = viewModel.nameError(name)
            val phoneErr = viewModel.phoneError(phone)
            nameLayout.error = nameErr
            phoneLayout.error = phoneErr

            if (nameErr == null && phoneErr == null) {
                saveButton.isEnabled = false   // stops double-saving
                viewModel.save(name, phone)
            }
        }

        // When saving is finished, close this screen and go back to the list
        viewModel.saved.observe(this) { done -> if (done) finish() }
    }
}