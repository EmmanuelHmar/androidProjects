package com.example.android.pets.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * {@link ContentProvider} for Pets app.
 */
public class PetProvider extends ContentProvider {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = PetProvider.class.getSimpleName();

    //    Database helper object
    private PetDbHelper dbHelper;

    private static final int PETS = 100;
    private static final int PET_ID = 101;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        dbHelper = new PetDbHelper(getContext());
        return true;
    }

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS, PETS);
        sUriMatcher.addURI(PetContract.CONTENT_AUTHORITY, PetContract.PATH_PETS + "/#", PET_ID);
    }

    /**
     * Perform the query for the given URI. Use the given projection, selection, selection arguments, and sort order.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        SQLiteDatabase db = dbHelper.getReadableDatabase();

//        Cursor to hold the query result
        Cursor cursor;

        int match = sUriMatcher.match(uri);

        switch (match) {
            case PETS:
//                Perform database query on the pets table
                cursor = db.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            case PET_ID:
//                Select statement
                selection = PetContract.PetEntry._ID + "=?";
//                Parse out the last path of the uri path
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

//                Select _id from pets where _id = PETS_ID
                cursor = db.query(PetContract.PetEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;

            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

//        Set notification URI on the cursor.
//        If the data at this URI changes, then we know e need to update the cursor.
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        int match = sUriMatcher.match(uri);

        switch (match) {
            case PETS:
                return insertPet(uri, contentValues);
            default:
                throw new IllegalArgumentException("Cannot query unknown uri " + uri);
        }
    }

    private Uri insertPet(Uri uri, ContentValues contentValues) {
        String name = contentValues.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        Integer gender = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
        Integer weight = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);

        if (name == null) {
            throw new IllegalArgumentException("Pet requires a name");
        } else if (gender == null || !PetContract.PetEntry.isValidGender(gender)) {
            throw new IllegalArgumentException("Pet requires a gender " + gender);
        } else if (weight == null && weight < 0) {
            throw new IllegalArgumentException("Pet requires a weight");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long id = db.insert(PetContract.PetEntry.TABLE_NAME, null, contentValues);

        if (id == -1) {
            Log.e(LOG_TAG, "Failed to insert row for " + uri);
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        return ContentUris.withAppendedId(uri, id);
    }


    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PETS:
                return updatePets(uri, contentValues, selection, selectionArgs);
            case PET_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updatePets(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updatePets(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {

//        If there are no values passed to change
        if (contentValues.size() == 0) {
            return 0;
        }

        String name = contentValues.getAsString(PetContract.PetEntry.COLUMN_PET_NAME);
        Integer gender = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_PET_GENDER);
        Integer weight = contentValues.getAsInteger(PetContract.PetEntry.COLUMN_PET_WEIGHT);

        if (contentValues.containsKey(name) && name == null) {
            throw new IllegalArgumentException("Pet name is required");
        }
        if (contentValues.containsKey(String.valueOf(gender))) {
            if (gender == null || !PetContract.PetEntry.isValidGender(gender)) {
                throw new IllegalArgumentException("Pet gender is required");
            }
        }

        if (contentValues.containsKey(String.valueOf(weight))) {
            if (weight == null || weight < 0) {
                throw new IllegalArgumentException("Pet weight is required");
            }
        }


        SQLiteDatabase db = dbHelper.getWritableDatabase();


        int rowsUpdated = db.update(PetContract.PetEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {


        final int match = sUriMatcher.match(uri);

//        getContext().getContentResolver().notifyChange(uri, null);

        switch (match) {
            case PETS:
                return deleteRows(uri, selection, selectionArgs);
            case PET_ID:
                selection = PetContract.PetEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteRows(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Delete is not supported for " + uri);
        }
    }

    private int deleteRows(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rowsDeleted = db.delete(PetContract.PetEntry.TABLE_NAME, selection, selectionArgs);

        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match) {
            case PETS:
                return PetContract.PetEntry.CONTENT_LIST_TYPE;
            case PET_ID:
                return PetContract.PetEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }
}
