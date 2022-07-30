package com.pratama.dany.keyaccess

import android.app.*
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.os.IBinder
import android.util.Log
import android.widget.Toast
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

class NotificationAlaramService : Service() {

    private var idHome : String = "2112"
    private var idUser : String = "2112"

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val dbr : FirebaseDatabase = FirebaseDatabase.getInstance()
    private val dbref : DatabaseReference = dbr.reference

    private fun startAlarmListener(){

        Thread(Runnable {

            db.collection("HOME").document(idHome).collection("DOOR").get().addOnCompleteListener { task ->

                if(task.isSuccessful){

                    for(doc in task.result!!){

                        val key = doc.getString("key").toString()
                        val nama = doc.getString("name").toString()
                        dbref.child(key).child("door").addValueEventListener(object: ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                Log.e("DATABASE", p0.message)
                            }

                            override fun onDataChange(p0: DataSnapshot) {

                                if(p0.exists()){
                                    val door = p0.getValue(Boolean::class.java)
                                    if(!door!!){
                                        try {
                                            val intent = Intent()
                                            intent.setClassName("com.pratama.dany.keyaccess",
                                                "com.pratama.dany.keyaccess.AlarmActivity")
                                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                                            intent.putExtra("nama", nama)
                                            startActivity(intent)
                                        } catch (e: PackageManager.NameNotFoundException) {
                                            Log.e("ERROR", e.message!!)
                                        }

                                    }

                                }

                            }

                        })

                        dbref.child(key).child("savedo").addValueEventListener(object: ValueEventListener {
                            override fun onCancelled(p0: DatabaseError) {
                                Log.e("DATABASE", p0.message)
                            }

                            override fun onDataChange(p0: DataSnapshot) {
                                if(p0.exists()){
                                    val save = p0.getValue(Boolean::class.java)
                                    if(!save!!){

                                        val calendar = Calendar.getInstance()
                                        val monthYearFormat = SimpleDateFormat("MMM-yyyy")
                                        val timeFormat = SimpleDateFormat("HH:mm:ss")
                                        val monthYear = monthYearFormat.format(calendar.time)
                                        val timeDate = timeFormat.format(calendar.time)

                                        try {
                                            val history = hashMapOf(
                                                "time" to timeDate,
                                                "door" to nama
                                            )

                                            db.collection("HOME").document(idHome)
                                                .collection("HISTORY").document(monthYear).collection("ALARM")
                                                .add(history).addOnCompleteListener { task ->
                                                    if(task.isSuccessful){
                                                        val saveStatus = dbref.child(key).child("savedo")
                                                        saveStatus.setValue(true)
                                                    }
                                                }

                                        } catch (e: PackageManager.NameNotFoundException) {
                                            Log.e("ERROR", e.message!!)
                                        }
                                    }
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
        idUser = intent?.getStringExtra("idUser").toString()
        startAlarmListener()
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
    }

}