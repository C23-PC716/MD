package com.example.dermacare

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

// Kelas adapter untuk RecyclerView
class MyAdapter(
    private val context: android.content.Context,
    private var dataList: List<DataClass>,
) : RecyclerView.Adapter<MyViewHolder>() {

    // Fungsi ini dipanggil saat RecyclerView membutuhkan ViewHolder baru
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        // Membuat tampilan ViewHolder menggunakan file layout "recycler_item.xml"
        val view: View =
            LayoutInflater.from(parent.context).inflate(R.layout.recycler_item, parent, false)
        return MyViewHolder(view)
    }

    // Fungsi ini dipanggil untuk mengikat data pada posisi tertentu ke ViewHolder
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        // Menggunakan Glide untuk memuat gambar dari URL ke ImageView
        Glide.with(context).load(dataList[position].dataImage).into(holder.recImage)
        // Mengatur teks pada TextView berdasarkan data yang ada pada posisi tersebut
        holder.recTitle.text = dataList[position].dataTitle
        holder.recDesc.text = dataList[position].dataDesc
        holder.recPriority.text = dataList[position].dataPriority
        holder.namaPenyakit.text = dataList[position].nama
        holder.deskripsiPenyakit.text = dataList[position].deskripsi

        // Menambahkan klik listener pada CardView
        holder.recCard.setOnClickListener {
            // Membuat intent untuk membuka DetailActivity dan mengirim data yang diperlukan
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra("Image", dataList[holder.adapterPosition].dataImage)
            intent.putExtra("Description", dataList[holder.adapterPosition].dataDesc)
            intent.putExtra("Title", dataList[holder.adapterPosition].dataTitle)
            intent.putExtra("Priority", dataList[holder.adapterPosition].dataPriority)
            context.startActivity(intent)
        }
    }

    // Fungsi ini mengembalikan jumlah item dalam RecyclerView
    override fun getItemCount(): Int {
        return dataList.size
    }

    // Fungsi untuk memperbarui data respons
    fun updateRespons(namaList: List<String>, deskripsiList: List<String>) {
        // Tidak ada implementasi yang diberikan dalam contoh kode
        // perlu mengimplementasikan logika pembaruan respons sesuai kebutuhan
        notifyDataSetChanged()
    }

    // Fungsi untuk mencari dan memperbarui dataList dengan hasil pencarian
    fun searchDataList(searchList: List<DataClass>) {
        dataList = searchList
        notifyDataSetChanged()
    }
}

// Kelas ViewHolder untuk RecyclerView
class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    var recImage: ImageView
    var recTitle: TextView
    var recDesc: TextView
    var recPriority: TextView
    var recCard: CardView
    var namaPenyakit: TextView
    var deskripsiPenyakit: TextView

    init {
        // Menginisialisasi elemen-elemen tampilan yang digunakan dalam ViewHolder
        recImage = itemView.findViewById(R.id.recImage)
        recTitle = itemView.findViewById(R.id.recTitle)
        recDesc = itemView.findViewById(R.id.recDesc)
        recPriority = itemView.findViewById(R.id.recPriority)
        recCard = itemView.findViewById(R.id.recCard)
        namaPenyakit = itemView.findViewById(R.id.namaPenyakit)
        deskripsiPenyakit = itemView.findViewById(R.id.deskripsiPenyakit)
    }
}
