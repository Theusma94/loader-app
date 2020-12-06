package com.udacity

import android.content.Intent
import android.os.Bundle
import android.view.MotionEvent
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_detail.*
import kotlinx.android.synthetic.main.content_detail.*

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        setSupportActionBar(toolbar)
        val bundle = intent.getBundleExtra(MainActivity.BUNDLE_KEY)
        file_name_text.text = bundle?.getString(MainActivity.TITLE_KEY) ?: ""
        bundle?.getBoolean(MainActivity.STATUS_KEY).apply {
            this?.let {
                if(it) {
                    status_text.text = "Success"
                    status_text.setTextColor(ContextCompat.getColor(this@DetailActivity,R.color.colorPrimaryDark))
                } else {
                    status_text.text = "Fail"
                    status_text.setTextColor(ContextCompat.getColor(this@DetailActivity,android.R.color.holo_red_dark))
                }
            }
        }

        button_finalize_detail.setOnClickListener {
            val intent = Intent(this,MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}
