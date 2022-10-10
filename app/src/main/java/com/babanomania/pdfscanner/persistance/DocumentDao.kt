package com.babanomania.pdfscanner.persistance

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface DocumentDao {

    @Query("SELECT * FROM document WHERE pending like :pending")
    fun pendingDocument(pending: Boolean): LiveData<Document?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun save(document: Document)

    @Update
    fun update(document: Document)

    @Delete
    fun delete(document: Document)

}
