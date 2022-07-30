package com.pratama.dany.keyaccess

import android.Manifest
import android.app.Dialog
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.hardware.fingerprint.FingerprintManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.CountDownTimer
import android.os.Handler
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyPermanentlyInvalidatedException
import android.security.keystore.KeyProperties
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_splash.*
import pl.droidsonroids.gif.GifImageView
import java.io.IOException
import java.security.*
import java.security.cert.CertificateException
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.NoSuchPaddingException
import javax.crypto.SecretKey

class SplashActivity : AppCompatActivity() {

    lateinit var caption : TextView
    lateinit var mdialog : Dialog

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    private var fingerprintManager: FingerprintManager? = null
    private var keyguardManager: KeyguardManager? = null
    private var keyStore: KeyStore? = null
    private var keyGenerator: KeyGenerator? = null
    private var cipher: Cipher? = null
    private var cryptoObject: FingerprintManager.CryptoObject? = null

    private val KEY_NAME = "key_access"
    var count : Int = 0

    private lateinit var idUser : String
    private lateinit var idHome : String

    private val sqliteHelper = SqliteHelper(this)

    override fun onCreate(savedInstanceState: Bundle?) {

        val result = sqliteHelper.getStatusNightMode(21) as String
        if(result.equals("T")){
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.SplashTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Glide.with(this).load(R.drawable.logokey)
            .apply(RequestOptions().override(2000, 2000))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(logoKey)

        init()
        setUpFeatureEnable()

    }

    private fun init(){

        if(auth.currentUser != null){

            val status = sqliteHelper.getStatusFingerprint(21)

            if(status.equals("T")){

                Handler().postDelayed({

                    showPopupAuth()
                    startFingerprint()

                }, 1000)

            } else {

                db.collection("USER").whereEqualTo("phone", auth.currentUser!!
                    .phoneNumber.toString()).get().addOnSuccessListener { querySnapshot ->

                    if(querySnapshot.isEmpty){

                        Handler().postDelayed({

                            auth.signOut()
                            val intent = Intent(this, SignInActivity::class.java)
                            startActivity(intent)
                            finish()

                        }, 1000)

                    } else {

                        for(doc in querySnapshot){

                            idUser = doc.id

                            if(doc.get("home") != null){

                                idHome = doc.getString("home").toString()

                                Handler().postDelayed({

                                    val intent = Intent(this, HomeActivity::class.java)
                                    intent.putExtra("idUser", idUser)
                                    intent.putExtra("idHome", idHome)
                                    startActivity(intent)
                                    finish()

                                }, 1000)

                            } else {

                                Handler().postDelayed({

                                    val intent = Intent(this, AddHomeActivity::class.java)
                                    intent.putExtra("idUser", idUser)
                                    startActivity(intent)
                                    finish()

                                }, 1000)

                            }

                        }

                    }

                }

            }

        } else {

            Handler().postDelayed({

                val intent = Intent(this, SignInActivity::class.java)
                startActivity(intent)
                finish()

            }, 2000)

        }

    }

    private fun setUpFeatureEnable(){
        val notif = sqliteHelper.getStatusNotification(21)
        if(notif.equals("-")){
            sqliteHelper.insertStatusNotification()
        }
    }

    fun startFingerprint(){

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            if (getManagers()) {

                generateKey()

                if(cipherInit()){

                    cipher?.let {

                        cryptoObject = FingerprintManager.CryptoObject(it)

                    }

                    val helper = FingerprintHandler(this)

                    if (fingerprintManager != null && cryptoObject != null) {

                        helper.startAuth(fingerprintManager!!, cryptoObject!!)

                    }

                }

            }

        }

    }

    private fun showPopupAuth() {

        val passcode : Button
        val cancel : Button
        mdialog = Dialog(this)
        mdialog.setCancelable(false)
        mdialog.setContentView(R.layout.auth_layout)
        passcode = mdialog.findViewById(R.id.passcode)
        cancel = mdialog.findViewById(R.id.cancel)
        caption = mdialog.findViewById(R.id.caption)

        cancel.setOnClickListener {

            mdialog.dismiss()
            Handler().postDelayed({

                onBackPressed()

            }, 300)

        }

        mdialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mdialog.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation
        mdialog.show()

    }

    private fun getManagers(): Boolean {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            keyguardManager = getSystemService(Context.KEYGUARD_SERVICE)
                    as KeyguardManager
            fingerprintManager = getSystemService(Context.FINGERPRINT_SERVICE)
                    as FingerprintManager

            if (keyguardManager?.isKeyguardSecure == false) {

                Toast.makeText(this,
                    "Lock screen security not enabled in Settings",
                    Toast.LENGTH_LONG).show()
                return false
            }

            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.USE_FINGERPRINT) !=
                PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this,
                    "Fingerprint authentication permission not enabled",
                    Toast.LENGTH_LONG).show()

                return false
            }

            if (fingerprintManager?.hasEnrolledFingerprints() == false) {
                Toast.makeText(this,
                    "Register at least one fingerprint in Settings",
                    Toast.LENGTH_LONG).show()
                return false
            }

        }

        return true

    }

    private fun generateKey() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            try {
                keyStore = KeyStore.getInstance("AndroidKeyStore")
            } catch (e: Exception) {
                e.printStackTrace()
            }

            try {
                keyGenerator = KeyGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES,
                    "AndroidKeyStore")
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(
                    "Failed to get KeyGenerator instance", e)
            } catch (e: NoSuchProviderException) {
                throw RuntimeException("Failed to get KeyGenerator instance", e)
            }

            try {
                keyStore?.load(null)
                keyGenerator?.init(
                    KeyGenParameterSpec.Builder(KEY_NAME,
                        KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                        .setUserAuthenticationRequired(true)
                        .setEncryptionPaddings(
                            KeyProperties.ENCRYPTION_PADDING_PKCS7)
                        .build())
                keyGenerator?.generateKey()
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException(e)
            } catch (e: InvalidAlgorithmParameterException) {
                throw RuntimeException(e)
            } catch (e: CertificateException) {
                throw RuntimeException(e)
            } catch (e: IOException) {
                throw RuntimeException(e)
            }

        }

    }

    private fun cipherInit(): Boolean {

        var hasil : Boolean = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            try {
                cipher = Cipher.getInstance(
                    KeyProperties.KEY_ALGORITHM_AES + "/"
                            + KeyProperties.BLOCK_MODE_CBC + "/"
                            + KeyProperties.ENCRYPTION_PADDING_PKCS7)
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException("Failed to get Cipher", e)
            } catch (e: NoSuchPaddingException) {
                throw RuntimeException("Failed to get Cipher", e)
            }

            try {
                keyStore?.load(null)
                val key = keyStore?.getKey(KEY_NAME, null) as SecretKey
                cipher?.init(Cipher.ENCRYPT_MODE, key)
                hasil = true
            } catch (e: KeyPermanentlyInvalidatedException) {
                hasil = false
            } catch (e: KeyStoreException) {
                throw RuntimeException("Failed to init Cipher", e)
            } catch (e: CertificateException) {
                throw RuntimeException("Failed to init Cipher", e)
            } catch (e: UnrecoverableKeyException) {
                throw RuntimeException("Failed to init Cipher", e)
            } catch (e: IOException) {
                throw RuntimeException("Failed to init Cipher", e)
            } catch (e: NoSuchAlgorithmException) {
                throw RuntimeException("Failed to init Cipher", e)
            } catch (e: InvalidKeyException) {
                throw RuntimeException("Failed to init Cipher", e)
            }

        }

        return hasil

    }

    override fun onRestart() {

        super.onRestart()
        startFingerprint()

    }

}
