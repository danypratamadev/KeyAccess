package com.pratama.dany.keyaccess

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.os.StrictMode
import android.provider.MediaStore
import android.view.View
import android.view.inputmethod.InputMethodManager
import kotlinx.android.synthetic.main.activity_profile.*
import kotlinx.android.synthetic.main.activity_profile.back
import kotlinx.android.synthetic.main.activity_profile.next
import kotlinx.android.synthetic.main.activity_profile.parentView
import java.io.File

class ProfileActivity : AppCompatActivity(), BottomSheetProfile.BottomSheetPhotoListener{

    private lateinit var name_txt : String
    private lateinit var number_txt : String
    private var path_txt = "null"
    private lateinit var mPhoto : File

    private val REQ1 = 1
    private val REQ2 = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        init()
        onFocusChangeListener()

        next.setOnClickListener {

            nextSteps()

        }

        back.setOnClickListener {

            onBackPressed()

        }

        photo.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQ2)

            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(name.windowToken, 0)

        }

        photo2.setOnClickListener {

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQ2)

            val keyboard = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            keyboard.hideSoftInputFromWindow(name.windowToken, 0)

        }

        parentView.setOnTouchListener { view, motionEvent ->

            hideKeyboard(name)
            name.clearFocus()

            return@setOnTouchListener true

        }

    }

    private fun init(){

        number_txt = intent.getStringExtra("phone")
        name.requestFocus()

        Handler().postDelayed({

            showKeyboard()

        }, 300)

    }

    private fun nextSteps(){

        if(!name.text.toString().equals("")){

            name_txt = name.text.toString()

            val intent = Intent(this, InvitationActivity::class.java)
            intent.putExtra("name", name_txt)
            intent.putExtra("phone", number_txt)
            intent.putExtra("path", path_txt)
            startActivity(intent)

        } else {

            if(name.text.toString().equals("")){
                ti_name.isErrorEnabled = true
                ti_name.error = "Enter your name!"
            }

        }

    }

    private fun onFocusChangeListener(){

        name.setOnFocusChangeListener { v, hasFocus ->

            if(hasFocus){
                onaddTextChangedListener()
            }

        }

    }

    private fun onaddTextChangedListener(){

        name.addTextChangedListener(object : TextWatcher {

            override fun afterTextChanged(s: Editable?) {

                if(!s.toString().equals("")){

                    ti_name.isErrorEnabled = false

                } else {

                    ti_name.isErrorEnabled = true
                    ti_name.error = "Enter your name!"

                }

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                //To change body of created functions use File | Settings | File Templates.
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                //To change body of created functions use File | Settings | File Templates.
            }


        })

    }

    override fun onClicked(code: Int) {

        if(code == 1){

            val folder = File(Environment.getExternalStorageDirectory().toString() + "/Pictures/KeyAccess/")
            if(!folder.exists()){
                folder.mkdir()
            }

            val path = Environment.getExternalStorageDirectory().toString() + "/Pictures/KeyAccess/"
            mPhoto = File(path)
            val uri = Uri.fromFile(mPhoto)
            val intent = Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)
            val builder = StrictMode.VmPolicy.Builder()
            StrictMode.setVmPolicy(builder.build())
            startActivityForResult(intent, REQ1)

        } else {

            val folder = File(Environment.getExternalStorageDirectory().toString() + "/Pictures/KeyAccess/")
            if(!folder.exists()){
                folder.mkdir()
            }

            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQ2)

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {

            if (requestCode == REQ1) {

                if (mPhoto.exists()) {

                    photo.setImageBitmap(BitmapFactory.decodeFile(mPhoto.absolutePath))

                }

            } else {

                val dataPhoto = data?.getData() as Uri
                path_txt = getPathFromURI(dataPhoto)!!

                mPhoto = File(path_txt)
                photo.setImageBitmap(BitmapFactory.decodeFile(mPhoto.absolutePath))

            }

        }

    }

    private fun getPathFromURI(contentUri: Uri): String? {

        var res: String? = null
        val proj = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = contentResolver.query(contentUri, proj, null, null, null)
        if (cursor!!.moveToFirst()) {
            val column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            res = cursor.getString(column_index)
        }
        cursor.close()
        return res

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
