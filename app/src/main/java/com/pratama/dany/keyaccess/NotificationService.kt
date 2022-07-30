package com.pratama.dany.keyaccess

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.IBinder
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.firebase.firestore.DocumentChange
import com.google.firebase.firestore.FirebaseFirestore


class NotificationService : Service() {

    private lateinit var notificationManager: NotificationManager
    private lateinit var notificationChannel: NotificationChannel
    private lateinit var builder: Notification.Builder
    private val channelId = "com.pratama.dany.keyaccess"
    private val description = "Test notification"

    private var idHome : String = "2112"
    private var idUser : String = "2112"

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    private fun startNotificationListener(){

        Thread(Runnable {

            db.collection("HOME").document(idHome).collection("DOOR").addSnapshotListener{ result, e ->

                if (e != null) {
                    Log.w("Service", "listen:error", e)
                    return@addSnapshotListener
                }

                for (dc in result!!.documentChanges) {
                    when (dc.type) {
                        DocumentChange.Type.MODIFIED -> showNotification(dc.document.getString("name").toString(), dc.document.getBoolean("status")!!)
                    }
                }

            }



        }).start()

    }

    override fun onBind(p0: Intent?): IBinder? {

        return null

    }

    override fun onCreate() {
        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        idHome = intent?.getStringExtra("idHome").toString()
        idUser = intent?.getStringExtra("idUser").toString()
        startNotificationListener()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

    fun showNotification(door : String, status : Boolean){

        var message = door as String

        if(status == false){

            message = message + " is Unlocked"

            notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("idHome", idHome)
            intent.putExtra("idUser", idUser)
            val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel = NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.GREEN
                notificationChannel.enableVibration(true)
                notificationManager.createNotificationChannel(notificationChannel)

                builder = Notification.Builder(this, channelId)
                    .setContentTitle("Attention")
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_warning_red_400_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setCategory(NotificationCompat.CATEGORY_SYSTEM).setGroup("com.pratama.dany.keyaccess").setShowWhen(true)
            } else {

                builder = Notification.Builder(this)
                    .setContentTitle("Attention")
                    .setContentText(message)
                    .setSmallIcon(R.drawable.ic_warning_red_400_24dp)
                    .setLargeIcon(BitmapFactory.decodeResource(this.resources, R.mipmap.ic_launcher))
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setCategory(NotificationCompat.CATEGORY_SYSTEM).setGroup("com.pratama.dany.keyaccess").setShowWhen(true)

            }

            notificationManager.notify(21, builder.build())

        }

    }

}