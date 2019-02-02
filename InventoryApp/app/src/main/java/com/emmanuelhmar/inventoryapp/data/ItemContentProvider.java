package com.emmanuelhmar.inventoryapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class ItemContentProvider extends ContentProvider {

    private ItemDbHelper dbHelper;

    //    Create a UriMatcher object
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //    The code for the uriMatcher
    private static final int ITEMS = 100;
    private static final int ITEM_ID = 101;

    static {
//       addUri maps an authority and path to the int values
        uriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS, ITEMS);
        uriMatcher.addURI(ItemContract.CONTENT_AUTHORITY, ItemContract.PATH_ITEMS + "/#", ITEM_ID);
    }

    @Override
    public boolean onCreate() {
        dbHelper = new ItemDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        Cursor cursor;

        switch (uriMatcher.match(uri)) {
            case ITEMS:
                cursor = db.query(ItemContract.ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case ITEM_ID:
//                Select statement
                selection = ItemContract.ItemEntry.TABLE_ID + "=?";
//             Parse out the last path of the uri path
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ItemContract.ItemEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown " + uri);
        }

//        Set the notification URI on the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }

    //    Get the MIME type
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);

        switch (match) {
            case ITEMS:
                return ItemContract.ItemEntry.CONTENT_LIST_TYPE;
            case ITEM_ID:
                return ItemContract.ItemEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " unknown match " + match);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {

        int match = uriMatcher.match(uri);

        switch (match) {
//            Can only insert 1 item at a time
            case ITEMS:
                return insertItem(uri, contentValues);
            default:
                throw new IllegalArgumentException("Error inserting pet");
        }
    }

    private Uri insertItem(Uri uri, ContentValues contentValues) {
        String name = contentValues.getAsString(ItemContract.ItemEntry.COLUMN_NAME_NAME);
        Integer price = contentValues.getAsInteger(ItemContract.ItemEntry.COLUMN_NAME_PRICE);
        Integer quantity = contentValues.getAsInteger(ItemContract.ItemEntry.COLUMN_NAME_QUANTITY);
        String supplier = contentValues.getAsString(ItemContract.ItemEntry.COLUMN_NAME_SUPPLIER);

        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Name is not valid");
        } else if (price == null || price < 0) {
            throw new IllegalArgumentException("Price is invalid");
        } else if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Quantity is invalid");
        } else if (supplier == null) {
            throw new IllegalArgumentException("Supplier is invalid");
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long newRow = db.insert(ItemContract.ItemEntry.TABLE_NAME, null, contentValues);

        if (newRow != -1) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return ContentUris.withAppendedId(uri, newRow);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int match = uriMatcher.match(uri);

        switch (match) {
            case ITEMS:
                return deletePets(uri, selection, selectionArgs);
            case ITEM_ID:
                selection = ItemContract.ItemEntry.TABLE_ID + "?=";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deletePets(uri, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Error deleting item " + uri);
        }
    }

    private int deletePets(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        int rows = db.delete(ItemContract.ItemEntry.TABLE_NAME, selection, selectionArgs);

//        Notify the cursor change when item(s) are deleted
        if (rows != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rows;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }
}
