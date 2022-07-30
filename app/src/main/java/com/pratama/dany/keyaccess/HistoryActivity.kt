package com.pratama.dany.keyaccess

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import kotlinx.android.synthetic.main.activity_history.*


class HistoryActivity : AppCompatActivity() {

    private lateinit var idHome : String
    private lateinit var nameHome : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)

        init()
        onClickListener()

    }

    private fun init(){

        idHome = intent.getStringExtra("idHome")
        nameHome = intent.getStringExtra("nameHome")

        val pageFragmentAdapter = PageFragmentAdapter(supportFragmentManager, idHome, nameHome)
        val limit = if (pageFragmentAdapter.count > 1) pageFragmentAdapter.count - 1 else 1

        Handler().postDelayed({

            historyViewPager?.offscreenPageLimit = limit
            historyViewPager?.adapter = pageFragmentAdapter
            tabHistory?.setupWithViewPager(historyViewPager)

        }, 300)

    }

    private fun onClickListener(){

        back.setOnClickListener{

            onBackPressed()

        }

    }

}
