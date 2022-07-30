package com.pratama.dany.keyaccess

import android.app.Activity
import android.app.Dialog
import android.app.ProgressDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.google.zxing.Result
import me.dm7.barcodescanner.zxing.ZXingScannerView
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore


class CameraActivity : AppCompatActivity(), ZXingScannerView.ResultHandler {

    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var mScanner : ZXingScannerView
    private lateinit var result_code : String
    private lateinit var dialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_camera)

        mScanner = ZXingScannerView(this)
        setContentView(mScanner)
        mScanner.setResultHandler(this)
        mScanner.setAutoFocus(true)
        mScanner.startCamera()

    }

    override fun handleResult(result: Result?) {

        result_code = result?.text.toString()
        dialog = ProgressDialog(this)
        dialog.setMessage("Please wait")
        dialog.setCancelable(false)
        dialog.show()

        db.collection("DEVICE").whereEqualTo("key", result_code).get().addOnSuccessListener{ result ->

            if(!result.isEmpty){

                for(doc in result!!){

                    if(doc.getBoolean("pair") == false){

                        Handler().postDelayed({

                            val intent = Intent()
                            intent.putExtra("codeQr", result_code)
                            intent.putExtra("idQr", doc.id)
                            setResult(Activity.RESULT_OK, intent)
                            finish()

                        }, 500)

                    } else {

                        dialog.dismiss()
                        Toast.makeText(this, "Invalid QR Code. Please try again!", Toast.LENGTH_LONG).show()
                        Handler().postDelayed({
                            mScanner.resumeCameraPreview(this)
                            mScanner.startCamera()
                        }, 500)

                    }

                }

            } else {

                dialog.dismiss()
                Toast.makeText(this, "Invalid QR Code. Please try again!", Toast.LENGTH_LONG).show()
                Handler().postDelayed({
                    mScanner.resumeCameraPreview(this)
                    mScanner.startCamera()
                }, 500)

            }

        }

    }

    override fun onRestart() {
        super.onRestart()
        mScanner.resumeCameraPreview(this)
        mScanner.startCamera()
    }

}
