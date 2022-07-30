package com.pratama.dany.keyaccess

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var out : Button

    private val auth : FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        out = findViewById(R.id.out)

        out.setOnClickListener {

            auth.signOut()
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)

        }

    }
}
