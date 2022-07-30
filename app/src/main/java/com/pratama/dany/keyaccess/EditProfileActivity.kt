package com.pratama.dany.keyaccess

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.*
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.android.synthetic.main.activity_edit_profile.*

class EditProfileActivity : AppCompatActivity() {

    private val TAG : String = "EditProfileActivity"
    private val auth : FirebaseAuth = FirebaseAuth.getInstance()
    private val db : FirebaseFirestore = FirebaseFirestore.getInstance()

    private lateinit var id_txt : String
    private lateinit var url_txt : String
    private lateinit var phone_txt : String
    private lateinit var name_txt : String

    override fun onCreate(savedInstanceState: Bundle?) {

        if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES){
            setTheme(R.style.DarkTheme)
        } else {
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)

        url_txt = intent.getStringExtra("url")
        phone_txt = auth.currentUser?.phoneNumber.toString()

        db.collection("USER").whereEqualTo("phone",
            phone_txt).addSnapshotListener { result, e ->

            if (e != null) {
                Log.w(TAG, "listen:error", e)
                return@addSnapshotListener
            }

            for(doc in result!!){
                id_txt = doc.id
                name_txt = doc.getString("name").toString()
                name_dis.text = doc.getString("name")
                phone_dis.text = phone_txt
            }

        }

        Glide.with(this).load(url_txt).apply(
            RequestOptions()
                .override(250, 250))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imgUser)

        imgUser.setOnClickListener {

            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, imgUser, ViewCompat.getTransitionName(imgUser).toString())
            val intent = Intent(this, ImageViewActivity::class.java)
            intent.putExtra("url", url_txt)
            startActivity(intent, options.toBundle())

        }

        choose.setOnClickListener {



        }

        name.setOnClickListener {

            showPopupName()

        }

        phone.setOnClickListener {

            showPopupEmail()

        }

        back.setOnClickListener {

            onBackPressed()

        }

    }

    fun showPopupName() {

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

        name.setText(name_txt)
        name.requestFocus()

        cancel.setOnClickListener {

            mdialog.dismiss()

        }

        save.setOnClickListener {

            if(name.text.toString().equals(name_txt)){

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

                db.collection("USER").document(id_txt).update(data).addOnSuccessListener {

                    progress.visibility = View.INVISIBLE
                    result.visibility = View.VISIBLE
                    name.alpha = 1F
                    save.alpha = 1F

                    Handler().postDelayed({

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

    fun showPopupEmail() {

        val save : Button
        val cancel : Button
        val ti_phone : TextInputLayout
        val phone : TextInputEditText
        val progress : ProgressBar
        val result : ImageView
        val mdialog : Dialog
        mdialog = Dialog(this)
        mdialog.setCancelable(false)
        mdialog.setContentView(R.layout.phone_layout)
        mdialog.window?.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE)
        save = mdialog.findViewById(R.id.save)
        cancel = mdialog.findViewById(R.id.cancel)
        ti_phone = mdialog.findViewById(R.id.ti_phone)
        phone = mdialog.findViewById(R.id.phone)
        progress = mdialog.findViewById(R.id.progress)
        result = mdialog.findViewById(R.id.result)

        progress.visibility = View.INVISIBLE
        result.visibility = View.INVISIBLE

        val phoneLast = phone_txt.takeLast(phone_txt.length - 3)
        phone.setText(phoneLast)
        phone.requestFocus()

        cancel.setOnClickListener {

            mdialog.dismiss()

        }

        save.setOnClickListener {

            if(phone.text.toString().equals(phone_txt)){

                mdialog.dismiss()

            } else {

                progress.visibility = View.VISIBLE
                phone.alpha = 0.8F
                phone.clearFocus()
                phone.isEnabled = false
                save.alpha = 0.8F
                save.isEnabled = false

                val data = hashMapOf<String, Any>(

                    "phone" to phone.text.toString()

                )

                db.collection("USER").document(id_txt).update(data).addOnSuccessListener {

                    progress.visibility = View.INVISIBLE
                    result.visibility = View.VISIBLE
                    phone.alpha = 1F
                    save.alpha = 1F

                    Handler().postDelayed({

                        mdialog.dismiss()

                    }, 400)

                }.addOnFailureListener {

                    progress.visibility = View.INVISIBLE
                    result.visibility = View.VISIBLE
                    result.backgroundTintList = resources.getColorStateList(R.color.Red500)
                    phone.alpha = 1F
                    phone.isEnabled = true
                    save.alpha = 1F
                    save.isEnabled = true

                    phone.requestFocus()

                }

            }

        }

        mdialog.getWindow()!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        mdialog.getWindow()!!.getAttributes().windowAnimations = R.style.DialogAnimation
        mdialog.show()

    }

}
