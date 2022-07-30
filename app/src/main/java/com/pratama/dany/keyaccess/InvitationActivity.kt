package com.pratama.dany.keyaccess

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_invitation.*

class InvitationActivity : AppCompatActivity() {

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var number_txt : String
    private lateinit var name_txt : String
    private lateinit var path_txt : String
    private var idHome : String = "21"
    private var invalid = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invitation)

        init()

        back.setOnClickListener {

            onBackPressed()

        }

        next.setOnClickListener {

            if(!code.text.toString().equals("") && invalid == 1){
                nextSteps()
            } else {
                ti_code.isErrorEnabled = true
                ti_code.error = "Enter your invitation code!"
            }

        }

        skip.setOnClickListener{

            nextSteps()

        }

        parentView.setOnTouchListener { view, motionEvent ->

            hideKeyboard(code)
            code.clearFocus()

            return@setOnTouchListener true

        }

    }

    private fun init(){

        number_txt = intent.getStringExtra("phone")
        name_txt = intent.getStringExtra("name")
        path_txt = intent.getStringExtra("path")

        progress.visibility = View.INVISIBLE
        code.requestFocus()

        Handler().postDelayed({

            showKeyboard()

        }, 300)

        code.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(p0: Editable?) {

                progress.visibility = View.VISIBLE

                db.collection("HOME").whereEqualTo("invitation", p0.toString()).get().addOnSuccessListener { e ->

                    if(!e.isEmpty){

                        for(doc in e){

                            idHome = doc.id
                            ti_code.isErrorEnabled = false
                            progress.visibility = View.INVISIBLE
                            invalid = 1

                        }

                    } else {

                        ti_code.isErrorEnabled = true
                        ti_code.error = "Invalid code! Try again."
                        progress.visibility = View.INVISIBLE
                        invalid = 2

                    }

                }

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

        })



    }

    private fun nextSteps(){

        val intent = Intent(this, PasscodeActivity::class.java)
        intent.putExtra("name", name_txt)
        intent.putExtra("phone", number_txt)
        intent.putExtra("path", path_txt)
        intent.putExtra("idHome", idHome)
        startActivity(intent)

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
