package com.pratama.dany.keyaccess

import android.content.Context
import android.media.Ringtone
import android.media.RingtoneManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Vibrator
import kotlinx.android.synthetic.main.activity_alarm.*

class AlarmActivity : AppCompatActivity() {

    private lateinit var vib: Vibrator
    private lateinit var ringtoneSound: Ringtone

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        val nama = intent.getStringExtra("nama").toString()

        doorInfo.text = "$nama was forced open!"

        val pattern = longArrayOf(60, 120, 180, 240, 300, 360, 420, 480)
        vib = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            vib.vibrate(pattern, 1)
        } else {
            vib.vibrate(pattern, 1)
        }

        val ringtoneUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        ringtoneSound = RingtoneManager.getRingtone(applicationContext, ringtoneUri)

        ringtoneSound.play()

    }

    override fun onDestroy() {
        super.onDestroy()
        vib.cancel()
        ringtoneSound.stop()
    }

}
