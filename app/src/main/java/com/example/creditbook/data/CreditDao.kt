package com.example.creditbook.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CreditDao {

    // ---- Writing ----
    @Insert
    suspend fun insertCustomer(customer: Customer): Long

    @Update
    suspend fun updateCustomer(customer: Customer)

    @Insert
    suspend fun insertEntry(entry: LedgerEntry)

    // ---- Reading (Flow = the screen updates automatically when data changes) ----

    /** Every customer with the amount they owe. The balance is always calculated from the history. */
    @Query(
        """
        SELECT c.id, c.name, c.phone,
               COALESCE(SUM(CASE e.type
                   WHEN 'DEBT' THEN e.amountNgwee
                   WHEN 'PAYMENT' THEN -e.amountNgwee
               END), 0) AS balanceNgwee
        FROM customers c
        LEFT JOIN ledger_entries e ON e.customerId = c.id
        GROUP BY c.id
        ORDER BY c.name COLLATE NOCASE
        """
    )
    fun getCustomersWithBalance(): Flow<List<CustomerWithBalance>>

    @Query("SELECT * FROM customers WHERE id = :customerId")
    fun getCustomer(customerId: Long): Flow<Customer?>

    @Query("SELECT * FROM ledger_entries WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getEntriesForCustomer(customerId: Long): Flow<List<LedgerEntry>>

    /** Used to check that a payment is not more than what the customer owes. */
    @Query(
        """
        SELECT COALESCE(SUM(CASE type
                   WHEN 'DEBT' THEN amountNgwee
                   WHEN 'PAYMENT' THEN -amountNgwee
               END), 0)
        FROM ledger_entries WHERE customerId = :customerId
        """
    )
    suspend fun getBalance(customerId: Long): Long

    /**
     * For the daily WorkManager reminder: customers who still owe money and have a
     * debt whose due date is before [now]. One-off call (not a Flow) because a background job needs it once.
     */
    @Query(
        """
        SELECT c.id, c.name, c.phone,
               SUM(CASE e.type
                   WHEN 'DEBT' THEN e.amountNgwee
                   WHEN 'PAYMENT' THEN -e.amountNgwee
               END) AS balanceNgwee
        FROM customers c
        JOIN ledger_entries e ON e.customerId = c.id
        GROUP BY c.id
        HAVING balanceNgwee > 0
           AND MIN(CASE WHEN e.type = 'DEBT' AND e.dueDate IS NOT NULL THEN e.dueDate END) < :now
        """
    )
    suspend fun getOverdueCustomers(now: Long): List<CustomerWithBalance>
}