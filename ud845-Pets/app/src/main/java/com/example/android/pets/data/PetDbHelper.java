package com.example.android.pets.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class PetDbHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "pets.db";

    private static final String SQL_CREATE_ENTRIES = "CREATE TABLE " + PetContract.PetEntry.TABLE_NAME +
            " (" + PetContract.PetEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            PetContract.PetEntry.COLUMN_PET_NAME + " TEXT NOT NULL, " + PetContract.PetEntry.COLOMN_PET_BREED +
            " TEXT, " + PetContract.PetEntry.COLUMN_PET_GENDER + " TEXT NOT NULL, " +
            PetContract.PetEntry.COLUMN_PET_WEIGHT + " INTEGER NOT NULL DEFAULT 0);";

    private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS " + PetContract.PetEntry.TABLE_NAME;

    public PetDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

//    Create the database
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

//    If you want to upgrade/make changes to the database
    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}