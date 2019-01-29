package com.emmanuelhmar.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ItemDbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "items.db";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + ItemContract.ItemEntry.TABLE_NAME +
            " (" + ItemContract.ItemEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT ," +
            ItemContract.ItemEntry.COLUMN_NAME_PRICE + " INTEGER NOT NULL," + ItemContract.ItemEntry.COLUMN_NAME_QUANTITY +
            " INTEGER NOT NULL DEFAULT 0," + ItemContract.ItemEntry.COLUMN_NAME_SUPPLIER + " TEXT NOT NULL," +
            ItemContract.ItemEntry.COLUMN_NAME_PICTURE + "BLOB);";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + ItemContract.ItemEntry.TABLE_NAME;

    public ItemDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
//        This database is a cache for online data, to upgrade = discard the data and start over
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
