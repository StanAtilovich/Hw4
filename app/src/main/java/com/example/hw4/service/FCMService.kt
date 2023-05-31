package com.example.hw4.service

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.example.hw4.R
import com.example.hw4.api.ApiService
import com.example.hw4.auth.AppAuth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random


@AndroidEntryPoint
class FCMService : FirebaseMessagingService() {
    private val action = "action"
    private val content = "content"
    private val channelId = "remote"
    private val gson = Gson()


    @Inject
    lateinit var appAuth: AppAuth
    @Inject
    lateinit var apiService: ApiService

    override fun onCreate() {
        super.onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = getString(R.string.channel_remote_name)
            val descriptionText = getString(R.string.channel_remote_description)
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(channelId, name, importance).apply {
                description = descriptionText
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }


    override fun onMessageReceived(message: RemoteMessage) {
        try {
            val recipientId = gson.fromJson(message.data[content], Info::class.java).recipientId
            val userId = appAuth.data.value?.id

            if (recipientId == null || recipientId == userId) {
                handleInfo(gson.fromJson(message.data[content], Info::class.java))
            } else if (recipientId == 0L && recipientId != userId) {
                appAuth.sendPushToken()
            } else if (recipientId != 0L && recipientId != userId) {
                appAuth.sendPushToken()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            println(gson.toJson(message.data[content]))
        }

    }


    private fun handlePost(content: NewPost) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(
                getString(R.string.notification_user_posted, content.postContent)
            )
            .setContentText(content.postContent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText(content.postContent)
            )
            .build()


        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    private fun handleInfo(info: Info) {
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(info.recipientId.toString())
            .setContentText(info.content)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        NotificationManagerCompat.from(this)
            .notify(Random.nextInt(100_000), notification)
    }

    override fun onNewToken(token: String) {
        appAuth.sendPushToken(token)
    }
}

data class FCMPost(
    val id: Long,
    val author: String,
    val content: String,
)

enum class Action {
    LIKE,
    POST, INFO
}

data class Like(
    val userId: Int,
    val userName: String,
    val postId: Int,
    val postAuthor: String,
)

data class NewPost(
    val postAuthor: String,
    val postContent: String
)

data class Info(
    val recipientId: Long? = null,
    val content: String
)