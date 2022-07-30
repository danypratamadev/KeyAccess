package com.pratama.dany.keyaccess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import pl.droidsonroids.gif.GifImageView

class WelcomeActivity : AppCompatActivity() {

    private lateinit var form_action : LinearLayout
    private lateinit var sign : Button
    private lateinit var register : Button
    private lateinit var gifwell : GifImageView
    private lateinit var appname : TextView
    private lateinit var subname : TextView

    private var mBack : Long = 0
    private val INTERVAL = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_welcome)

        gifwell = findViewById(R.id.gifwell)
        form_action = findViewById(R.id.form_action)
        sign = findViewById(R.id.sign)
        register = findViewById(R.id.register)
        appname = findViewById(R.id.appname)
        subname = findViewById(R.id.subname)

        appname.visibility = View.INVISIBLE
        subname.visibility = View.INVISIBLE

        val animation = AnimationUtils.loadAnimation(this, R.anim.slide_from_buttom)
        val animation2 = AnimationUtils.loadAnimation(this, R.anim.fade_up_from_position)
        val animation3 = AnimationUtils.loadAnimation(this, R.anim.slide_down_from_top)

        form_action.startAnimation(animation)

        Handler().postDelayed({

            appname.visibility = View.VISIBLE
            subname.visibility = View.VISIBLE
            gifwell.startAnimation(animation2)
            appname.startAnimation(animation3)
            subname.startAnimation(animation3)

        }, 300)

        sign.setOnClickListener{

            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)

        }

        register.setOnClickListener{

            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)

        }

    }

    override fun onBackPressed() {
        if (mBack + INTERVAL > System.currentTimeMillis()) {
            super.onBackPressed()
            return
        } else {
            Toast.makeText(this, "Double tap to exit", Toast.LENGTH_SHORT).show()
        }
        mBack = System.currentTimeMillis()
    }

}
