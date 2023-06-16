package com.example.dermacare

import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    // ==================================== Ini Kode Untuk Login =======================================//
    lateinit var textFullName: TextView
    lateinit var textEmail: TextView
    lateinit var btnLogout: Button
    lateinit var btnHistori: Button
    val firebaseAuth = FirebaseAuth.getInstance()

    // ==================================================================================================//

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // ==================================== Ini Kode Untuk Login =======================================//
        textFullName = findViewById(R.id.full_name)
        textEmail = findViewById(R.id.email)
        btnLogout = findViewById(R.id.btn_logout)
        val firebaseUser = firebaseAuth.currentUser

        if (firebaseUser!=null){
            textFullName.text = firebaseUser.displayName
            textEmail.text = firebaseUser.email
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // inisiasi Logout
        btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            // dipindahi ke login
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Navigasi ke HistoriActivity
        btnHistori = findViewById(R.id.btnHistori)
        btnHistori.setOnClickListener {
            startActivity(Intent(this, KameraActivity::class.java))
        }
    }
}
