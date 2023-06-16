package com.example.dermacare

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.SearchView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dermacare.databinding.ActivityKameraBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class KameraActivity : AppCompatActivity() {

    private lateinit var binding: ActivityKameraBinding
    private lateinit var dataList: ArrayList<DataClass>
    private lateinit var adapter: MyAdapter
    var databaseReference: DatabaseReference? = null
    var eventListener: ValueEventListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityKameraBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Mengatur layout manager untuk RecyclerView dengan menggunakan GridLayoutManager
        val gridLayoutManager = GridLayoutManager(this@KameraActivity, 1)
        binding.recyclearView99999.layoutManager = LinearLayoutManager(this@KameraActivity)

        // Membuat dialog progres
        val builder = AlertDialog.Builder(this@KameraActivity)
        builder.setCancelable(false)
        builder.setView(R.layout.progress_layout)
        val dialog = builder.create()
        dialog.show()

        // Inisialisasi dataList, adapter, dan databaseReference
        dataList = ArrayList()
        adapter = MyAdapter(this@KameraActivity, dataList)
        binding.recyclearView99999.adapter = adapter
        databaseReference = FirebaseDatabase.getInstance().getReference("DermaCare")

        // Menampilkan dialog progres
        dialog.show()

        // Menambahkan event listener pada databaseReference untuk mendapatkan data dari Firebase Database
        eventListener = databaseReference!!.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Menghapus data yang ada pada dataList
                dataList.clear()
                // Mengiterasi setiap child pada snapshot
                for (itemSnapshot in snapshot.children) {
                    // Mengambil nilai dari child dan mengubahnya menjadi objek DataClass
                    val dataClass = itemSnapshot.getValue(DataClass::class.java)
                    if (dataClass != null) {
                        // Menambahkan objek DataClass ke dataList
                        dataList.add(dataClass)
                    }
                }
                // Memberi tahu adapter bahwa data telah berubah
                adapter.notifyDataSetChanged()
                // Menutup dialog progres
                dialog.dismiss()
            }

            override fun onCancelled(error: DatabaseError) {
                // Menutup dialog progres jika terjadi error
                dialog.dismiss()
            }
        })

        // Menambahkan click listener pada tombol Floating Action Button (fab)
        binding.fab.setOnClickListener {
            val intent = Intent(this, UploadActivity::class.java)
            startActivity(intent)
        }

        // Menambahkan listener pada SearchView untuk melakukan pencarian saat teks berubah
        binding.search.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                // Memanggil fungsi searchList untuk mencari dan memperbarui dataList berdasarkan teks baru
                searchList(newText)
                return true
            }
        })
    }

    // Fungsi untuk mencari dan memperbarui dataList berdasarkan teks yang dimasukkan
    fun searchList(text: String) {
        val searchList = ArrayList<DataClass>()
        for (dataClass in dataList) {
            if (dataClass.dataTitle?.lowercase()?.contains(text.lowercase()) == true) {
                // Jika judul dataClass mengandung teks yang dicari, tambahkan dataClass ke searchList
                searchList.add(dataClass)
            }
        }
        // Memanggil fungsi searchDataList pada adapter untuk memperbarui dataList dengan hasil pencarian
        adapter.searchDataList(searchList)
    }
}