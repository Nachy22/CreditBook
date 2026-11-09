
| Full Name | Student Number |
|-----------|----------------|
| Nachula Nachamba | 2410157 |
| Martin Muloshi | 2410009 |
| Taizya Nakapende | 2410396|
| Majata Hichimi | 2410181 |
| Katendi Kamenga | 2410432|


[README.md](https://github.com/user-attachments/files/34578888/README.md)
# CreditBook

An Android app that replaces the paper credit notebook used by small traders in Zambia.

## The problem

Small shop owners let customers take goods now and pay later, and record these debts in a paper notebook. Notebooks get lost or torn, traders forget who owes what, customers dispute payments, and due dates pass unnoticed, so the trader loses money.

CreditBook keeps that notebook on the phone. It saves customers, debts and payments; calculates each customer's balance automatically; and sends a daily reminder when payments are overdue. It works fully offline.

## How to build and run

1. Open Android Studio (2026.1 or newer) and choose **File → New → Project from Version Control**.
2. Paste this repository's URL and click **Clone**.
3. Wait for the Gradle sync to finish. If asked, install Android SDK Platform 37.
4. Choose an emulator or phone (Android 7.0 / API 24 or newer) and click **Run ▷**.
5. On Android 13+, tap **Allow** when asked about notifications. The app also works if you tap **Don't allow**.

To test the reminder: add a debt due tomorrow, move the emulator's date forward a few days (Settings → System → Date & time), and reopen the app.

## Screens

| Screen | What it does |
| --- | --- |
| **Customer list** | Every customer and what they owe. Buttons to add a customer and open the Summary. |
| **Add customer** | Name and phone number, with error messages for empty or invalid input. |
| **Customer details** | Balance and full history of debts and payments, with buttons to add either. |
| **Add debt / payment** | Amount, note and due date. Rejects invalid amounts, past dates, and overpayments. |
| **Summary** | Total owed, number of customers who owe, and the top 5 debtors. |

## Research Note

### 1. Room

**What it is:** Android's library for saving structured data in a database on the phone.

**Why we need it:** a trader's records must survive closing the app or restarting the phone. Room stores customers and their debts and payments in two linked tables, and calculates each balance with a query, so it never gets out of date.

**Where we learned it:** _[Room owner to complete]_

**What was difficult:** _[Room owner to complete]_

### 2. WorkManager and notifications

**What it is:** Android's library for background jobs that run even when the app is closed.

**Why we need it:** reminders are only useful when the trader isn't using the app. A daily job checks for overdue debts and shows one notification. It is scheduled only once, and it never crashes if notification permission was refused.

**Where we learned it:** _[WorkManager owner to complete]_

**What was difficult:** _[WorkManager owner to complete]_
What went wrong and how we fixed it.

Our project uses the newest Android Gradle Plugin (9.x), which has Kotlin built in. Older KSP versions refuse to work with it, so the project would not build. We fixed it by using KSP 2.3.11, which supports the built-in Kotlin.
The Room files were first written with the package name com.zut.creditbook, but the project is com.example.creditbook, so nothing could find them. We changed the first line of every file to match.
[Room owner: add anything else that went wrong for you, in your own words]

Where we learned it.

Android Developers: "Schedule tasks with WorkManager" (developer.android.com/develop/background-work/background-tasks/persistent)
Android Developers: "Create a notification" and "Create and manage notification channels"
Android Developers: "Notification runtime permission" (Android 13+)
[WorkManager owner: add any other sources you used, such as codelabs or videos]

