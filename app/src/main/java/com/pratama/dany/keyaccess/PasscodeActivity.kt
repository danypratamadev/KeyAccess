package com.pratama.dany.keyaccess

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.goodiebag.pinview.Pinview
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_passcode.*
import java.io.File


class PasscodeActivity : AppCompatActivity() {

    private var req : Int = 0
    private lateinit var idUser : String
    private lateinit var idHome : String
    private lateinit var passcode_txt : String
    private lateinit var number_txt : String
    private lateinit var name_txt : String
    private lateinit var path_txt : String
    private var url_txt = "null"

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val storage: FirebaseStorage = FirebaseStorage.getInstance()
    private val storageRef = storage.reference

    private lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_passcode)

        init()

        reset.setOnClickListener {

            for (i in 0 until passcode.childCount) {

                val child = passcode.getChildAt(i) as EditText
                child.setText("")

            }

            capPasscode.visibility = View.GONE
            reset.visibility = View.INVISIBLE

            req = 0

        }

        back.setOnClickListener {

            onBackPressed()
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(passcode.windowToken, 0)

        }

    }

    private fun init(){

        number_txt = intent.getStringExtra("phone")
        name_txt = intent.getStringExtra("name")
        path_txt = intent.getStringExtra("path")
        idHome = intent.getStringExtra("idHome")

        reset.visibility = View.INVISIBLE
        verify.visibility = View.INVISIBLE

        passcode.requestFocus()

        passcode.setPinViewEventListener(object : Pinview.PinViewEventListener {

            override fun onDataEntered(pinview: Pinview?, fromUser: Boolean) {

                req = req + 1

                if(req == 1){

                    passcode_txt = pinview?.value.toString()
                    passcode.clearFocus()
                    passcode.requestFocus()

                    for (i in 0 until passcode.childCount) {
                        val child = passcode.getChildAt(i) as EditText
                        child.setText("")
                    }

                    val animation = AnimationUtils.loadAnimation(this@PasscodeActivity,
                        R.anim.fade_up_from_position2)

                    capPasscode.visibility = View.VISIBLE
                    reset.visibility = View.VISIBLE
                    reset.startAnimation(animation)

                } else {

                    passcode.alpha = 0.5F
                    passcode.isEnabled = false
                    passcode.clearFocus()
                    hideKeyboard(passcode)
                    verify.visibility = View.VISIBLE

                    if(pinview?.value.toString().equals(passcode_txt)){

                        reset.alpha = 0.8F
                        reset.isEnabled = false
                        dialog = ProgressDialog(this@PasscodeActivity)
                        dialog.setMessage("Setting up your account")
                        dialog.setCancelable(false)
                        dialog.show()

                        if(path_txt.equals("null")){
                            saveDataUserToDatabase()
                        } else {
                            uploadFotoToStorage(path_txt)
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
                            showKeyboard()

                            Toast.makeText(this@PasscodeActivity, "Passcode don't match!", Toast.LENGTH_SHORT).show()

                        }, 500)
                    }
                }

            }
        })

    }

    private fun uploadFotoToStorage(path: String){

        val file = Uri.fromFile(File(path))
        val ref = storageRef.child("pictures/${file.lastPathSegment}")
        val uploadTask = ref.putFile(file)

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    throw it
                }
            }
            ref.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                url_txt = task.result!!.toString()
                saveDataUserToDatabase()
            }
        }

    }

    private fun saveDataUserToDatabase(){

        if(idHome.equals("21")){

            val user = hashMapOf(

                "name" to name_txt,
                "phone" to number_txt,
                "picture" to url_txt,
                "passcode" to passcode_txt,
                "access" to "Admin"

            )

            db.collection("USER").add(user).addOnSuccessListener { documentReference ->

                idUser = documentReference.id
                val intent = Intent(this@PasscodeActivity, AddHomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("idUser", idUser)
                startActivity(intent)

                verify.visibility = View.INVISIBLE
                passcode.alpha = 1F
                passcode.isEnabled = true
                reset.alpha = 1F
                reset.isEnabled = true

            }

        } else {

            val user = hashMapOf(

                "name" to name_txt,
                "phone" to number_txt,
                "picture" to url_txt,
                "passcode" to passcode_txt,
                "home" to idHome,
                "access" to "User"

            )

            db.collection("USER").add(user).addOnSuccessListener { documentReference ->

                idUser = documentReference.id
                val intent = Intent(this@PasscodeActivity, HomeActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                intent.putExtra("idUser", idUser)
                intent.putExtra("idHome", idHome)
                startActivity(intent)

                verify.visibility = View.INVISIBLE
                passcode.alpha = 1F
                passcode.isEnabled = true
                reset.alpha = 1F
                reset.isEnabled = true

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
