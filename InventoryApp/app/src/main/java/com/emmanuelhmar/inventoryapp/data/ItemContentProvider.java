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
                throw new IllegalArgumentException("Cannot query unknown " + uri) ;
        }

//        Set the notification URI on the cursor
        cursor.setNotificationUri(getContext().getContentResolver(), uri);

        return cursor;
    }


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
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }
}
