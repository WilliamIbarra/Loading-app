package com.udacity

import android.app.DownloadManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    private var url = ""
    private var fileName = ""
    private var description = ""

    private var notificationId = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))



        createNotificationChannel()

        glide.setOnClickListener {
            url = GLIDE_URL
            fileName = getString(R.string.glide)
            description = getString(R.string.txt_glide)
        }

        retrofit.setOnClickListener {
            url = RETROFIT_URL
            fileName = getString(R.string.retrofit)
            description = getString(R.string.txt_retrofit)
        }

        load_app.setOnClickListener {
            url = URL
            fileName = getString(R.string.load_app)
            description = getString(R.string.txt_load_app)
        }

        custom_button.setOnClickListener {
            if (url.isNotEmpty()) {
                if (!custom_button.isDownloading()) {
                    download()
                    custom_button.updateStatus()
                    custom_button.updatePosition()
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.txt_please_wait),
                        Toast.LENGTH_LONG
                    ).show()
                }
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.txt_select_a_file),
                    Toast.LENGTH_LONG
                )
                    .show()
            }
        }
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (downloadID == id) {
                //Change the button status
                custom_button.updateStatus()

                // Create a notification

                makeNotification()

            }
        }
    }

    private fun makeNotification() {

        notificationId++

        // Create an explicit intent for an Activity in your app
        val intent = Intent(this, DetailActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        intent.putExtra("name", fileName)
        intent.putExtra("status", SUCCESS)
        val pendingIntent: PendingIntent = PendingIntent.getActivity(this, 0, intent, 0)

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_assistant_black_24dp)
            .setContentTitle(getString(R.string.txt_notification_title))
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(fileName))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            // Set the intent that will fire when the user taps the notification
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)


        with(NotificationManagerCompat.from(this)) {
            // notificationId is a unique int for each notification that you must define
            notify(notificationId, builder.build())
        }

    }

    private fun createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = CHANNEL_ID
            val descriptionText = getString(R.string.txt_channel_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            // Register the channel with the system
            val notificationManager: NotificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun download() {
        val request =
            DownloadManager.Request(Uri.parse(url))
                .setTitle(fileName)
                .setDescription(description)
                .setRequiresCharging(false)
                .setAllowedOverMetered(true)
                .setAllowedOverRoaming(true)

        val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
        downloadID =
            downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val GLIDE_URL =
            "https://github.com/bumptech/glide"
        private const val RETROFIT_URL =
            "https://github.com/square/retrofit"
        private const val URL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val CHANNEL_ID = "channelId"

        private const val SUCCESS = "Success"
    }

}
