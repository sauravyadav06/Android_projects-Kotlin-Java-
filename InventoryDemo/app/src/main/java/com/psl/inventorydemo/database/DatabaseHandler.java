package com.psl.inventorydemo.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;

import com.psl.inventorydemo.helper.AppConstants;
import com.psl.inventorydemo.helper.AssetUtils;
import com.psl.inventorydemo.model.AssetMaster;
import com.psl.inventorydemo.model.AssetTypeMaster;

import java.util.ArrayList;
import java.util.List;


public class DatabaseHandler extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "ADP_ASSET_TRACKING";
    private static final String TABLE_ASSET_MASTER = "Asset_Master_Table";
    private static final String TABLE_ASSET_TYPE_MASTER = "Asset_Type_Master_Table";
    private static final String K_ASSET_ID = "K_ASSET_ID";
    private static final String K_ASSET_NAME = "K_ASSET_NAME";
    private static final String K_ASSET_TYPE = "K_ASSET_TYPE";
    private static final String K_ASSET_TYPE_ID = "K_ASSET_TYPE_ID";
    private static final String K_ASSET_SERIAL_NUMBER = "K_ASSET_SERIAL_NUMBER";
    private static final String K_EPC = "K_EPC";
    private static final String K_ITEM_NAME = "K_ITEM_NAME";
    private static final String K_ITEM_CODE = "K_ITEM_CODE";

    public DatabaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ASSET_MASTER_TABLE = "CREATE TABLE "
                + TABLE_ASSET_MASTER
                + "("
                + K_ASSET_ID + " TEXT UNIQUE,"//0
                + K_ASSET_NAME + " TEXT,"//1
                + K_ASSET_TYPE_ID + " TEXT,"//1
                + K_ASSET_TYPE + " TEXT,"//1
                + K_ASSET_SERIAL_NUMBER + " TEXT,"//1
                + K_EPC + " TEXT"//1
                + ")";
        String CREATE_ASSET_TYPE_MASTER_TABLE = "CREATE TABLE "
                + TABLE_ASSET_TYPE_MASTER
                + "("
                + K_ASSET_TYPE_ID + " TEXT ,"//0
                + K_ASSET_NAME + " TEXT"//1
                + ")";

        db.execSQL(CREATE_ASSET_MASTER_TABLE);
        db.execSQL(CREATE_ASSET_TYPE_MASTER_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSET_MASTER);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ASSET_TYPE_MASTER);
    }
    public void storeAssetMaster(List<AssetMaster> lst) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT OR REPLACE INTO Asset_Master_Table (K_ASSET_ID,K_ASSET_NAME,K_ASSET_TYPE_ID,K_ASSET_TYPE,K_ASSET_SERIAL_NUMBER,K_EPC) VALUES (? ,?, ?, ?, ?, ?)";
        //db.beginTransaction();
        db.beginTransactionNonExclusive();
        SQLiteStatement stmt = db.compileStatement(sql);
        try {
            for (int i = 0; i < lst.size(); i++) {
                stmt.bindString(1, ""+lst.get(i).getAssetID());
                stmt.bindString(2, lst.get(i).getAssetName());
                stmt.bindString(3, lst.get(i).getAssetTypeID());
                stmt.bindString(4, lst.get(i).getAssetType());
                stmt.bindString(5, lst.get(i).getAssetSerialNo());
                stmt.bindString(6, lst.get(i).getAssetTagID());
                stmt.execute();
                stmt.clearBindings();
            }
            db.setTransactionSuccessful();
            // db.endTransaction();
        } catch (Exception e) {
            Log.e("ASSETMASTEREXC", e.getMessage());
        } finally {
            if (db != null) {
                db.endTransaction();
            }
        }
    }
    public void storeAssetTypeMaster(List<AssetTypeMaster> lst) {
        SQLiteDatabase db = this.getWritableDatabase();
        String sql = "INSERT OR REPLACE INTO Asset_Type_Master_Table (K_ASSET_TYPE_ID,K_ASSET_NAME) VALUES (? ,?)";
        //db.beginTransaction();
        db.beginTransactionNonExclusive();
        SQLiteStatement stmt = db.compileStatement(sql);
        try {
            for (int i = 0; i < lst.size(); i++) {
                stmt.bindString(1, ""+lst.get(i).getAssetTypeID());
                stmt.bindString(2, lst.get(i).getAssetName());
                stmt.execute();
                stmt.clearBindings();
            }
            db.setTransactionSuccessful();
            // db.endTransaction();
        } catch (Exception e) {
            Log.e("ASSETTYPEMASTEREXC", e.getMessage());
        } finally {
            if (db != null) {
                db.endTransaction();
            }
        }
    }
    public void deleteAssetMaster() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ASSET_MASTER, null, null);
        db.close();
    }
    public void deleteAssetTypeMaster() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ASSET_TYPE_MASTER, null, null);
        db.close();
    }
    public ArrayList<String> getAssetType(){
        ArrayList<String> assetType = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT " + K_ASSET_NAME + " FROM " + TABLE_ASSET_TYPE_MASTER +
                " ORDER BY " + K_ASSET_TYPE_ID + " ASC";
        Cursor cursor = null;
        try {
            cursor = db.rawQuery(selectQuery, null);
            if (cursor.moveToFirst()) {
                do {
                    String assetName = cursor.getString(cursor.getColumnIndexOrThrow(K_ASSET_NAME));
                    assetType.add(assetName);
                } while (cursor.moveToNext());
            }
        } finally {
            // Ensure the cursor is closed to prevent memory leaks
            if (cursor != null) {
                cursor.close();
            }
            db.close(); // Optionally close the database if you're done with it
        }
        return assetType;
    }
    public boolean IsExistAsset(String assetTypeID){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            // Define the condition
            String condition = K_ASSET_TYPE_ID + " = ?";
            String[] selectionArgs = {assetTypeID};

            // Query the database
            cursor = db.query(
                    TABLE_ASSET_MASTER,
                    null, // Columns (null means all columns)
                    condition,
                    selectionArgs,
                    null, // groupBy
                    null, // having
                    null // orderBy
            );

            // Check if there are any rows
            return cursor != null && cursor.getCount() > 0;
        } catch (Exception e) {
            Log.e("ASSETTYPEMASTEREXC", e.getMessage());
            return false;
        } finally {
            // Close the cursor and database
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
    }
    public String getAssetTypeIDByAssetTypeName(String AssetTypeName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ASSET_TYPE_MASTER, new String[]{K_ASSET_TYPE_ID}, K_ASSET_NAME + "='" + AssetTypeName +"'", null, null, null, null);
        System.out.println("Cursor" + cursor.getCount());
        try {
            if (cursor == null || cursor.getCount() == 0) {
                return AppConstants.UNKNOWN_ASSET;
            } else {
                if (cursor.getCount() > 0) {
                    //PID Note
                    cursor.moveToFirst();
                    return cursor.getString(cursor.getColumnIndexOrThrow(K_ASSET_TYPE_ID));
                } else {
                    //PID Not Found
                    return AppConstants.UNKNOWN_ASSET;
                }
            }
        } catch (Exception e) {
            return AppConstants.UNKNOWN_ASSET;
        } finally {
            cursor.close();
        }
    }
    public String getAssetNameByTagID(String tagId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ASSET_MASTER, new String[]{K_ASSET_NAME}, K_EPC + "='" + tagId + "'", null, null, null, null);
        System.out.println("Cursor" + cursor.getCount());
        try {
            if (cursor == null || cursor.getCount() == 0) {
                return AppConstants.UNKNOWN_ASSET;
            } else {
                if (cursor.getCount() > 0) {
                    //PID Note
                    cursor.moveToFirst();
                    return cursor.getString(cursor.getColumnIndexOrThrow(K_ASSET_NAME));
                } else {
                    //PID Not Found
                    return AppConstants.UNKNOWN_ASSET;
                }
            }
        } catch (Exception e) {
            return AppConstants.UNKNOWN_ASSET;
        } finally {
            cursor.close();
        }
    }
    public AssetMaster getAssetMasterByTagId(String epc) {
        AssetMaster assetMasters = new AssetMaster();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT  * FROM " + TABLE_ASSET_MASTER + " WHERE "+ K_EPC +" = '"+epc+"';";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String assetid = cursor.getString(cursor.getColumnIndexOrThrow(K_ASSET_ID));
                String assetname = cursor.getString(cursor.getColumnIndexOrThrow(K_ASSET_NAME));
                String assettype = cursor.getString(cursor.getColumnIndexOrThrow(K_ASSET_TYPE));
                String assettypeid = cursor.getString(cursor.getColumnIndexOrThrow(K_ASSET_TYPE_ID));
                String aserialno = cursor.getString(cursor.getColumnIndexOrThrow(K_ASSET_SERIAL_NUMBER));
                String atagid = cursor.getString(cursor.getColumnIndexOrThrow(K_EPC));
                assetMasters.setAssetID(assetid);
                assetMasters.setAssetName(assetname);
                assetMasters.setAssetType(assettype);
                assetMasters.setAssetTypeID(assettypeid);
                assetMasters.setAssetSerialNo(aserialno);
                assetMasters.setAssetTagID(atagid);
            } while (cursor.moveToNext());
        }
        return assetMasters;
    }
    public ArrayList<String> getAssetNameByAssetType(String assetType) {
        ArrayList<String> assetNameList = new ArrayList<>();
        SQLiteDatabase db = this.getWritableDatabase();
        String selectQuery = "SELECT "+K_ASSET_NAME+" FROM " + TABLE_ASSET_MASTER +" WHERE "+K_ASSET_TYPE+" = '"+assetType+"';";
        Cursor cursor = db.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                String assetName = cursor.getString(cursor.getColumnIndexOrThrow(K_ASSET_NAME));
                assetNameList.add(assetName);
            } while (cursor.moveToNext());
        }
        return assetNameList;
    }
    public String getAssetTypeIDByAssetType(String assetType) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ASSET_TYPE_MASTER, new String[]{K_ASSET_TYPE_ID}, K_ASSET_NAME + "='" + assetType + "'", null, null, null, null);
        System.out.println("Cursor" + cursor.getCount());
        try {
            if (cursor == null || cursor.getCount() == 0) {
                return AppConstants.UNKNOWN_ASSET;
            } else {
                if (cursor.getCount() > 0) {
                    //PID Note
                    cursor.moveToFirst();
                    return cursor.getString(cursor.getColumnIndexOrThrow(K_ASSET_TYPE_ID));
                } else {
                    //PID Not Found
                    return AppConstants.UNKNOWN_ASSET;
                }
            }
        } catch (Exception e) {
            return AppConstants.UNKNOWN_ASSET;
        } finally {
            cursor.close();
        }
    }
    public String getAssetSerialNumberByAssetName(String assetName) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ASSET_MASTER, new String[]{K_ASSET_SERIAL_NUMBER}, K_ASSET_NAME + "='" + assetName + "'", null, null, null, null);
        System.out.println("Cursor" + cursor.getCount());
        try {
            if (cursor == null || cursor.getCount() == 0) {
                return AppConstants.UNKNOWN_ASSET;
            } else {
                if (cursor.getCount() > 0) {
                    //PID Note
                    cursor.moveToFirst();
                    return cursor.getString(cursor.getColumnIndexOrThrow(K_ASSET_SERIAL_NUMBER));
                } else {
                    //PID Not Found
                    return AppConstants.UNKNOWN_ASSET;
                }
            }
        } catch (Exception e) {
            return AppConstants.UNKNOWN_ASSET;
        } finally {
            cursor.close();
        }
    }
    public String getAssetTypeNameByAssetTagID(String assetTagID) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ASSET_MASTER, new String[]{K_ASSET_TYPE}, K_EPC + "='" + assetTagID + "'", null, null, null, null);
        System.out.println("Cursor" + cursor.getCount());
        try {
            if (cursor == null || cursor.getCount() == 0) {
                return AppConstants.UNKNOWN_ASSET;
            } else {
                if (cursor.getCount() > 0) {
                    //PID Note
                    cursor.moveToFirst();
                    return cursor.getString(cursor.getColumnIndexOrThrow(K_ASSET_TYPE));
                } else {
                    //PID Not Found
                    return AppConstants.UNKNOWN_ASSET;
                }
            }
        } catch (Exception e) {
            return AppConstants.UNKNOWN_ASSET;
        } finally {
            cursor.close();
        }
    }
}
