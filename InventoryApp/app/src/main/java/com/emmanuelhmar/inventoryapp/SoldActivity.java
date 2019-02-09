package com.emmanuelhmar.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.emmanuelhmar.inventoryapp.data.ItemContract;
import com.emmanuelhmar.inventoryapp.data.ItemCursorAdapter;

public class SoldActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private CursorAdapter cursorAdapter;
    private String TAG = SoldActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sold);

//        invalidateOptionsMenu();

        setTitle(R.string.sold_shipments);

        getLoaderManager().initLoader(1, null, this);
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        MenuItem dummyItem = menu.findItem(R.id.dummy_item);
//        MenuItem cart = menu.findItem(R.id.save_item);
//
//        if (dummyItem != null && cart != null) {
//            cart.setVisible(false);
//            dummyItem.setVisible(false);
//        }
//
//        return super.onPrepareOptionsMenu(menu);
//    }


//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
////        MenuItem delete = menu.findItem(R.id.delete_item);
////        MenuItem cart = menu.findItem(R.id.buy_item);
////        MenuItem save = menu.findItem(R.id.save_item);
////
//////        if (getTitle().equals(getString(R.string.add_item))) {
//////            hide the delete button
//////            delete.setVisible(false);
////
//////            Hide the buy button
////            cart.setVisible(false);
//////        } else if (getTitle().equals(getString(R.string.edit_item))) {
//////            cart.setVisible(false);
//////        } else if (getTitle().equals(getString(R.string.buy_item))) {
////            save.setVisible(false);
//////            delete.setVisible(false);
//////        }
////
////        return super.onPrepareOptionsMenu(menu);
//    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem cart = menu.findItem(R.id.sales_item);
        MenuItem save = menu.findItem(R.id.dummy_item);
        cart.setVisible(false);
        save.setVisible(false);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (item.getItemId() == R.id.delete_all_items) {
            deleteDialog();
        }

        return super.onOptionsItemSelected(item);
    }

    private void deleteAllItems() {
        int rowsDeleted = getContentResolver().delete(ItemContract.ItemEntry.SOLD_ITEMS_CONTENT_URI, null, null);

        Toast.makeText(this,  "Items sold cleared", Toast.LENGTH_SHORT).show();

    }

    private void deleteDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.delete_all_items_message).setTitle(R.string.delete_all_items);
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteAllItems();
            }
        });

        builder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        builder.create().show();
    }

    //    The Cursor Loader
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {

        String[] projection = {ItemContract.ItemEntry._ID, ItemContract.ItemEntry.COLUMN_NAME_NAME,
                ItemContract.ItemEntry.COLUMN_NAME_PRICE, ItemContract.ItemEntry.COLUMN_NAME_QUANTITY,
                ItemContract.ItemEntry.COLUMN_NAME_SUPPLIER, ItemContract.ItemEntry.COLUMN_NAME_TOTAL,
                ItemContract.ItemEntry.COLUMN_NAME_PICTURE};

        return new CursorLoader(getApplicationContext(), ItemContract.ItemEntry.SOLD_ITEMS_CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, final Cursor cursor) {
        ListView listView = findViewById(R.id.sold_view);
//        View view = findViewById(R.id.main_view);

        cursorAdapter = new ItemCursorAdapter(this, cursor, "sales");

//        listView.setEmptyView(view);
        listView.setAdapter(cursorAdapter);

        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

        cursorAdapter.swapCursor(null);
    }

}
