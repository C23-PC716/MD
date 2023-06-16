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
import com.google.firebase.auth.ktx.userProfileChangeRequest


// yang asli

class RegisterActivity : AppCompatActivity() {

    lateinit var editFullName: EditText
    lateinit var editEmail: EditText
    lateinit var editPassword: EditText
    lateinit var editPasswordConf: EditText
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
        setContentView(R.layout.activity_register)

        // inisiasi id activity register
        editFullName = findViewById(R.id.full_name)
        editEmail = findViewById(R.id.email)
        editPassword = findViewById(R.id.password)
        editPasswordConf = findViewById(R.id.password_conf)
        btnRegister = findViewById(R.id.btn_register)
        btnLogin = findViewById(R.id.btn_login)

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Logging")
        progressDialog.setMessage("Aplikasi Sedang Loading, Mohon Menunggu Aplikasi Berjalan")

        // inisisasi id login activity Register
        btnLogin.setOnClickListener{
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
        // Button Register
        btnRegister.setOnClickListener{
            //validasi si nama, email dan password apakah doi valid
            if (editFullName.text.isNotEmpty() && editEmail.text.isNotEmpty() && editPassword.text.isNotEmpty()){
                if (editPassword.text.toString() == editPasswordConf.text.toString()){
                    // Menjalankan Register
                    prosesRegister()
                }else {
                    Toast.makeText(this, "Anda Hanya Dapat Mengisikan Kata Sandi Yang Sama", LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Tolong Isikan Data dengan Benar", LENGTH_SHORT).show()
            }
        }
    }

    // Memproses Registrasi
    private fun prosesRegister(){
        val fullName = editFullName.text.toString()
        val email = editEmail.text.toString()
        val password = editPassword.text.toString()

        progressDialog.show()

        // ini saat proses register
        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener{  task ->
                if (task.isSuccessful){
                    val userUpdateProfile = userProfileChangeRequest {
                        displayName = fullName
                    }
                    val user = task.result.user
                    user!!.updateProfile(userUpdateProfile)
                        .addOnCompleteListener {
                            progressDialog.dismiss()
                            startActivity(Intent(this, MainActivity::class.java))
                        }
                        .addOnFailureListener { error2 ->
                            Toast.makeText(this, error2.localizedMessage, LENGTH_SHORT).show()
                        }
                }else {
                    progressDialog.dismiss()
                }
            }
            .addOnFailureListener { error ->
                Toast.makeText(this, error.localizedMessage, LENGTH_SHORT).show()
            }
    }
}