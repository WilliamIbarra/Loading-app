package com.udacity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.udacity.databinding.ContentDetailBinding
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {
private lateinit var mBinding: ContentDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ContentDetailBinding.inflate(layoutInflater).apply {
            nameDownload = intent.getStringExtra("name")
            statusDownload = intent.getStringExtra("status")
        }

        mBinding.backBtn.setOnClickListener {
            // Create an explicit intent for an Activity in your app
            val intent = Intent(this, MainActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }

        setContentView(mBinding.root)
        setSupportActionBar(toolbar)
    }

}
