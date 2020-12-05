package com.udacity

import android.app.DownloadManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var optionChecked: Int = -1
    private val downloadOptions: Map<Int,String> = mapOf(
            0 to GLIDE,
            1 to UDACITY,
            2 to RETROFIT
    )

    private lateinit var notificationManager: NotificationManager
    private lateinit var pendingIntent: PendingIntent
    private lateinit var action: NotificationCompat.Action

    lateinit var downloadManager: DownloadManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        radio_group_download.setOnCheckedChangeListener { group, checkedId ->
            val radioButtonID: Int = checkedId
            val radioButton: RadioButton = group.findViewById(radioButtonID)
            val indexOfChecked: Int = group.indexOfChild(radioButton)

            optionChecked = indexOfChecked
        }
        custom_button.setOnClickListener {
            if(optionChecked == -1) {
                Toast.makeText(this,"Select one option",Toast.LENGTH_LONG).show()
            } else {
                Log.d("MainActivity","Choosed: ${downloadOptions[optionChecked]}")
                download(downloadOptions[optionChecked])
                custom_button.setState(ButtonState.Loading)
            }
        }
        downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
    }

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val query = DownloadManager.Query()
            intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)?.let {
                query.setFilterById(
                        it
                )
            }
            val cursor: Cursor = downloadManager.query(query)
            if(cursor.moveToFirst()) {
                when(cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))) {
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        Log.d("MainActivity","Successfully")
                        cursor.close()
                        custom_button.setState(ButtonState.Completed)
                    }
                    DownloadManager.STATUS_FAILED -> {
                        Log.d("MainActivity","Failed")
                        cursor.close()
                        custom_button.setState(ButtonState.Completed)
                    }
                }
            }
        }
    }

    private fun download(url: String?) {
        val request =
                DownloadManager.Request(Uri.parse(url))
                        .setTitle(getString(R.string.app_name))
                        .setDescription(getString(R.string.app_description))
                        .setRequiresCharging(false)
                        .setAllowedOverMetered(true)
                        .setAllowedOverRoaming(true)

        downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
    }

    companion object {
        private const val UDACITY =
                "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val RETROFIT =
                "https://github.com/square/retrofit/archive/master.zip"
        private const val GLIDE =
                "https://github.com/bumptech/glide/archive/master.zip"
        private const val CHANNEL_ID = "channelId"
    }

}
