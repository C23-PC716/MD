package com.example.dermacare

import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.dermacare.databinding.ActivityUploadBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.DateFormat
import java.util.Calendar

class UploadActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUploadBinding
    var imageURL: String? = null
    var uri: Uri? = null
    private lateinit var database: FirebaseDatabase
    private lateinit var storage: FirebaseStorage
    private var nama: String? = null
    private var deskripsi: String? = null


    // Mendefinisikan antarmuka RetrofitAPI untuk endpoint "predict"
    interface RetrofitAPI {
        @Multipart
        @POST("/predict")
        fun postData(@Part image: MultipartBody.Part): Call<PredictResponse>
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUploadBinding.inflate(layoutInflater)
        setContentView(binding.root)

        database = FirebaseDatabase.getInstance()
        storage = FirebaseStorage.getInstance()

        val activityResultLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == RESULT_OK) {
                    val data = result.data
                    if (data != null) {
                        uri = data.data
                        binding.uploadImage.setImageURI(uri)
                    }
                } else {
                    Toast.makeText(this@UploadActivity, "No Image Selected", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        binding.uploadImage.setOnClickListener {
            val photoPicker = Intent(Intent.ACTION_PICK)
            photoPicker.type = "image/*"
            activityResultLauncher.launch(photoPicker)
        }
        binding.saveButton.setOnClickListener {
            saveData()
        }
    }

    private fun saveData() {
        if (uri != null) {
            val storageReference = FirebaseStorage.getInstance().reference.child("Task Images")
                .child(uri!!.lastPathSegment!!)

            val builder = AlertDialog.Builder(this@UploadActivity)
            builder.setCancelable(false)
            builder.setView(R.layout.progress_layout)
            val dialog = builder.create()
            dialog.show()

            storageReference.putFile(uri!!).addOnSuccessListener { taskSnapshot ->
                val uriTask = taskSnapshot.storage.downloadUrl
                while (!uriTask.isComplete);
                val urlImage = uriTask.result
                imageURL = urlImage.toString()
                uploadData()
                dialog.dismiss()
            }.addOnFailureListener {
                dialog.dismiss()
            }
        } else {
            Toast.makeText(this@UploadActivity, "No Image Selected", Toast.LENGTH_SHORT).show()
        }
    }

    private fun uploadData() {
        val title = binding.uploadTitle.text.toString()
        val desc = binding.uploadDesc.text.toString()
        val priority = binding.uploadPriority.text.toString()

        val dataClass = DataClass(title, desc, priority, imageURL, null, null)
        val currencyDate = DateFormat.getDateInstance().format(Calendar.getInstance().time)

        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://project-zgsoimxqzq-uc.a.run.app")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val retrofitAPI: RetrofitAPI = retrofit.create(RetrofitAPI::class.java)

        val file = uri?.let { getFile(this, it) }

        val requestFile: RequestBody? = file?.asRequestBody("image/*".toMediaTypeOrNull())
        val filePart = requestFile?.let {
            MultipartBody.Part.createFormData(
                "image",
                file?.name,
                it
            )
        }

        val call: Call<PredictResponse>? = filePart?.let { retrofitAPI.postData(it) }

        call?.enqueue(object : Callback<PredictResponse> {
            override fun onResponse(
                call: Call<PredictResponse>,
                response: Response<PredictResponse>
            ) {
                if (response.isSuccessful) {
                    val predictResponse: PredictResponse? = response.body()
                    val id = predictResponse?.nama
                    val description = predictResponse?.deskripsi
                    Toast.makeText(
                        this@UploadActivity,
                        "Nama: $id\nDeskripsi: $description",
                        Toast.LENGTH_SHORT
                    ).show()
                    // Tampilkan @+id/uploadImage, @+id/uploadTitle, @+id/uploadDesc, @+id/uploadPriority, nama, dan deskripsi
                    binding.uploadImage.setImageURI(uri)
                    binding.uploadTitle.setText(id)
                    binding.uploadDesc.setText(description)
                    binding.uploadPriority.setText(priority)
                } else {
                    Toast.makeText(
                        this@UploadActivity,
                        "Failed to get response",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                saveDataToFirebase(dataClass, currencyDate)
            }

            override fun onFailure(call: Call<PredictResponse>, t: Throwable) {
                Log.e("test", t.toString())
                Toast.makeText(
                    this@UploadActivity,
                    "Failed to communicate with the server",
                    Toast.LENGTH_SHORT
                ).show()
                saveDataToFirebase(dataClass, currencyDate)
            }
        })
    }

    @Throws(IOException::class)
    fun getFile(context: Context, uri: Uri): File? {
        val destinationFilename = File(
            (context.filesDir.path + File.separatorChar) + queryName(
                context,
                uri
            )
        )
        try {
            context.contentResolver.openInputStream(uri).use { ins ->
                if (ins != null) {
                    createFileFromStream(
                        ins,
                        destinationFilename
                    )
                }
            }
        } catch (ex: Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }
        return destinationFilename
    }

    private fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }
    }

    private fun queryName(context: Context, uri: Uri): String {
        val returnCursor: Cursor =
            context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    private fun saveDataToFirebase(dataClass: DataClass, currencyDate: String) {
        val databaseReference = FirebaseDatabase.getInstance().reference.child("DermaCare")
        val newDataRef = databaseReference.push()

        newDataRef.setValue(dataClass).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this@UploadActivity, "Data saved to Firebase", Toast.LENGTH_SHORT)
                    .show()
            } else {
                Toast.makeText(
                    this@UploadActivity,
                    "Failed to save data to Firebase",
                    Toast.LENGTH_SHORT
                ).show()
            }
            finish()
        }.addOnFailureListener { e ->
            Toast.makeText(
                this@UploadActivity,
                "Failed to save data to Firebase: ${e.message}",
                Toast.LENGTH_SHORT
            ).show()
            finish()
        }
    }
}