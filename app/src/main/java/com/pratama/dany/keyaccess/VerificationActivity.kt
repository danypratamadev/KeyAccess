package com.pratama.dany.keyaccess

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.goodiebag.pinview.Pinview
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit
import android.os.CountDownTimer
import android.os.Handler
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_passcode.*
import kotlinx.android.synthetic.main.activity_passcode.back
import kotlinx.android.synthetic.main.activity_passcode.verify
import kotlinx.android.synthetic.main.activity_verification.*


class VerificationActivity : AppCompatActivity() {

    private lateinit var phone_txt : String
    private lateinit var codeId : String
    private lateinit var idUser : String
    private lateinit var idHome : String

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_verification)

        init()

        pinCode.setPinViewEventListener(object : Pinview.PinViewEventListener {

            override fun onDataEntered(pinview: Pinview?, fromUser: Boolean) {

                pinCode.alpha = 0.5F
                pinCode.isEnabled = false
                pinCode.clearFocus()
                hideKeyboard(pinCode)
                verify.visibility = View.VISIBLE

                verifyVerificationCode(pinview?.value.toString())

            }


        })

        back.setOnClickListener {

            onBackPressed()
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(pinCode.windowToken, 0)

        }

    }

    private fun init(){

        phone_txt = intent.getStringExtra("phone")

        number.text = phone_txt
        pinCode.requestFocus()
        verify.visibility = View.INVISIBLE

        startVerification(phone_txt)

        resend.setOnClickListener {

            load.visibility = View.VISIBLE
            time.text = "Sending verification code..."
            time.setTextColor(resources.getColor(R.color.TweetIconDisable))
            startVerification(phone_txt)

        }

    }

    private fun startVerification(number : String){

        resend.isEnabled = false

        PhoneAuthProvider.getInstance().verifyPhoneNumber(number, 60, TimeUnit.SECONDS, this,
            object : PhoneAuthProvider.OnVerificationStateChangedCallbacks(){

                override fun onVerificationCompleted(p0: PhoneAuthCredential) {

                    val code = p0?.smsCode

                    if (code != null) {

                        pinCode.value = code
                        pinCode.clearFocus()

                    }

                }

                override fun onVerificationFailed(p0: FirebaseException) {

                    Toast.makeText(this@VerificationActivity, "Please Verification Your Phone Number!", Toast.LENGTH_SHORT).show()

                }

                override fun onCodeSent(p0: String, p1: PhoneAuthProvider.ForceResendingToken) {

                    load.visibility = View.GONE

                    Toast.makeText(this@VerificationActivity, "Verification code has been sent", Toast.LENGTH_SHORT).show()

                    if (p0 != null) {
                        codeId = p0
                    }

                    val timer = object : CountDownTimer(59000, 1000) {

                        override fun onFinish() {

                            resend.isEnabled = true
                            time.text = "Resend code"
                            time.setTextColor(resources.getColor(R.color.TweetIconEnable))

                        }

                        override fun onTick(millisUntilFinished: Long) {

                            time.text = "Resend code in " + ((millisUntilFinished / 1000) + 1) + " seconds"

                        }


                    }
                    timer.start()

                }

            })

    }

    private fun verifyVerificationCode(otp: String) {

        val credential = PhoneAuthProvider.getCredential(codeId, otp)

        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {

        auth.signInWithCredential(credential).addOnCompleteListener(this) { task ->

                if (task.isSuccessful) {

                    db.collection("USER").whereEqualTo("phone", phone_txt).get().addOnSuccessListener { querySnapshot ->

                        if(querySnapshot.isEmpty){

                            val intent = Intent(this, ProfileActivity::class.java)
                            intent.putExtra("phone", phone_txt)
                            startActivity(intent)
                            finish()

                        } else {

                            for(doc in querySnapshot){

                                idUser = doc.id
                                idHome = doc.getString("home").toString()

                            }

                            val intent = Intent(this, HomeActivity::class.java)
                            intent.putExtra("idUser", idUser)
                            intent.putExtra("idHome", idHome)
                            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                            startActivity(intent)

                        }

                    }

                } else {

                    if (task.exception is FirebaseAuthInvalidCredentialsException) {

                        Toast.makeText(this, "Invalid code.", Toast.LENGTH_SHORT).show()

                        pinCode.alpha = 1F
                        pinCode.isEnabled = true
                        verify.visibility = View.INVISIBLE

                        pinCode.clearFocus()
                        pinCode.requestFocus()
                        for (i in 0 until pinCode.childCount) {
                            val child = pinCode.getChildAt(i) as EditText
                            child.setText("")
                        }
                        showKeyboard()

                    }

                }
            }

    }

    private fun showKeyboard(){

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

    }

    private fun hideKeyboard(view: View){

        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)

    }

}
