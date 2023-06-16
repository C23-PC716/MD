package com.example.dermacare

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.dermacare.databinding.ActivityDetailBinding

class DetailActivity : AppCompatActivity() {

    var imageURL = ""
    private lateinit var binding: ActivityDetailBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val bundle = intent.extras
        if (bundle != null) {
            binding.detailTitle.text = bundle.getString("Title")
            binding.detailDesc.text = bundle.getString("Description")
            binding.detailPriority.text = bundle.getString("Priority")
            imageURL = bundle.getString("Image") ?: ""
            if (imageURL.isNotEmpty()) {
                Glide.with(this).load(imageURL).into(binding.detailImage)
            }

            // Menampilkan nama penyakit dan deskripsi penyakit
            binding.detailNama.text = bundle.getString("Nama")
            binding.detailDeskripsi.text = bundle.getString("Deskripsi")
        }
    }
}