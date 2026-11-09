package com.example.creditbook.work

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit

/**
 * Tells WorkManager when to run the overdue check.
 * Call scheduleDaily() once when the app starts.
 */
object ReminderScheduler {

    private const val DAILY_WORK_NAME = "overdue_check_daily"

    /**
     * Runs the check about once every 24 hours.
     * KEEP means: if this job is already scheduled, leave it alone.
     * Without that, opening the app 10 times would schedule 10 jobs.
     */
    fun scheduleDaily(context: Context) {
        val request = PeriodicWorkRequestBuilder<OverdueCheckWorker>(1, TimeUnit.DAYS).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            DAILY_WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    /**
     * For TESTING only: runs the check right now instead of waiting a day.
     * Remove the call to this before final submission, or keep it behind a debug button.
     */
    fun runNow(context: Context) {
        val request = OneTimeWorkRequestBuilder<OverdueCheckWorker>().build()
        WorkManager.getInstance(context).enqueue(request)
    }
}