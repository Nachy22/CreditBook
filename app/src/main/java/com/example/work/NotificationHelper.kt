package com.example.creditbook.work

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat

/**
 * Everything to do with showing the "overdue debts" notification.
 * Kept separate from the worker so the worker only decides WHEN to notify,
 * and this file decides HOW.
 */
object NotificationHelper {

    const val CHANNEL_ID = "overdue_debts"
    private const val NOTIFICATION_ID = 1001

    /**
     * Android 8+ will not show any notification unless it belongs to a channel.
     * Creating a channel that already exists does nothing, so it is safe to call every time.
     */
    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Overdue debts",
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Reminds you when customers have passed their payment date"
            }
            val manager = context.getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }
    }

    /**
     * Android 13+ needs the user's permission to show notifications.
     * Older phones do not ask, so for them the answer is always yes.
     */
    fun hasPermission(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS
            ) == PackageManager.PERMISSION_GRANTED

    /** "1 customer has an overdue debt" or "3 customers have overdue debts". */
    fun messageFor(count: Int): String =
        if (count == 1) "1 customer has an overdue debt"
        else "$count customers have overdue debts"

    /**
     * Shows the notification. If there is nothing overdue, or the trader said no
     * to the permission, it quietly does nothing, so the app never crashes.
     */
    fun showOverdue(context: Context, count: Int) {
        if (count <= 0) return
        if (!hasPermission(context)) return

        createChannel(context)

        // Tapping the notification opens the app.
        val launchIntent = context.packageManager.getLaunchIntentForPackage(context.packageName)
        val pendingIntent = launchIntent?.let {
            PendingIntent.getActivity(
                context,
                0,
                it,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle("CreditBook reminder")
            .setContentText(messageFor(count))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        try {
            NotificationManagerCompat.from(context).notify(NOTIFICATION_ID, notification)
        } catch (e: SecurityException) {
            // Permission was taken away between our check and now. Do nothing.
        }
    }
}