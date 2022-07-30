@file:Suppress("SyntaxError")

package com.pratama.dany.keyaccess

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import android.os.CancellationSignal
import android.os.CountDownTimer
import android.os.Handler
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat.startActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@RequiresApi(Build.VERSION_CODES.M)
class FingerprintHandler(private val appContext: SplashActivity) : FingerprintManager.AuthenticationCallback() {

    private var cancellationSignal: CancellationSignal? = null
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private lateinit var idUser : String
    private lateinit var idHome : String

    fun startAuth(manager: FingerprintManager, cryptoObject: FingerprintManager.CryptoObject){

        cancellationSignal = CancellationSignal()

        db.collection("USER").whereEqualTo("phone", auth.currentUser?.phoneNumber).get().addOnSuccessListener { result ->

            for (doc in result){

                idUser = doc.id
                idHome = doc.getString("home").toString()

                if (ActivityCompat.checkSelfPermission(appContext, Manifest.permission.USE_FINGERPRINT) != PackageManager.PERMISSION_GRANTED){

                    return@addOnSuccessListener

                }

                try {

                    manager.authenticate(cryptoObject, cancellationSignal, 0, this, null)

                } catch (sce : SecurityException){
                    Log.e("Fingerprint", sce.toString())
                }

            }

        }

    }

    override fun onAuthenticationError(errMsgId: Int, errString: CharSequence) {
        //Toast.makeText(appContext,"Authentication error\n" + errString, Toast.LENGTH_LONG).show()
        val timer = object : CountDownTimer(31000, 1000) {
            override fun onFinish() {

                appContext.caption.text = "Touch sensor"
                appContext.caption.setTextColor(appContext.resources.getColor(R.color.Gray700))
                appContext.startFingerprint()

            }

            override fun onTick(millisUntilFinished: Long) {

                appContext.caption.text = "5 incorrect attempts to verify fingerprint.\nTry again in " + millisUntilFinished / 1000 + " seconds"

            }


        }
        timer.start()
        //appContext.caption.text = "Too many attempts."
        appContext.caption.setTextColor(appContext.resources.getColor(R.color.Orange700))
    }

    override fun onAuthenticationHelp(helpMsgId: Int, helpString: CharSequence) {
        //Toast.makeText(appContext,"Authentication help\n" + helpString, Toast.LENGTH_LONG).show()
        appContext.caption.text = helpString.toString()+" Try again"
        appContext.caption.setTextColor(appContext.resources.getColor(R.color.Orange600))
    }

    override fun onAuthenticationFailed() {
        //Toast.makeText(appContext,"Authentication failed.", Toast.LENGTH_LONG).show()
        appContext.count = appContext.count + 1
        appContext.caption.text = "Fingerprint not recognized, "+appContext.count.toString()+" attempt failed. Try again"
        appContext.caption.setTextColor(appContext.resources.getColor(R.color.Red600))
    }

    override fun onAuthenticationSucceeded(result: FingerprintManager.AuthenticationResult?) {
        //Toast.makeText(appContext, "Authentication succeeded.", Toast.LENGTH_SHORT).show()
        appContext.caption.text = "Fingerprint recognized."
        appContext.caption.setTextColor(appContext.resources.getColor(R.color.Green600))

        Handler().postDelayed({

            appContext.mdialog.dismiss()

        }, 300)

        val intent = Intent(appContext, HomeActivity::class.java)
        intent.putExtra("idUser", idUser)
        intent.putExtra("idHome", idHome)
        appContext.startActivity(intent)
        appContext.finish()
    }

}