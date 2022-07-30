package com.pratama.dany.keyaccess

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.app.ComponentActivity.ExtraData
import androidx.core.content.ContextCompat.getSystemService
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import android.os.Handler
import android.view.View
import android.view.WindowManager
import android.widget.*
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.firestore.FirebaseFirestore
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException
import com.journeyapps.barcodescanner.BarcodeEncoder
import com.mikhaellopez.circularimageview.CircularImageView
import kotlinx.android.synthetic.main.activity_home_details.*
import kotlinx.android.synthetic.main.activity_home_details.back
import kotlinx.android.synthetic.main.activity_home_details.img
import kotlinx.android.synthetic.main.activity_home_details.name
import kotlinx.android.synthetic.main.activity_setting.*
import java.util.*


class HomeDetailsActivity : AppCompatActivity() {

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var nameHome : String
    private lateinit var homeAddress : String
    private lateinit var idHome : String
    private lateinit var invitCode : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_details)

        init()
        onClickListener()

    }

    private fun init(){

        nameHome = intent.getStringExtra("homeName")
        homeAddress = intent.getStringExtra("homeAddress")
        idHome = intent.getStringExtra("idHome")
        invitCode = intent.getStringExtra("invitation")

        name_dis.text = nameHome
        address_dis.text = homeAddress
        codeInvit.text = invitCode

        load.visibility = View.GONE

        val multiFormatWriter = MultiFormatWriter()
        try {
            val bitMatrix = multiFormatWriter.encode(idHome+"/"+invitCode, BarcodeFormat.QR_CODE, 200, 200)
            val barcodeEncoder = BarcodeEncoder()
            val bitmap = barcodeEncoder.createBitmap(bitMatrix)
            img.setImageBitmap(bitmap)
        } catch (e: WriterException) {
            e.printStackTrace()
        }

    }

    private fun onClickListener(){

        name.setOnClickListener {

            showPopupName()

        }

        refresh.setOnClickListener {

            refresh.visibility = View.GONE
            load.visibility = View.VISIBLE

            val newCode = generateInvitationCode()

            val update = hashMapOf<String, Any>(

                "invitation" to newCode

            )

            db.collection("HOME").document(idHome).update(update).addOnSuccessListener { e ->

                load.visibility = View.GONE
                refresh.visibility = View.VISIBLE
                codeInvit.text = newCode
                val multiFormatWriter = MultiFormatWriter()
                try {
                    val bitMatrix = multiFormatWriter.encode(idHome+"/"+newCode, BarcodeFormat.QR_CODE, 200, 200)
                    val barcodeEncoder = BarcodeEncoder()
                    val bitmap = barcodeEncoder.createBitmap(bitMatrix)
                    img.setImageBitmap(bitmap)
                } catch (e: WriterException) {
                    e.printStackTrace()
                }

                Toast.makeText(this, "Success generate new invitation code", Toast.LENGTH_SHORT).show()

            }

        }

        back.setOnClickListener {

            onBackPressed()

        }

    }

    private fun generateInvitationCode() : String{

        val chars1 = "ABCDEF012GHIJKL345MNOPQR678STUVWXYZ9".toCharArray()
        val sb1 = StringBuilder()
        val random1 = Random()

        for (i in 0..5) {

            val c1 = chars1[random1.nextInt(chars1.size)]
            sb1.append(c1)

        }

        val random_string = sb1.toString()

        return random_string

    }

    private fun showPopupName() {

        val save : Button
        val cancel : Button
        val ti_name : TextInputLayout
        val name : TextInputEditText
        val progress : ProgressBar
        val result : ImageView
        val mdialog : Dialog
        mdialog = Dialog(this)
        mdialog.setCancelable(false)
        mdialog.setContentView(R.layout.name_layout)
        mdialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        save = mdialog.findViewById(R.id.save)
        cancel = mdialog.findViewById(R.id.cancel)
        ti_name = mdialog.findViewById(R.id.ti_name)
        name = mdialog.findViewById(R.id.name)
        progress = mdialog.findViewById(R.id.progress)
        result = mdialog.findViewById(R.id.result)

        progress.visibility = View.INVISIBLE
        result.visibility = View.INVISIBLE

        name.setText(nameHome)
        name.requestFocus()

        cancel.setOnClickListener {

            mdialog.dismiss()

        }

        save.setOnClickListener {

            if(name.text.toString().equals(nameHome)){

                mdialog.dismiss()

            } else {

                progress.visibility = View.VISIBLE
                name.alpha = 0.8F
                name.clearFocus()
                name.isEnabled = false
                save.alpha = 0.8F
                save.isEnabled = false

                val data = hashMapOf<String, Any>(

                    "name" to name.text.toString()

                )

                db.collection("HOME").document(idHome).update(data).addOnSuccessListener {

                    progress.visibility = View.INVISIBLE
                    result.visibility = View.VISIBLE
                    name.alpha = 1F
                    save.alpha = 1F

                    Handler().postDelayed({

                        name_dis.text = name.text.toString()
                        mdialog.dismiss()

                    }, 400)

                }.addOnFailureListener {

                    progress.visibility = View.INVISIBLE
                    result.visibility = View.VISIBLE
                    result.backgroundTintList = resources.getColorStateList(R.color.Red500)
                    name.alpha = 1F
                    name.isEnabled = true
                    save.alpha = 1F
                    save.isEnabled = true

                    name.requestFocus()

                }

            }

        }

        mdialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mdialog.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation
        mdialog.show()

    }

}
