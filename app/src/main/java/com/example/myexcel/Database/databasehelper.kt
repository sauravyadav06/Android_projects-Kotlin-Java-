package com.example.myexcel.Database

import android.content.Context
import android.content.ContentValues
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import android.util.Log

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "truck_data.db" // Database name
        const val DATABASE_VERSION = 4// Version number for upgrades
        const val TRANSACTION_TABLE = "transaction_table"
        const val SKU_MASTER_TABLE = "sku_master_table"

        // Columns for the transaction table
        const val COL_ID = "id"
        const val COL_TRUCK_NO = "truck_no"
        const val COL_SKU_NAME = "sku_name"
        const val COL_SKU_CODE = "sku_code"
        const val COL_IN_DATE = "in_date"
        const val COL_IN_TIME = "in_time"
        const val COL_OUT_DATE = "out_date"
        const val COL_OUT_TIME = "out_time"
        const val COL_QR_SCAN_TYPE = "qr_Scan_Type"
        const val COL_LOADING_TYPE = "loading_Type"
        const val COL_QUANTITY="quantity"
    }

    // Create tables in the database
    override fun onCreate(db: SQLiteDatabase?) {
        val createTransactionTable = """
            CREATE TABLE $TRANSACTION_TABLE (
                $COL_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COL_TRUCK_NO TEXT,
                $COL_SKU_CODE TEXT,
                $COL_SKU_NAME TEXT,
                $COL_IN_DATE TEXT,
                $COL_IN_TIME TEXT,
                $COL_OUT_DATE TEXT,
                $COL_OUT_TIME TEXT,
                $COL_QR_SCAN_TYPE TEXT,
                $COL_LOADING_TYPE TEXT,
                $COL_QUANTITY TEXT
            )
        """
        val createSKUMasterTable = """
            CREATE TABLE $SKU_MASTER_TABLE (
                $COL_SKU_NAME TEXT NOT NULL,
                $COL_SKU_CODE TEXT NOT NULL
            )
        """
        db?.execSQL(createTransactionTable)
        db?.execSQL(createSKUMasterTable)
    }

    // Handle database upgrades
    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db?.execSQL("DROP TABLE IF EXISTS $TRANSACTION_TABLE")
        db?.execSQL("DROP TABLE IF EXISTS $SKU_MASTER_TABLE")
        onCreate(db) // Recreate tables
    }

    // Insert transaction data
    fun insertData(transaction: TransactionData): Boolean {
        val db = writableDatabase
        val values = ContentValues()
        values.put(COL_TRUCK_NO, transaction.truckNo)
        values.put(COL_SKU_NAME, transaction.skuName)
        values.put(COL_SKU_CODE, transaction.skuCode)
        values.put(COL_IN_DATE, transaction.inDate)
        values.put(COL_IN_TIME, transaction.inTime)
        values.put(COL_OUT_DATE, transaction.outDate)
        values.put(COL_OUT_TIME, transaction.outTime)
        values.put(COL_QR_SCAN_TYPE, transaction.qrScanType)
        values.put(COL_LOADING_TYPE, transaction.loadingType)
        values.put(COL_QUANTITY, transaction.quantity)
        val result = db.insert(TRANSACTION_TABLE, null, values)
        db.close()
        return result != -1L
    }


    // Insert SKU data into the SKU Master table
    fun storeSKUMaster(skuList: List<SKU>) {
        val db = writableDatabase
        db.beginTransaction() // Start a transaction for bulk insert
        try {
            for (sku in skuList) {
                if (!sku.skuName.isNullOrBlank() && !sku.skuCode.isNullOrBlank()) {
                    val values = ContentValues()
                    values.put(COL_SKU_NAME, sku.skuName)
                    values.put(COL_SKU_CODE, sku.skuCode)
                    db.insert(SKU_MASTER_TABLE, null, values)
                }
            }
            db.setTransactionSuccessful() // Commit the transaction
        } catch (e: Exception) {
            e.printStackTrace() // Log the error
            Log.e("DBEr", e.printStackTrace().toString())
        } finally {
            db.endTransaction() // End the transaction
            db.close() // Close the database
        }
    }

    // Delete all data from SKU Master table
    fun deleteSKUMaster() {
        val db = writableDatabase
        db.delete(SKU_MASTER_TABLE, null, null)
        db.close()
    }

    // Delete all transaction data
    fun deleteTransactionData() {
        val db = writableDatabase
        db.delete(TRANSACTION_TABLE, null, null)
        db.close()
    }




    // Fetch SKU Name by SKU Code
    fun getSKUNameBySKUCode(skuCode: String): String {
        val db = readableDatabase
        val query = "SELECT $COL_SKU_NAME FROM $SKU_MASTER_TABLE WHERE $COL_SKU_CODE = ?"
        val cursor = db.rawQuery(query, arrayOf(skuCode))
        var skuName = "UNKNOWN"
        try {
            if (cursor.moveToFirst()) {
                skuName = cursor.getString(cursor.getColumnIndexOrThrow(COL_SKU_NAME))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
            db.close()
        }
        return skuName
    }

    // Fetch all SKU Codes
    fun getAllSKUCode(): List<String> {
        val skuCodesList = mutableListOf<String>()
        val db = readableDatabase
        val query = "SELECT $COL_SKU_CODE FROM $SKU_MASTER_TABLE ORDER BY $COL_SKU_CODE ASC"
        val cursor = db.rawQuery(query, null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    val skuCode = cursor.getString(cursor.getColumnIndexOrThrow(COL_SKU_CODE))
                    skuCodesList.add(skuCode)
                } while (cursor.moveToNext())
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            cursor.close()
            db.close()
        }
        return skuCodesList
    }

    // Fetch all transaction data
    fun getAllTransactions(): List<TransactionData> {
        val transactionList = mutableListOf<TransactionData>()
        val db = readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TRANSACTION_TABLE", null)
        try {
            if (cursor.moveToFirst()) {
                do {
                    val transaction = TransactionData(
                        truckNo = cursor.getString(cursor.getColumnIndexOrThrow(COL_TRUCK_NO)),
                        skuName = cursor.getString(cursor.getColumnIndexOrThrow(COL_SKU_NAME)),
                        skuCode = cursor.getString(cursor.getColumnIndexOrThrow(COL_SKU_CODE)),
                        inDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_IN_DATE)),
                        inTime = cursor.getString(cursor.getColumnIndexOrThrow(COL_IN_TIME)),
                        outDate = cursor.getString(cursor.getColumnIndexOrThrow(COL_OUT_DATE)),
                        outTime = cursor.getString(cursor.getColumnIndexOrThrow(COL_OUT_TIME)),
                        qrScanType = cursor.getString(cursor.getColumnIndexOrThrow(COL_QR_SCAN_TYPE)),
                        loadingType = cursor.getString(cursor.getColumnIndexOrThrow(COL_LOADING_TYPE)),
                        quantity = cursor.getString(cursor.getColumnIndexOrThrow(COL_QUANTITY)),

                    )
                    transactionList.add(transaction)
                } while (cursor.moveToNext())
            }
        } finally {
            cursor.close()
            db.close()
        }
        return transactionList
    }

}


