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
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.android.pets.data.PetContract;

/**
 * Allows user to create a new pet or edit an existing one.
 */
public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = EditorActivity.class.getSimpleName();

    /**
     * EditText field to enter the pet's name
     */
    private EditText mNameEditText;

    /**
     * EditText field to enter the pet's breed
     */
    private EditText mBreedEditText;

    /**
     * EditText field to enter the pet's weight
     */
    private EditText mWeightEditText;

    /**
     * EditText field to enter the pet's gender
     */
    private Spinner mGenderSpinner;

    /**
     * Gender of the pet. The possible values are:
     * 0 for unknown gender, 1 for male, 2 for female.
     */
    private int mGender = 0;

    //    The Uri passed from CatalogActivity
    private Uri currentPetUri;

    //    The titleName of the activity
    private String addPetTitle;
    private String editPetTitle;

    private boolean petHasChanged;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        currentPetUri = getIntent().getData();

        if (currentPetUri == null) {
            setTitle(getString(R.string.add_pet_editor_activity));
            addPetTitle = getString(R.string.add_pet_editor_activity);
        } else {
            setTitle(getString(R.string.edit_pet_editor_activity));
            editPetTitle = getString(R.string.edit_pet_editor_activity);
        }

        // Find all relevant views that we will need to read user input from
        mNameEditText = (EditText) findViewById(R.id.edit_pet_name);
        mBreedEditText = (EditText) findViewById(R.id.edit_pet_breed);
        mWeightEditText = (EditText) findViewById(R.id.edit_pet_weight);
        mGenderSpinner = (Spinner) findViewById(R.id.spinner_gender);

//
        if (getTitle().equals(editPetTitle)) {
            getLoaderManager().initLoader(0, null, this);
        }

        if (getTitle().equals(addPetTitle)) {
//            Invalidate the options menu if activity is "Add a Pet"
            invalidateOptionsMenu();
        }

        setupSpinner();

        mNameEditText.setOnTouchListener(onTouchListener);
        mBreedEditText.setOnTouchListener(onTouchListener);
        mWeightEditText.setOnTouchListener(onTouchListener);
        mGenderSpinner.setOnTouchListener(onTouchListener);
    }

    /**
     * Setup the dropdown spinner that allows the user to select the gender of the pet.
     */
    private void setupSpinner() {
        // Create adapter for spinner. The list options are from the String array it will use
        // the spinner will use the default layout
        ArrayAdapter genderSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_gender_options, android.R.layout.simple_spinner_item);

        // Specify dropdown layout style - simple list view with 1 item per line
        genderSpinnerAdapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);

        // Apply the adapter to the spinner
        mGenderSpinner.setAdapter(genderSpinnerAdapter);

        // Set the integer mSelected to the constant values
        mGenderSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selection = (String) parent.getItemAtPosition(position);
                if (!TextUtils.isEmpty(selection)) {
                    if (selection.equals(getString(R.string.gender_male))) {
                        mGender = PetContract.PetEntry.GENDER_MALE; // Male
                    } else if (selection.equals(getString(R.string.gender_female))) {
                        mGender = PetContract.PetEntry.GENDER_FEMALE; // Female
                    } else {
                        mGender = PetContract.PetEntry.GENDER_UNKNOWN; // Unknown
                    }
                }
            }

            // Because AdapterView is an abstract class, onNothingSelected must be defined
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                mGender = 0; // Unknown
            }
        });
    }

    private void savePets() {

        String namePet = mNameEditText.getText().toString().trim();
        String breedPet = mBreedEditText.getText().toString().trim();
        int genderPet = mGender;
        String weightString = mWeightEditText.getText().toString().trim();

//        If the fields are empty, exit activity without saving anything
        if (TextUtils.isEmpty(namePet) &&
                TextUtils.isEmpty(breedPet) && TextUtils.isEmpty(weightString) && genderPet == PetContract.PetEntry.GENDER_UNKNOWN) {
            Toast.makeText(this, "Empty fields not saved", Toast.LENGTH_SHORT).show();
            return;
        }

//        Get the integer from the weight string
        int weight;
        weight = Integer.valueOf(weightString);

//        Content Values key-pair
        ContentValues values = new ContentValues();
        values.put(PetContract.PetEntry.COLUMN_PET_NAME, namePet);
        values.put(PetContract.PetEntry.COLUMN_PET_BREED, breedPet);
        values.put(PetContract.PetEntry.COLUMN_PET_GENDER, genderPet);
        values.put(PetContract.PetEntry.COLUMN_PET_WEIGHT, weight);

//        Add the pet to database if "Add a Pet"
        if (getTitle().equals(addPetTitle)) {
//        Return the currentPetUri after running the insert query
            Uri insertURI = getContentResolver().insert(PetContract.PetEntry.CONTENT_URI, values);

//        Parse the last path of the URI, which is the id
            long newRowID = ContentUris.parseId(insertURI);

            if (newRowID == -1) {
                Toast.makeText(this, R.string.editor_insert_pet_failed, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, R.string.editor_insert_pet_successful, Toast.LENGTH_LONG).show();
            }
        }
//        Else if the activity is "Edit Pet"
        else {
            int updatePet = getContentResolver().update(currentPetUri, values, null, null);

            if (updatePet != 0) {
                Toast.makeText(this, "Pet updated ", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Error updating pet ", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
//        return super.onPrepareOptionsMenu(menu);

//        Set the save button to invisible when "Adding a Pet"
        if (currentPetUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                // Do nothing for now
                savePets();
                finish();
                return true;
            // Respond to a click on the "Delete" menu option
            case R.id.action_delete:
                // Do nothing for now
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case android.R.id.home:
                // Navigate back to parent activity (CatalogActivity)

//                If the pet hasn't changed, continue w/ navigating to the parent activity
                if (!petHasChanged) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

//                Else if there are unsaved changes, setup the dialog to warn the user
                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
//                        User clicked "discard button", navigate to parent activity
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };

                showUnsavedChangedDialog(discardButtonClickListener);

                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {

        String[] projection = {PetContract.PetEntry._ID, PetContract.PetEntry.COLUMN_PET_NAME,
                PetContract.PetEntry.COLUMN_PET_BREED, PetContract.PetEntry.COLUMN_PET_GENDER, PetContract.PetEntry.COLUMN_PET_WEIGHT};

        return new CursorLoader(getApplicationContext(), currentPetUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor.moveToFirst()) {

            String weight = cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_WEIGHT));

            mNameEditText.setText(cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_NAME)));
            mBreedEditText.setText(cursor.getString(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_BREED)));
            mWeightEditText.setText(weight);

            mGender = cursor.getInt(cursor.getColumnIndex(PetContract.PetEntry.COLUMN_PET_GENDER));
            mGenderSpinner.setSelection(mGender);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mNameEditText.getText().clear();
        mBreedEditText.getText().clear();
        mWeightEditText.getText().clear();
    }

    //    OnTouchListener that listens for any user touches on a view, change the pet boolean to true
    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            petHasChanged = true;
            return false;
        }
    };

    private void showUnsavedChangedDialog(DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            //            User clicked "keep editing" editing, dismiss and continue
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

    @Override
    public void onBackPressed() {
        if (!petHasChanged) {
            super.onBackPressed();
            return;
        }

        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                user clicked "Discard" button, close the current activity
                finish();
            }
        };

        showUnsavedChangedDialog(discardButtonClickListener);

    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                User clicked "delete" button, so delete the pet.
                deletePet();
            }
        });

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
//                "Cancel" button is clicked, dismiss the dialog
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    //    Delete the pet from the database
    private void deletePet() {

        if (currentPetUri != null) {
            int rowsDeleted = getContentResolver().delete(currentPetUri, null, null);

            if (rowsDeleted != 0) {
                Toast.makeText(this, "Pet deleted ", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error deleting pet ", Toast.LENGTH_SHORT).show();
            }

            finish();
        }
    }
}