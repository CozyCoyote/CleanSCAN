package com.babanomania.pdfscanner.persistance

import androidx.room.ProvidedTypeConverter
import androidx.room.TypeConverter

@ProvidedTypeConverter
class Converters {

    @TypeConverter
    fun fromPaths(value: String?): List<String>? {
        return if (value.isNullOrEmpty()) null else value.split(",")
    }

    @TypeConverter
    fun fromUri(paths: List<String>?): String? {
        return paths?.joinToString(",")
    }

}
