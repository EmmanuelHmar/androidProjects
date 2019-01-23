/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.pets;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity {

    private static final String TAG = CatalogActivity.class.getSimpleName();
    PetDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_catalog);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        dbHelper = new PetDbHelper(this);
        displayDatabaseInfo();
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_catalog.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_catalog, menu);
        return true;
    }

    private void insertPet() {
//        Get the database in write mode
        SQLiteDatabase db = dbHelper.getWritableDatabase();

//        Create a map of values, where column name are keys (& values)

        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, PetContract.PetEntry.GENDER_MALE);
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, 7);

        long newRowId = db.insert(PetContract.PetEntry.TABLE_NAME, null, values);

        Log.d(TAG, "insertPet: " + newRowId);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertPet();
                displayDatabaseInfo();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                // Do nothing for now
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void displayDatabaseInfo() {

//        This projection will specify which column from the database we will actually use in the query
        String[] projection = {PetContract.PetEntry._ID, PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED, PetContract.PetEntry.COLUMN_PET_GENDER,
                PetContract.PetEntry.COLUMN_PET_WEIGHT};

//    Filter results using selction
        String selection = PetContract.PetEntry.COLUMN_PET_GENDER + " ?=";
        String[] selectionArgs = {"0"};

//        Sort the results
        String sortOrder = PetContract.PetEntry.TABLE_NAME + " DESC";

        Cursor cursor = getContentResolver().query(PetContract.PetEntry.CONTENT_URI, projection, null, null, null);

//        Get it as rows in the database
//        Cursor cursor = db.query(PetContract.PetEntry.TABLE_NAME, projection, null, null, null
//                , null
//                , null);


        Log.d(TAG, "displayDatabaseInfo: " + cursor.toString());

        int getID = cursor.getColumnIndex(PetContract.PetEntry._ID);
        int getName = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME);
        int getBreed = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED);
        int getGender = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_GENDER);
        int getWeight = cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT);

        try {
            TextView displayView = (TextView) findViewById(R.id.text_view_pet);
            displayView.setText("Number of rows in pets database table: " + cursor.getCount());

            displayView.append("\n" + PetContract.PetEntry._ID + " - " + PetContract.PetEntry.COLUMN_PET_NAME +
                    " - " + PetContract.PetEntry.COLUMN_PET_BREED + " - " + PetContract.PetEntry.GENDER_FEMALE +
                    " - " + PetContract.PetEntry.COLUMN_PET_WEIGHT + "\n");

            while (cursor.moveToNext()) {
                int id = cursor.getInt(getID);
                String name = cursor.getString(getName);
                String breed = cursor.getString(getBreed);
                int gender = cursor.getInt(getGender);
                int weight = cursor.getInt(getWeight);

                displayView.append("\n" + id + " - " + name + " - " + breed + " - " + gender + " - " +
                        weight);
            }


        } finally {
            cursor.close();
        }

    }
}
