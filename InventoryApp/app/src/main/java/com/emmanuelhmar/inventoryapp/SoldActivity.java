package com.emmanuelhmar.inventoryapp;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.CursorAdapter;
import android.widget.ListView;

import com.emmanuelhmar.inventoryapp.data.ItemContract;
import com.emmanuelhmar.inventoryapp.data.ItemCursorAdapter;

public class SoldActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private CursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold);

        getLoaderManager().initLoader(1, null, this);
    }

    //    The Cursor Loader
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {

        String[] projection = {ItemContract.ItemEntry._ID, ItemContract.ItemEntry.COLUMN_NAME_NAME,
                ItemContract.ItemEntry.COLUMN_NAME_PRICE, ItemContract.ItemEntry.COLUMN_NAME_QUANTITY,
                ItemContract.ItemEntry.COLUMN_NAME_SUPPLIER, ItemContract.ItemEntry.COLUMN_NAME_PICTURE};

        return new CursorLoader(getApplicationContext(), ItemContract.ItemEntry.SOLD_ITEMS_CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        ListView listView = findViewById(R.id.sold_view);
//        View view = findViewById(R.id.main_view);

        cursorAdapter = new ItemCursorAdapter(this, cursor);

//        listView.setEmptyView(view);
        listView.setAdapter(cursorAdapter);

//        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//
//                editOrDeleteDialog(id);
//            }
//        });

        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

        cursorAdapter.swapCursor(null);
    }
}
