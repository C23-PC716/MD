package com.example.dermacare

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toast.LENGTH_SHORT
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    lateinit var editEmail: EditText
    lateinit var editPassword: EditText
    lateinit var btnRegister: Button
    lateinit var btnLogin: Button
    lateinit var progressDialog: ProgressDialog

    // koneksi ke firebase
    var firebaseAuth = FirebaseAuth.getInstance()

    // cek user apakah sudah login atau belum
    override fun onStart() {
        super.onStart()
        //misalkan sudah login di arahkan ke mainaktivity (beranda)
        if (firebaseAuth.currentUser!=null){
            startActivity(Intent(this, MainActivity::class.java))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        editEmail = findViewById(R.id.email)
        editPassword = findViewById(R.id.password)
        btnRegister = findViewById(R.id.btn_register)
        btnLogin = findViewById(R.id.btn_login)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Logging")
        progressDialog.setMessage("Aplikasi Sedang Loading, Mohon Menunggu Aplikasi Berjalan")

        //inisiasi Login
        btnLogin.setOnClickListener{
            if (editEmail.text.isNotEmpty() && editPassword.text.isNotEmpty()){
                prosesLogin()
            }else {
                Toast.makeText(this, "Tolong Isi Email dan Password Terlebih dahulu", LENGTH_SHORT).show()
            }
        }
        btnRegister.setOnClickListener {
            startActivity(Intent(this, RegisterActivity::class.java))
        }
    }

    private fun prosesLogin(){
        val email = editEmail.text.toString()
        val password = editPassword.text.toString()

        progressDialog.show()

        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                startActivity(Intent(this, MainActivity::class.java))
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, error.localizedMessage, LENGTH_SHORT).show()
            }
            .addOnCompleteListener {
                progressDialog.dismiss()
            }
    }
}