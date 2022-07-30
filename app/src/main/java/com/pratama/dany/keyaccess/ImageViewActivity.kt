package com.pratama.dany.keyaccess

import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageButton
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.github.chrisbanes.photoview.PhotoView
import kotlinx.android.synthetic.main.activity_image_view.*
import kotlinx.android.synthetic.main.activity_setting.*

class ImageViewActivity : AppCompatActivity() {

    private lateinit var back : ImageButton
    private lateinit var imgView : PhotoView

    private lateinit var url_txt : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_view)

        back = findViewById(R.id.back)
        imgView = findViewById(R.id.imageView)

        url_txt = intent.getStringExtra("url")

        Glide.with(this).load(url_txt).apply(
            RequestOptions()
                .override(450, 450))
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(imageView)

        back.setOnClickListener {

            onBackPressed()

        }

    }
}
