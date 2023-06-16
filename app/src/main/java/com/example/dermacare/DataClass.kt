package com.example.dermacare

class DataClass {
    var dataTitle: String? = null
    var dataDesc: String? = null
    var dataPriority: String? = null
    var dataImage: String? = null
    var nama: String? = null
    var deskripsi: String? = null

    constructor(
        dataTitle: String?,
        dataDesc: String?,
        dataPriority: String?,
        dataImage: String?,
        nama: String?,
        deskripsi: String?
    ) {
        this.dataTitle = dataTitle
        this.dataDesc = dataDesc
        this.dataPriority = dataPriority
        this.dataImage = dataImage
        this.nama = nama
        this.deskripsi = deskripsi
    }

    constructor() {}
}
