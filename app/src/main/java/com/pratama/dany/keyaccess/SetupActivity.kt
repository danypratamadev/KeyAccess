package com.pratama.dany.keyaccess

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.view.animation.AnimationUtils
import com.google.zxing.WriterException
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import kotlinx.android.synthetic.main.activity_setup.*
import pl.droidsonroids.gif.GifImageView


class SetupActivity : AppCompatActivity()  {

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()
    private val dbf : FirebaseDatabase = FirebaseDatabase.getInstance()

    private val REQ = 21
    private lateinit var idUser : String
    private lateinit var idHome : String
    private lateinit var idDoor : String
    private var codeQr : String = ""
    private lateinit var idQr : String

    private lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup)

        init()
        onClickListener()
        onaddTextChangedListener()

    }

    private fun init(){

        idUser = intent.getStringExtra("idUser")
        idHome = intent.getStringExtra("idHome")

        qr_code.visibility = View.GONE
        door_name.requestFocus()

        Handler().postDelayed({

            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)

        }, 300)

        db.collection("USER").document(idUser).addSnapshotListener{ result, e ->

            if (e != null) {
                Log.w("SetupActivity", "listen:error", e)
                return@addSnapshotListener
            }

            if(result != null && result.exists()){

                user_name.setText(result.getString("name"))
                user_name.isEnabled = false

            }

        }

        parentView.setOnTouchListener { view, motionEvent ->

            hideKeyboard(door_name)
            door_name.clearFocus()

            return@setOnTouchListener true

        }

    }

    private fun onClickListener(){

        back.setOnClickListener {

            onBackPressed()
            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(door_name.windowToken, 0)

        }

        scan.setOnClickListener {

            val intent = Intent(this, CameraActivity::class.java)
            startActivityForResult(intent, REQ)

        }

        finish.setOnClickListener {

            if(!door_name.text.toString().equals("") && !codeQr.equals("")){

                dialog = ProgressDialog(this)
                dialog.setTitle("Pairing")
                dialog.setMessage("Adding ${door_name.text}")
                dialog.setCancelable(false)
                dialog.show()

                val newDoor = hashMapOf(
                    "name" to door_name.text.toString(),
                    "key" to codeQr,
                    "status" to true,
                    "auto" to false,
                    "door" to true,
                    "time" to 5000
                )

                db.collection("HOME").document(idHome).collection("DOOR").add(newDoor).addOnSuccessListener { e ->

                    idDoor = e.id

                    val updateDevice = hashMapOf<String, Any>(
                        "pair" to true
                    )

                    db.collection("DEVICE").document(idQr).update(updateDevice).addOnSuccessListener { e ->

                        val newDoor = hashMapOf(
                            "door" to idDoor
                        )

                        db.collection("USER").document(idUser).collection("ACCESS")
                            .add(newDoor).addOnSuccessListener { e ->

                            Handler().postDelayed({

                                dialog.dismiss()
                                onBackPressed()

                            }, 500)

                        }

                    }

                    val publicRef = dbf.reference
                    val newUser = DataDoorFBR(false, true, true, true, 1, true, 5000, false)

                    publicRef.child(codeQr).setValue(newUser)

                }

            } else {

                if(door_name.text.toString().equals("")){

                    ti_door_name.isErrorEnabled = true
                    ti_door_name.error = "Enter your door name!"

                }

                if(codeQr.equals("")){

                    Toast.makeText(this, "Please scan QR Code to continue", Toast.LENGTH_SHORT).show()

                }

            }

        }

    }

    private fun onaddTextChangedListener(){

        door_name.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

                if(!s.toString().equals("")){

                    ti_door_name.isErrorEnabled = false

                } else {

                    ti_door_name.isErrorEnabled = true
                    ti_door_name.error = "Enter your door name!"

                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //To change body of created functions use File | Settings | File Templates.
            }

            internal fun isEmailValid(email: CharSequence?): Boolean {
                return android.util.Patterns.EMAIL_ADDRESS.matcher(email)
                    .matches()
            }

        })

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == REQ){

            if(resultCode == Activity.RESULT_OK){

                if(data != null){

                    codeQr = data!!.getStringExtra("codeQr")
                    idQr = data!!.getStringExtra("idQr")
                    val animation = AnimationUtils.loadAnimation(this, R.anim.fade_down_from_position2)

                    qr_code_gif.startAnimation(animation)
                    scan.startAnimation(animation)
                    qr_code_gif.visibility = View.GONE
                    scan.visibility = View.GONE
                    qr_code.visibility = View.VISIBLE

                    result_dis.text = codeQr

                    val multiFormatWriter = MultiFormatWriter()
                    try {
                        val bitMatrix = multiFormatWriter.encode(codeQr, BarcodeFormat.QR_CODE, 200, 200)
                        val barcodeEncoder = BarcodeEncoder()
                        val bitmap = barcodeEncoder.createBitmap(bitMatrix)
                        qr_code.setImageBitmap(bitmap)
                    } catch (e: WriterException) {
                        e.printStackTrace()
                    }

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
