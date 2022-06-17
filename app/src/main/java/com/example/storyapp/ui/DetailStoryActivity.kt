package com.example.storyapp.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityDetailStoryBinding

class DetailStoryActivity : AppCompatActivity() {
    private val binding: ActivityDetailStoryBinding by lazy {
    ActivityDetailStoryBinding.inflate(layoutInflater)
}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        setupAction()
    }

    private fun setupAction() {
        val name = intent.getStringExtra(NAME_DETAIL_EXTRA)
        val description = intent.getStringExtra(DESCRIPTION_DETAIL_EXTRA)
        val imgUrl = intent.getStringExtra(IMAGE_URL_DETAIL_EXTRA)

        supportActionBar?.title = name
        supportActionBar?.setDisplayShowHomeEnabled(true)

        binding.tvDescripDetail.text = description

        Glide.with(this)
            .load(imgUrl)
            .into(binding.imgDetailStory)
    }

    companion object {
        const val NAME_DETAIL_EXTRA = "name_detail_extra"
        const val DESCRIPTION_DETAIL_EXTRA = "desc_detail_extra"
        const val IMAGE_URL_DETAIL_EXTRA = "img_detail_extra"
    }

}