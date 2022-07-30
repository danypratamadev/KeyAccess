package com.pratama.dany.keyaccess

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.database.FirebaseDatabase
import com.mikhaellopez.circularimageview.CircularImageView
import kotlinx.android.synthetic.main.activity_setting.*


class SettingActivity : AppCompatActivity() {

    private val TAG : String = "SettingsActivity"
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val dbf : FirebaseDatabase = FirebaseDatabase.getInstance()

    private lateinit var idUser : String
    private lateinit var idHome : String
    private lateinit var nameHome : String
    private lateinit var homeAddress : String
    private lateinit var invitCode : String
    private lateinit var accessUser : String
    private lateinit var url_txt : String

    private lateinit var sqliteHelper : SqliteHelper

    override fun onCreate(savedInstanceState: Bundle?) {

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setting)

        init()
        onClickListener()

    }

    private fun init(){

        sqliteHelper = SqliteHelper(this)

        idUser = intent.getStringExtra("idUser")
        idHome = intent.getStringExtra("idHome")
        nameHome = intent.getStringExtra("nameHome")
        homeAddress = intent.getStringExtra("homeAddress")
        invitCode = intent.getStringExtra("invitation")
        accessUser = intent.getStringExtra("accessUser")

        if(accessUser.equals("User")){

            home_txt.visibility = View.GONE
            home.visibility = View.GONE

        }

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            dark_switch.isChecked = true
        }

        val finger_status = sqliteHelper.getStatusFingerprint(21) as String
        if(finger_status.equals("T")){
            finger_switch.isChecked = true
        }

        val close_status = sqliteHelper.getStatusAutomatically(21) as String
        if(close_status.equals("T")){
            close_switch.isChecked = true
        }

        val notif_status = sqliteHelper.getStatusNotification(21) as String
        if(notif_status.equals("T")){
            notif_switch.isChecked = true
        }

        homeName.text = nameHome

        db.collection("USER").document(idUser).addSnapshotListener{ result, e ->

            if (e != null) {
                Log.w(TAG, "listen:error", e)
                return@addSnapshotListener
            }

            if(result != null && result.exists()){

                name.text = result.getString("name")
                access.text = result.getString("access")
                url_txt = result.getString("picture").toString()

                Glide.with(this).load(url_txt).apply(
                    RequestOptions()
                        .override(100, 100))
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .into(imgUser)

            }

        }

    }

    private fun onClickListener(){

        back.setOnClickListener {

            onBackPressed()

        }

        profil_cap.setOnClickListener {

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this,
                imgUser, ViewCompat.getTransitionName(imgUser).toString())
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("url", url_txt)
            startActivity(intent, options.toBundle())

        }

        home.setOnClickListener {

            val intent = Intent(this, HomeDetailsActivity::class.java)
            intent.putExtra("homeName", nameHome)
            intent.putExtra("homeAddress", homeAddress)
            intent.putExtra("idHome", idHome)
            intent.putExtra("invitation", invitCode)
            startActivity(intent)

        }

        dark_switch.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked){

                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)

                val result = sqliteHelper.insertStatusNightMode(21, "T") as Boolean

                if(result){
                    Toast.makeText(this, "Night mode is enable", Toast.LENGTH_SHORT).show()
                } else {
                    sqliteHelper.updateStatusNightMode(21, "T")
                    Toast.makeText(this, "Night mode is enable", Toast.LENGTH_SHORT).show()
                }

                //restartApps()

            } else {

                //AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)

                val result = sqliteHelper.insertStatusNightMode(21, "F") as Boolean

                if(result){
                    Toast.makeText(this, "Night mode is disable", Toast.LENGTH_SHORT).show()
                } else {
                    sqliteHelper.updateStatusNightMode(21, "F")
                    Toast.makeText(this, "Night mode is disable", Toast.LENGTH_SHORT).show()
                }

                //restartApps()

            }

        }

        finger_switch.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked){

                val result = sqliteHelper.insertStatusFingerprint(21, "T") as Boolean

                if(result){
                    Toast.makeText(this, "Fingerprint authentication is enable", Toast.LENGTH_SHORT).show()
                } else {
                    sqliteHelper.updateStatusFingerprint(21, "T")
                    Toast.makeText(this, "Fingerprint Authentication is enable", Toast.LENGTH_SHORT).show()
                }

            } else {

                val result = sqliteHelper.insertStatusFingerprint(21, "F") as Boolean

                if(result){
                    Toast.makeText(this, "Fingerprint Authentication is disable", Toast.LENGTH_SHORT).show()
                } else {
                    sqliteHelper.updateStatusFingerprint(21, "F")
                    Toast.makeText(this, "Fingerprint Authentication is disable", Toast.LENGTH_SHORT).show()
                }

            }

        }

        close_switch.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked){

                val result = sqliteHelper.insertStatusAutomatically(21, "T") as Boolean

                if(result){
                    Toast.makeText(this, "Close automatically is enable", Toast.LENGTH_SHORT).show()
                } else {
                    sqliteHelper.updateStatusAutomatically(21, "T")
                    Toast.makeText(this, "Close automatically is enable", Toast.LENGTH_SHORT).show()
                }

                val auto = hashMapOf<String, Any>(
                    "auto" to true
                )

                db.collection("HOME").document(idHome).update(auto).addOnCompleteListener { task ->
                    if(task.isSuccessful){

                        db.collection("HOME").document(idHome).collection("DOOR").get().addOnCompleteListener { task ->

                            if(task.isSuccessful){

                                for(doc in task.result!!){

                                    db.collection("HOME").document(idHome).collection("DOOR").document(doc.id).update(auto)
                                    val doorStatus = dbf.getReference(doc.getString("key") + "/auto")
                                    doorStatus.setValue(true)

                                }

                            }

                        }

                    }
                }

            } else {

                val result = sqliteHelper.insertStatusAutomatically(21, "F") as Boolean

                if(result){
                    Toast.makeText(this, "Close automatically is disable", Toast.LENGTH_SHORT).show()
                } else {
                    sqliteHelper.updateStatusAutomatically(21, "F")
                    Toast.makeText(this, "Close automatically is disable", Toast.LENGTH_SHORT).show()
                }

                val auto = hashMapOf<String, Any>(
                    "auto" to false
                )

                db.collection("HOME").document(idHome).update(auto).addOnCompleteListener { task ->
                    if(task.isSuccessful){

                        db.collection("HOME").document(idHome).collection("DOOR").get().addOnCompleteListener { task ->

                            if(task.isSuccessful){

                                for(doc in task.result!!){

                                    db.collection("HOME").document(idHome).collection("DOOR").document(doc.id).update(auto)
                                    val doorStatus = dbf.getReference(doc.getString("key") + "/auto")
                                    doorStatus.setValue(false)

                                }

                            }

                        }

                    }
                }

            }

        }

        profil.setOnClickListener {

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imgUser, ViewCompat.getTransitionName(imgUser).toString())
            val intent = Intent(this, EditProfileActivity::class.java)
            intent.putExtra("url", url_txt)
            startActivity(intent, options.toBundle())

        }

        passcode.setOnClickListener {

            val intent = Intent(this, ChangePasscodeActivity::class.java)
            startActivity(intent)

        }

        notif_switch.setOnCheckedChangeListener { buttonView, isChecked ->

            if(isChecked){

                val result = sqliteHelper.insertStatusNotification(21, "T") as Boolean

                if(result){
                    Toast.makeText(this, "Notification is enable", Toast.LENGTH_SHORT).show()
                } else {
                    sqliteHelper.updateStatusNotification(21, "T")
                    Toast.makeText(this, "Notification is enable", Toast.LENGTH_SHORT).show()
                }

            } else {

                val result = sqliteHelper.insertStatusNotification(21, "F") as Boolean

                if(result){
                    Toast.makeText(this, "Notification is disable", Toast.LENGTH_SHORT).show()
                } else {
                    sqliteHelper.updateStatusNotification(21, "F")
                    Toast.makeText(this, "Notification is disable", Toast.LENGTH_SHORT).show()
                }

            }

        }

        signout.setOnClickListener {

            showPopupWarning()

        }

    }

    private fun showPopupWarning() {

        val logout : Button
        val cancel : Button
        val mdialog : Dialog
        mdialog = Dialog(this)
        mdialog.setCancelable(false)
        mdialog.setContentView(R.layout.signout_layout)
        logout = mdialog.findViewById(R.id.logout)
        cancel = mdialog.findViewById(R.id.cancel)

        cancel.setOnClickListener {

            mdialog.dismiss()

        }

        logout.setOnClickListener {

            auth.signOut()

            val intent = Intent(this, SignInActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)

        }

        mdialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mdialog.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation
        mdialog.show()

    }

    private fun restartApps(){

        val intent = Intent(applicationContext, SettingActivity::class.java)
        startActivity(intent)
        overridePendingTransition(R.anim.abc_fade_in, R.anim.abc_fade_out)
        finish()

    }

}
