package com.pratama.dany.keyaccess

import android.Manifest
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.core.app.ActivityCompat
import android.content.pm.PackageManager
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_sign_in.*

class SignInActivity : AppCompatActivity() {

    private lateinit var phone_txt : String
    private lateinit var mdialog : Dialog
    private val REQ = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        init()
        onFocusChangeListener()
        verifyPermissions()

        next.setOnClickListener {

            nextSteps()

        }

        back.setOnClickListener {

            onBackPressed()

        }

        parentView.setOnTouchListener { view, motionEvent ->

            hideKeyboard(phone)
            phone.clearFocus()

            return@setOnTouchListener true

        }

    }

    private fun init(){

        gifwell.visibility = View.INVISIBLE
        appname.visibility = View.INVISIBLE
        subname.visibility = View.INVISIBLE
        form_signin.visibility = View.INVISIBLE
        progress.visibility = View.INVISIBLE

        val animation = AnimationUtils.loadAnimation(this, R.anim.fade_up_from_position)
        val animation2 = AnimationUtils.loadAnimation(this, R.anim.slide_down_from_top)
        val animation3 = AnimationUtils.loadAnimation(this, R.anim.slide_from_buttom)

        gifwell.visibility = View.VISIBLE
        appname.visibility = View.VISIBLE
        subname.visibility = View.VISIBLE
        form_signin.visibility = View.VISIBLE
        gifwell.startAnimation(animation)
        appname.startAnimation(animation2)
        subname.startAnimation(animation2)
        form_signin.startAnimation(animation3)

    }

    private fun nextSteps(){

        if(!phone.text.toString().equals("")){

            val result = phone.text.toString().take(1)

            if(phone.text.toString().length > 9 && !result.equals("0")){

                phone_txt = "+62"+phone.text.toString()

                phone.alpha = 0.8F
                phone.clearFocus()
                phone.isEnabled = false
                next.alpha = 0.8F
                next.isEnabled = false
                progress.visibility = View.VISIBLE

                Handler().postDelayed({

                    showPopupVerify()

                    progress.visibility = View.INVISIBLE
                    phone.alpha = 1F
                    phone.isEnabled = true
                    next.alpha = 1F
                    next.isEnabled = true

                }, 500)

            } else {

                ti_phone.isErrorEnabled = true
                ti_phone.error = "Invalid phone number! Please check your phone number."

            }

        } else {

            ti_phone.isErrorEnabled = true
            ti_phone.error = "Enter your phone number!"

        }

    }

    private fun onFocusChangeListener(){

        phone.setOnFocusChangeListener { v, hasFocus ->

            if(hasFocus){
                onaddTextChangedListener()
            }

        }

    }

    private fun onaddTextChangedListener(){

        phone.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

                if(!s.toString().equals("")){

                    if(s.toString().length > 9){

                        if(isPhoneValid(s) == false){
                            ti_phone.isErrorEnabled = true
                            ti_phone.error = "Invalid phone number! Please check your phone number."
                        } else {
                            ti_phone.isErrorEnabled = false
                        }

                    } else {

                        ti_phone.isErrorEnabled = true
                        ti_phone.error = "Invalid phone number! Please check your phone number."

                    }

                } else {

                    ti_phone.isErrorEnabled = true
                    ti_phone.error = "Enter your phone number!"

                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //To change body of created functions use File | Settings | File Templates.
            }

            internal fun isPhoneValid(phone: CharSequence?): Boolean {
                return android.util.Patterns.PHONE.matcher(phone)
                    .matches()
            }

        })

    }

    private fun showPopupVerify() {

        mdialog = Dialog(this)
        mdialog.setCancelable(false)
        mdialog.setContentView(R.layout.numcom_layout)

        val yes : Button = mdialog.findViewById(R.id.yes)
        val edit : Button = mdialog.findViewById(R.id.edit)
        val num : TextView = mdialog.findViewById(R.id.num)

        num.text = phone_txt

        edit.setOnClickListener {

            mdialog.dismiss()
            phone.requestFocus()
            showKeyboard()

        }

        yes.setOnClickListener {

            val intent = Intent(this, VerificationActivity::class.java)
            intent.putExtra("phone", phone_txt)
            startActivity(intent)
            mdialog.dismiss()

        }

        mdialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mdialog.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation
        mdialog.show()

    }

    private fun verifyPermissions() {

        val permission = arrayOf<String>(
            Manifest.permission.INTERNET,
            Manifest.permission.USE_FINGERPRINT,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.CALL_PHONE
        )

        if (ContextCompat.checkSelfPermission(
                this.applicationContext,
                permission[0]
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this.applicationContext,
                permission[1]
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this.applicationContext,
                permission[2]
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this.applicationContext,
                permission[3]
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this.applicationContext,
                permission[4]
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this.applicationContext,
                permission[5]
            ) == PackageManager.PERMISSION_GRANTED
            && ContextCompat.checkSelfPermission(
                this.applicationContext,
                permission[6]
            ) == PackageManager.PERMISSION_GRANTED
        ) {

        } else {
            ActivityCompat.requestPermissions(this, permission, REQ)
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
