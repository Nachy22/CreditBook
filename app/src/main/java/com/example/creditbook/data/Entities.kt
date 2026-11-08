package com.example.creditbook.data

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/** One shop customer. */
@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val phone: String,
    val createdAt: Long = System.currentTimeMillis()
)

/** One debt or payment belonging to a customer. Money is stored in whole ngwee (K25.50 = 2550). */
@Entity(
    tableName = "ledger_entries",
    foreignKeys = [
        ForeignKey(
            entity = Customer::class,
            parentColumns = ["id"],
            childColumns = ["customerId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("customerId")]
)
data class LedgerEntry(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val customerId: Long,
    val type: String,           // EntryType.DEBT or EntryType.PAYMENT
    val amountNgwee: Long,
    val note: String = "",
    val dueDate: Long? = null,  // only used for debts (milliseconds since 1970)
    val createdAt: Long = System.currentTimeMillis()
)

object EntryType {
    const val DEBT = "DEBT"
    const val PAYMENT = "PAYMENT"
}

/** A customer plus their calculated balance. Not a table: filled in by a query. */
data class CustomerWithBalance(
    val id: Long,
    val name: String,
    val phone: String,
    val balanceNgwee: Long
)