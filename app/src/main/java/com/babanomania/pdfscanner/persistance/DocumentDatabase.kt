package com.babanomania.pdfscanner.persistance

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [Document::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class DocumentDatabase : RoomDatabase() {
    abstract fun documentDao(): DocumentDao
}


fun createDatabase(appContext: Context) =
    Room.databaseBuilder(appContext, DocumentDatabase::class.java, "documents.db")
        .addTypeConverter(Converters())
        .fallbackToDestructiveMigration()
        .build()
