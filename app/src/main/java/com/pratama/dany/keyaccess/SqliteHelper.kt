package com.pratama.dany.keyaccess

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.content.ContentValues



class SqliteHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    override fun onCreate(db: SQLiteDatabase?) {
        db?.execSQL(SQL_CREATE_NIGHTMODE)
        db?.execSQL(SQL_CREATE_FINGERPRINT)
        db?.execSQL(SQL_CREATE_AUTOMATICALLY)
        db?.execSQL(SQL_CREATE_NOTIFICATION)
    }

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL(SQL_DELETE_NIGHTMODE)
        db?.execSQL(SQL_DELETE_FINGERPRINT)
        db?.execSQL(SQL_DELETE_AUTOMATICALLY)
        db?.execSQL(SQL_DELETE_NOTIFICATION)
        onCreate(db)
    }

    override fun onDowngrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        onUpgrade(db, oldVersion, newVersion)
    }

    fun insertStatusNightMode(id: Int, sts: String): Boolean? {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(SqliteContract.NightModeEntry.COLUMN_NIGHT_ID, id)
        contentValues.put(SqliteContract.NightModeEntry.COLUMN_STATUS, sts)

        val result = db.insert(SqliteContract.NightModeEntry.TABLE_NAME, null, contentValues)

        if(result.toInt() == -1){
            return false
        } else {
            return true
        }

    }

    fun updateStatusNightMode(id: Int, sts: String): Boolean? {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(SqliteContract.NightModeEntry.COLUMN_STATUS, sts)

        val result = db.update(SqliteContract.NightModeEntry.TABLE_NAME, contentValues,
            SqliteContract.NightModeEntry.COLUMN_NIGHT_ID + " = '" + id + "'", null)

        if(result.toInt() == -1){
            return false
        } else {
            return true
        }

    }

    fun getStatusNightMode(id: Int): String {
        var sts = "-"
        val query = "SELECT " + SqliteContract.NightModeEntry.COLUMN_STATUS + " FROM " +
                SqliteContract.NightModeEntry.TABLE_NAME + " WHERE " +
                SqliteContract.NightModeEntry.COLUMN_NIGHT_ID + " = '" + id + "'"
        val db = writableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                sts = cursor.getString(cursor.getColumnIndex(SqliteContract.NightModeEntry.COLUMN_STATUS))
            } while (cursor.moveToNext())
        }
        db.close()
        return sts
    }

    fun insertStatusFingerprint(): Boolean? {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(SqliteContract.FingerprintEntry.COLUMN_NIGHT_ID, 21)
        contentValues.put(SqliteContract.FingerprintEntry.COLUMN_STATUS, "T")

        val result = db.insert(SqliteContract.FingerprintEntry.TABLE_NAME, null, contentValues)

        return result.toInt() != -1

    }

    fun insertStatusFingerprint(id: Int, sts: String): Boolean? {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(SqliteContract.FingerprintEntry.COLUMN_NIGHT_ID, id)
        contentValues.put(SqliteContract.FingerprintEntry.COLUMN_STATUS, sts)

        val result = db.insert(SqliteContract.FingerprintEntry.TABLE_NAME, null, contentValues)

        return result.toInt() != -1

    }

    fun updateStatusFingerprint(id: Int, sts: String): Boolean? {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(SqliteContract.FingerprintEntry.COLUMN_STATUS, sts)

        val result = db.update(SqliteContract.FingerprintEntry.TABLE_NAME, contentValues,
            SqliteContract.FingerprintEntry.COLUMN_NIGHT_ID + " = '" + id + "'", null)

        return result.toInt() != -1

    }

    fun getStatusFingerprint(id: Int): String {
        var sts = "-"
        val query = "SELECT " + SqliteContract.FingerprintEntry.COLUMN_STATUS + " FROM " +
                SqliteContract.FingerprintEntry.TABLE_NAME + " WHERE " +
                SqliteContract.FingerprintEntry.COLUMN_NIGHT_ID + " = '" + id + "'"
        val db = writableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                sts = cursor.getString(cursor.getColumnIndex(SqliteContract.FingerprintEntry.COLUMN_STATUS))
            } while (cursor.moveToNext())
        }
        db.close()
        return sts
    }

    fun insertStatusAutomatically(): Boolean? {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(SqliteContract.OpenAutomaticallyEntry.COLUMN_NIGHT_ID, 21)
        contentValues.put(SqliteContract.OpenAutomaticallyEntry.COLUMN_STATUS, "T")

        val result = db.insert(SqliteContract.OpenAutomaticallyEntry.TABLE_NAME, null, contentValues)

        return result.toInt() != -1

    }

    fun insertStatusAutomatically(id: Int, sts: String): Boolean? {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(SqliteContract.OpenAutomaticallyEntry.COLUMN_NIGHT_ID, id)
        contentValues.put(SqliteContract.OpenAutomaticallyEntry.COLUMN_STATUS, sts)

        val result = db.insert(SqliteContract.OpenAutomaticallyEntry.TABLE_NAME, null, contentValues)

        return result.toInt() != -1

    }

    fun updateStatusAutomatically(id: Int, sts: String): Boolean? {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(SqliteContract.OpenAutomaticallyEntry.COLUMN_STATUS, sts)

        val result = db.update(SqliteContract.OpenAutomaticallyEntry.TABLE_NAME, contentValues,
            SqliteContract.OpenAutomaticallyEntry.COLUMN_NIGHT_ID + " = '" + id + "'", null)

        return result.toInt() != -1

    }

    fun getStatusAutomatically(id: Int): String {
        var sts = "-"
        val query = "SELECT " + SqliteContract.OpenAutomaticallyEntry.COLUMN_STATUS + " FROM " +
                SqliteContract.OpenAutomaticallyEntry.TABLE_NAME + " WHERE " +
                SqliteContract.OpenAutomaticallyEntry.COLUMN_NIGHT_ID + " = '" + id + "'"
        val db = writableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                sts = cursor.getString(cursor.getColumnIndex(SqliteContract.OpenAutomaticallyEntry.COLUMN_STATUS))
            } while (cursor.moveToNext())
        }
        db.close()
        return sts
    }

    fun insertStatusNotification(): Boolean? {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(SqliteContract.NotificationEntry.COLUMN_NIGHT_ID, 21)
        contentValues.put(SqliteContract.NotificationEntry.COLUMN_STATUS, "T")

        val result = db.insert(SqliteContract.NotificationEntry.TABLE_NAME, null, contentValues)

        return result.toInt() != -1

    }

    fun insertStatusNotification(id: Int, sts: String): Boolean? {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(SqliteContract.NotificationEntry.COLUMN_NIGHT_ID, id)
        contentValues.put(SqliteContract.NotificationEntry.COLUMN_STATUS, sts)

        val result = db.insert(SqliteContract.NotificationEntry.TABLE_NAME, null, contentValues)

        return result.toInt() != -1

    }

    fun updateStatusNotification(id: Int, sts: String): Boolean? {
        val db = writableDatabase
        val contentValues = ContentValues()
        contentValues.put(SqliteContract.NotificationEntry.COLUMN_STATUS, sts)

        val result = db.update(SqliteContract.NotificationEntry.TABLE_NAME, contentValues,
            SqliteContract.NotificationEntry.COLUMN_NIGHT_ID + " = '" + id + "'", null)

        return result.toInt() != -1

    }

    fun getStatusNotification(id: Int): String {
        var sts = "-"
        val query = "SELECT " + SqliteContract.NotificationEntry.COLUMN_STATUS + " FROM " +
                SqliteContract.NotificationEntry.TABLE_NAME + " WHERE " +
                SqliteContract.NotificationEntry.COLUMN_NIGHT_ID + " = '" + id + "'"
        val db = writableDatabase
        val cursor = db.rawQuery(query, null)

        if (cursor.moveToFirst()) {
            do {
                sts = cursor.getString(cursor.getColumnIndex(SqliteContract.NotificationEntry.COLUMN_STATUS))
            } while (cursor.moveToNext())
        }
        db.close()
        return sts
    }

    companion object {

        val DATABASE_VERSION = 1
        val DATABASE_NAME = "keyaccess.db"

        private val SQL_CREATE_NIGHTMODE =
            "CREATE TABLE " + SqliteContract.NightModeEntry.TABLE_NAME + " (" +
                    SqliteContract.NightModeEntry.COLUMN_NIGHT_ID + " INTEGER PRIMARY KEY, " +
                    SqliteContract.NightModeEntry.COLUMN_STATUS + " TEXT)"

        private val SQL_DELETE_NIGHTMODE = "DROP TABLE IF EXISTS " + SqliteContract.NightModeEntry.TABLE_NAME

        private val SQL_CREATE_FINGERPRINT =
            "CREATE TABLE " + SqliteContract.FingerprintEntry.TABLE_NAME + " (" +
                    SqliteContract.FingerprintEntry.COLUMN_NIGHT_ID + " INTEGER PRIMARY KEY, " +
                    SqliteContract.FingerprintEntry.COLUMN_STATUS + " TEXT)"

        private val SQL_DELETE_FINGERPRINT = "DROP TABLE IF EXISTS " + SqliteContract.NightModeEntry.TABLE_NAME

        private val SQL_CREATE_AUTOMATICALLY =
            "CREATE TABLE " + SqliteContract.OpenAutomaticallyEntry.TABLE_NAME + " (" +
                    SqliteContract.OpenAutomaticallyEntry.COLUMN_NIGHT_ID + " INTEGER PRIMARY KEY, " +
                    SqliteContract.OpenAutomaticallyEntry.COLUMN_STATUS + " TEXT)"

        private val SQL_DELETE_AUTOMATICALLY = "DROP TABLE IF EXISTS " + SqliteContract.OpenAutomaticallyEntry.TABLE_NAME

        private val SQL_CREATE_NOTIFICATION =
            "CREATE TABLE " + SqliteContract.NotificationEntry.TABLE_NAME + " (" +
                    SqliteContract.NotificationEntry.COLUMN_NIGHT_ID + " INTEGER PRIMARY KEY, " +
                    SqliteContract.NotificationEntry.COLUMN_STATUS + " TEXT)"

        private val SQL_DELETE_NOTIFICATION = "DROP TABLE IF EXISTS " + SqliteContract.NotificationEntry.TABLE_NAME

    }

}