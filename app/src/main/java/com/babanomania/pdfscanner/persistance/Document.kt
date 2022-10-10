package com.babanomania.pdfscanner.persistance

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Document(
    @PrimaryKey(autoGenerate = true)
    var documentId: Int? = null,
    var name: String? = null,
    var path: String? = null,
    //contains only the file names, located in the cache folder. Uri's are too difficult of a concept for Android to comprehend
    var pages: List<String>? = null,
    @field:ColumnInfo(name = "pending")
    var pending: Boolean = true,
)
