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

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;
import com.example.android.pets.data.PetCursorAdapter;
import com.example.android.pets.data.PetDbHelper;

/**
 * Displays list of pets that were entered and stored in the app.
 */
public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private PetCursorAdapter adapter;
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

//        Get the list view
        ListView listView = (ListView) findViewById(R.id.list_view_pet);

//        Find and set the empty view on the listview, only shows when there's no list to show
        View emptyView = (View) findViewById(R.id.empty_view);
        listView.setEmptyView(emptyView);
        dbHelper = new PetDbHelper(this);
        getLoaderManager().initLoader(0, null, this);
//        displayDatabaseInfo();
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

//        Create a map of values, where column name are keys (& values)
        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, "Toto");
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, "Terrier");
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, PetContract.PetEntry.GENDER_MALE);
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, 7);

        Uri uri = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, values);

        Log.d(TAG, "insertPet: " + uri);
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
                deleteAllPetsDialog();
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

//        The returned cursor that was returned
        Cursor cursor = getContentResolver().query(PetContract.PetEntry.CONTENT_URI, projection, null, null, null);
        cursor.moveToNext();

//        Get the list view
        ListView listView = (ListView) findViewById(R.id.list_view_pet);

//        Initialize the cursorAdapter
        adapter = new PetCursorAdapter(this, cursor);

//        Set the adapter
        listView.setAdapter(adapter);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
//        This projection will specify which column from the database we will actually use in the query
        String[] projection = {PetContract.PetEntry._ID, PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED};
        //, PetContract.PetEntry.COLUMN_PET_GENDER,
        //       PetContract.PetEntry.COLUMN_PET_WEIGHT};

//    Filter results using selction
        String selection = PetContract.PetEntry.COLUMN_PET_GENDER + " ?=";
        String[] selectionArgs = {"0"};

//        Sort the results
        String sortOrder = PetContract.PetEntry.TABLE_NAME + " DESC";

//        The returned cursor that was returned
//        Cursor cursor = getContentResolver().query(PetContract.PetEntry.CONTENT_URI, projection, null, null, null);

        return new CursorLoader(getApplicationContext(), PetContract.PetEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
//        Get the list view
        ListView listView = (ListView) findViewById(R.id.list_view_pet);

//        Initialize the cursorAdapter
        adapter = new PetCursorAdapter(this, cursor);

//        Set the adapter
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Uri uri = ContentUris.withAppendedId(PetContract.PetEntry.CONTENT_URI, id);

                Log.d(TAG, "onItemClick: URI " + uri);
                Intent intent = new Intent(CatalogActivity.this, EditorActivity.class);

//                Include the URI id
                intent.setData(uri);

                startActivity(intent);

            }
        });


//        swap the new cursor in
        adapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
//        Called when the last cursor provided to onloadfinished above is about to be closed.
//        We need to make sure we are no longer using it
        adapter.swapCursor(null);

    }

    //    Dialog to ask if we want to delete all the pets in the database
    private void deleteAllPetsDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deletePets();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deletePets() {

        int deleteRows = getContentResolver().delete(PetContract.PetEntry.CONTENT_URI, null, null);

        Log.d(TAG, "deletePets: " + PetContract.PetEntry.CONTENT_URI);
        Log.d(TAG, "deletePets: Rows " + deleteRows);

        if (deleteRows != 0) {
            Toast.makeText(this, "Deleted pets", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error deleting pets", Toast.LENGTH_SHORT).show();
        }
    }
}
