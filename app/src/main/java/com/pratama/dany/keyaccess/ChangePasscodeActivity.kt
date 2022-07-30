package com.pratama.dany.keyaccess

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.goodiebag.pinview.Pinview
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class ChangePasscodeActivity : AppCompatActivity() {

    private val TAG : String = "ChangePasscodeActivity"
    private lateinit var back : ImageButton
    private lateinit var passcode : Pinview
    private lateinit var verify : ProgressBar
    private lateinit var caption : TextView
    private lateinit var caption2 : TextView
    private lateinit var reset : TextView

    private lateinit var idUser : String
    private lateinit var old_passcode_txt : String
    private lateinit var new_passcode_txt : String

    private var operation : Int = 0

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_passcode)

        init()

    }

    private fun init(){

        back = findViewById(R.id.back)
        passcode = findViewById(R.id.passcode)
        verify = findViewById(R.id.verify)
        caption = findViewById(R.id.caption)
        caption2 = findViewById(R.id.caption2)
        reset = findViewById(R.id.reset)

        reset.visibility = View.INVISIBLE
        verify.visibility = View.INVISIBLE

        passcode.requestFocus()

        db.collection("USER").whereEqualTo("phone", auth.currentUser?.phoneNumber).addSnapshotListener { result, e ->

            if (e != null) {
                Log.w(TAG, "listen:error", e)
                return@addSnapshotListener
            }

            for(doc in result!!){

                idUser = doc.id
                old_passcode_txt = doc.getString("passcode").toString()

            }

        }

        passcode.setPinViewEventListener(object : Pinview.PinViewEventListener {

            override fun onDataEntered(pinview: Pinview?, fromUser: Boolean) {

                if(operation == 0){

                    passcode.alpha = 0.5F
                    passcode.isEnabled = false
                    verify.visibility = View.VISIBLE

                    if(pinview?.value.toString().equals(old_passcode_txt)){

                        Handler().postDelayed({

                            verify.visibility = View.INVISIBLE
                            passcode.alpha = 1F
                            passcode.isEnabled = true

                            passcode.clearFocus()
                            passcode.requestFocus()
                            for (i in 0 until passcode.childCount) {
                                val child = passcode.getChildAt(i) as EditText
                                child.setText("")
                            }

                            caption.text = "Enter new Passcode"
                            caption2.text = "A passcode protects your data and is used \nto unlock your home."
                            operation = 1

                        }, 500)

                    } else {

                        Handler().postDelayed({

                            verify.visibility = View.INVISIBLE
                            passcode.alpha = 1F
                            passcode.isEnabled = true

                            passcode.clearFocus()
                            passcode.requestFocus()
                            for (i in 0 until passcode.childCount) {
                                val child = passcode.getChildAt(i) as EditText
                                child.setText("")
                            }

                            operation = 0

                            Toast.makeText(this@ChangePasscodeActivity, "Wrong passcode! Try again", Toast.LENGTH_SHORT).show()

                        }, 500)

                    }

                } else if(operation == 1){

                    new_passcode_txt= pinview?.value.toString()
                    passcode.clearFocus()
                    passcode.requestFocus()

                    for (i in 0 until passcode.childCount) {
                        val child = passcode.getChildAt(i) as EditText
                        child.setText("")
                    }

                    val animation = AnimationUtils.loadAnimation(this@ChangePasscodeActivity, R.anim.fade_up_from_position2)

                    reset.visibility = View.VISIBLE
                    reset.startAnimation(animation)

                    operation = 2

                } else {

                    passcode.alpha = 0.5F
                    passcode.isEnabled = false
                    verify.visibility = View.VISIBLE

                    if(pinview?.value.toString().equals(new_passcode_txt)){

                        reset.alpha = 0.8F
                        reset.isEnabled = false
                        passcode.clearFocus()
                        val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        keyboard.hideSoftInputFromWindow(passcode.windowToken, 0)

                        val user = hashMapOf<String, Any>(

                            "passcode" to new_passcode_txt

                        )

                        db.collection("USER").document(idUser).update(user).addOnSuccessListener { e ->

                            Toast.makeText(this@ChangePasscodeActivity, "Change passcode success", Toast.LENGTH_SHORT).show()

                            verify.visibility = View.INVISIBLE
                            passcode.alpha = 1F
                            passcode.isEnabled = true
                            reset.alpha = 1F
                            reset.isEnabled = true

                            Handler().postDelayed({

                                onBackPressed()

                            }, 300)

                        }

                    } else {

                        Handler().postDelayed({

                            verify.visibility = View.INVISIBLE
                            passcode.alpha = 1F
                            passcode.isEnabled = true

                            passcode.clearFocus()
                            passcode.requestFocus()
                            for (i in 0 until passcode.childCount) {
                                val child = passcode.getChildAt(i) as EditText
                                child.setText("")
                            }

                            Toast.makeText(this@ChangePasscodeActivity, "Passcode don't match!", Toast.LENGTH_SHORT).show()

                        }, 500)
                    }

                }

            }


        })

        reset.setOnClickListener {

            for (i in 0 until passcode.childCount) {

                val child = passcode.getChildAt(i) as EditText
                child.setText("")

            }

            val animation = AnimationUtils.loadAnimation(this@ChangePasscodeActivity, R.anim.fade_down_from_position2)
            reset.startAnimation(animation)
            reset.visibility = View.INVISIBLE

            operation = 1

        }

        back.setOnClickListener {

            onBackPressed()
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(passcode.windowToken, 0)

        }

    }

}
