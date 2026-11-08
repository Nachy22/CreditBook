package com.example.creditbook.data

import kotlinx.coroutines.flow.Flow

/**
 * The only door to the data. Screens and ViewModels talk to this class,
 * never to Room directly, which keeps screen code separate from data code.
 */
class CreditRepository(private val dao: CreditDao) {

    val customers: Flow<List<CustomerWithBalance>> = dao.getCustomersWithBalance()

    fun customer(id: Long): Flow<Customer?> = dao.getCustomer(id)

    fun entries(customerId: Long): Flow<List<LedgerEntry>> = dao.getEntriesForCustomer(customerId)

    suspend fun addCustomer(name: String, phone: String): Long =
        dao.insertCustomer(Customer(name = name.trim(), phone = phone.trim()))

    suspend fun updateCustomer(customer: Customer) = dao.updateCustomer(customer)

    suspend fun addDebt(customerId: Long, amountNgwee: Long, note: String, dueDate: Long) =
        dao.insertEntry(
            LedgerEntry(
                customerId = customerId,
                type = EntryType.DEBT,
                amountNgwee = amountNgwee,
                note = note.trim(),
                dueDate = dueDate
            )
        )

    suspend fun addPayment(customerId: Long, amountNgwee: Long, note: String) =
        dao.insertEntry(
            LedgerEntry(
                customerId = customerId,
                type = EntryType.PAYMENT,
                amountNgwee = amountNgwee,
                note = note.trim()
            )
        )

    suspend fun balanceOf(customerId: Long): Long = dao.getBalance(customerId)

    suspend fun overdueCustomers(now: Long = System.currentTimeMillis()): List<CustomerWithBalance> =
        dao.getOverdueCustomers(now)
}