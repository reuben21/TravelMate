package com.project22.myapplication.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

/**
 * Let's start by creating our database CRUD helper class
 * based on the SQLiteHelper.
 */
class DatabaseHelper(context: Context) :
        SQLiteOpenHelper(context, DATABASE_NAME, null, 2) {
    /**
     * Our onCreate() method.
     * Called when the database is created for the first time. This is
     * where the creation of tables and the initial population of the tables
     * should happen.
     */
    override fun onCreate(db: SQLiteDatabase) {
    db.execSQL("CREATE TABLE IF NOT EXISTS $TABLE_NAME (ID TEXT PRIMARY KEY ,EMAIL_ID TEXT, FIRSTNAME TEXT, LASTNAME TEXT, PROFILE_IMAGE_URL TEXT, BIRTHDATE TEXT, PHONE_NO TEXT)")
    }

    /**
     * Let's create Our onUpgrade method
     * Called when the database needs to be upgraded. The implementation should
     * use this method to drop tables, add tables, or do anything else it needs
     * to upgrade to the new schema version.
     */
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME)
        onCreate(db)
    }

    /**
     * Let's create our insertData() method.
     * It Will insert data to SQLIte database.
     */
    fun insertData(id:String, email_id: String, firstName: String,lastName: String,profileImageUrl: String,birthDate: String, phoneNo: String) {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("ID", id)
        contentValues.put("EMAIL_ID", email_id)
        contentValues.put("FIRSTNAME", firstName)
        contentValues.put("LASTNAME", lastName)
        contentValues.put("PROFILE_IMAGE_URL", profileImageUrl)
        contentValues.put("BIRTHDATE", birthDate)
        contentValues.put("PHONE_NO", phoneNo)



        db.insert(TABLE_NAME, null, contentValues)
    }

    /**
     * Let's create  a method to update a row with new field values.
     */
    fun updateData(id:String,email_id: String, firstName: String,lastName: String,profileImageUrl: String,birthDate: String, phoneNo: String):
            Boolean {
        val db = this.writableDatabase
        val contentValues = ContentValues()
        contentValues.put("ID", id)
        contentValues.put("EMAIL_ID", email_id)
        contentValues.put("FIRSTNAME", firstName)
        contentValues.put("LASTNAME", lastName)
        contentValues.put("PROFILE_IMAGE_URL", profileImageUrl)
        contentValues.put("BIRTHDATE", birthDate)
        contentValues.put("PHONE_NO", phoneNo)

        db.update(TABLE_NAME, contentValues, "ID = ?", arrayOf(id))
        return true
    }

    /**
     * Let's create a function to delete a given row based on the id.
     */
    fun deleteData(id : String) : Int {
        val db = this.writableDatabase
        return db.delete(TABLE_NAME,"ID = ?", arrayOf(id))
    }

    /**
     * The below getter property will return a Cursor containing our dataset.
     */
    val allData : Cursor
        get() {
            val db = this.writableDatabase
            val res = db.rawQuery("SELECT * FROM " + TABLE_NAME, null)
            return res
        }

    /**
     * Let's create a companion object to hold our static fields.
     * A Companion object is an object that is common to all instances of a given
     * class.
     */
    companion object {
        val DATABASE_NAME = "travlemate.db"
        val TABLE_NAME = "USER_TABLE"



    }
}