package com.pratama.dany.keyaccess

import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class WifiService : Service() {

    private var idHome : String = "2112"

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val dbr : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val dbref : DatabaseReference = dbr.reference

    private fun startNotificationListener(){

        Thread(Runnable {

            db.collection("HOME").document(idHome).collection("DOOR").get().addOnCompleteListener { task ->

                if(task.isSuccessful){

                    for(doc in task.result!!){

                        val key = doc.getString("key").toString()
                        val nama = doc.getString("name").toString()
                        dbref.child(key).child("seconds").addValueEventListener(object: ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                Log.e("DATABASE", p0.message)
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if(p0.exists()){
                                    val detik = p0.getValue(Int::class.java)
                                    dbref.child(key).child("savesec").addListenerForSingleValueEvent(object: ValueEventListener {
                                        override fun onCancelled(p0: DatabaseError) {
                                            Log.e("DATABASE", p0.message)
                                        }

                                        override fun onDataChange(p0: DataSnapshot) {
                                            if(p0.exists()){
                                                val save = p0.getValue(Boolean::class.java)
                                                if(!save!!){
                                                    if(detik != 0){
                                                        val calendar = Calendar.getInstance()
                                                        val monthYearFormat = SimpleDateFormat("MMM-yyyy")
                                                        val timeFormat = SimpleDateFormat("HH:mm:ss")
                                                        val monthYear = monthYearFormat.format(calendar.time)
                                                        val timeEnd = timeFormat.format(calendar.time)

                                                        calendar.add(Calendar.SECOND, (-1 * detik!!))
                                                        val timeStrart = timeFormat.format(calendar.time)

                                                        try {
                                                            val history = hashMapOf(
                                                                "startlost" to timeStrart,
                                                                "endlost" to timeEnd,
                                                                "door" to nama
                                                            )

                                                            db.collection("HOME").document(idHome)
                                                                .collection("HISTORY").document(monthYear).collection("NOTIF")
                                                                .add(history).addOnCompleteListener { task ->
                                                                    if(task.isSuccessful){
                                                                        val saveStatus = dbref.child(key).child("savesec")
                                                                        saveStatus.setValue(true)
                                                                        val secTime = dbref.child(key).child("seconds")
                                                                        secTime.setValue(1)
                                                                    }
                                                                }

                                                        } catch (e: PackageManager.NameNotFoundException) {
                                                            Log.e("ERROR", e.message!!)
                                                        }
                                                    } else {

                                                        val calendar = Calendar.getInstance()
                                                        val monthYearFormat = SimpleDateFormat("MMM-yyyy")
                                                        val timeFormat = SimpleDateFormat("HH:mm:ss")
                                                        val monthYear = monthYearFormat.format(calendar.time)
                                                        val timeEnd = timeFormat.format(calendar.time)

                                                        try {
                                                            val history = hashMapOf(
                                                                "startlost" to "null",
                                                                "endlost" to timeEnd,
                                                                "door" to nama
                                                            )

                                                            db.collection("HOME").document(idHome)
                                                                .collection("HISTORY").document(monthYear).collection("NOTIF")
                                                                .add(history).addOnCompleteListener { task ->
                                                                    if(task.isSuccessful){
                                                                        val saveStatus = dbref.child(key).child("savesec")
                                                                        saveStatus.setValue(true)
                                                                        val secTime = dbref.child(key).child("seconds")
                                                                        secTime.setValue(1)
                                                                    }
                                                                }

                                                        } catch (e: PackageManager.NameNotFoundException) {
                                                            Log.e("ERROR", e.message!!)
                                                        }
                                                    }
                                                }
                                            }
                                        }


                                    })
                                }
                            }


                        })

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
        startNotificationListener()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}