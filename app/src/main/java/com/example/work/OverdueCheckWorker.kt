package com.example.creditbook.work

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.creditbook.data.AppDatabase

/**
 * The daily job. WorkManager creates this class and calls doWork(),
 * even if the app is closed.
 *
 * It asks Martin's database who is overdue and, if anyone is,
 * asks NotificationHelper to show the message.
 */
class OverdueCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        return try {
            val dao = AppDatabase.get(applicationContext).creditDao()

            // dueDate is stored in milliseconds, so "now" must be in milliseconds too.
            val overdue = dao.getOverdueCustomers(System.currentTimeMillis())

            if (overdue.isNotEmpty()) {
                NotificationHelper.showOverdue(applicationContext, overdue.size)
            }
            Result.success()
        } catch (e: Exception) {
            // Something went wrong (for example the database was busy). Try again later.
            Result.retry()
        }
    }
}