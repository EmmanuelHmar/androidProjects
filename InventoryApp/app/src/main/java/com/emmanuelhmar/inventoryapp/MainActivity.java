package com.emmanuelhmar.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.emmanuelhmar.inventoryapp.data.ItemContract;
import com.emmanuelhmar.inventoryapp.data.ItemCursorAdapter;
import com.emmanuelhmar.inventoryapp.data.ItemDbHelper;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private ItemDbHelper dbHelper;
    private ItemCursorAdapter cursorAdapter;
    private boolean editOrDelete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, EditorActivity.class);
                intent.putExtra("title", "Add item");

                startActivity(intent);
            }
        });

        dbHelper = new ItemDbHelper(this);
//        Initiate the loader
        getLoaderManager().initLoader(0, null, this);
    }

    //    Inflate the menu options
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.dummy_item:
                insertDummyPet();
                displayItems();
                return true;
            case R.id.delete_all_items:
                deleteConfirmationDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //    delete all the items in the database
    private void deleteAllItems() {

        int rows = getContentResolver().delete(ItemContract.ItemEntry.CONTENT_URI, null, null);

        if (rows != 0) {
            Toast.makeText(this, "Deleted all items", Toast.LENGTH_SHORT).show();
        }
    }

    private void insertDummyPet() {
        ContentValues contentValues = new ContentValues();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.panda);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 70, stream);

        byte[] b = stream.toByteArray();

        contentValues.put(ItemContract.ItemEntry.COLUMN_NAME_NAME, "cake");
        contentValues.put(ItemContract.ItemEntry.COLUMN_NAME_PRICE, 20);
        contentValues.put(ItemContract.ItemEntry.COLUMN_NAME_QUANTITY, 10);
        contentValues.put(ItemContract.ItemEntry.COLUMN_NAME_SUPPLIER, "NIKE");
        contentValues.put(ItemContract.ItemEntry.COLUMN_NAME_PICTURE, b);

        Uri uri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, contentValues);

        if (uri != null) {
            Toast.makeText(getApplicationContext(), "added item", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "error ", Toast.LENGTH_SHORT).show();
        }
    }

//    Display the items on the listView
    private void displayItems() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {ItemContract.ItemEntry._ID, ItemContract.ItemEntry.COLUMN_NAME_NAME,
                ItemContract.ItemEntry.COLUMN_NAME_QUANTITY, ItemContract.ItemEntry.COLUMN_NAME_SUPPLIER,
                ItemContract.ItemEntry.COLUMN_NAME_PRICE, ItemContract.ItemEntry.COLUMN_NAME_PICTURE};

        Cursor cursor = db.query(ItemContract.ItemEntry.TABLE_NAME, projection, null, null, null, null, null);

        ListView listView = findViewById(R.id.list_item);
        View view = findViewById(R.id.main_view);

        cursorAdapter = new ItemCursorAdapter(this, cursor);

        listView.setEmptyView(view);
        listView.setAdapter(cursorAdapter);
    }

//    The Cursor Loader
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {

        String[] projection = {ItemContract.ItemEntry._ID, ItemContract.ItemEntry.COLUMN_NAME_NAME,
                ItemContract.ItemEntry.COLUMN_NAME_PRICE, ItemContract.ItemEntry.COLUMN_NAME_QUANTITY,
                ItemContract.ItemEntry.COLUMN_NAME_SUPPLIER, ItemContract.ItemEntry.COLUMN_NAME_PICTURE};

        return new CursorLoader(getApplicationContext(), ItemContract.ItemEntry.CONTENT_URI, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor cursor) {
        ListView listView = findViewById(R.id.list_item);
        View view = findViewById(R.id.main_view);

        cursorAdapter = new ItemCursorAdapter(this, cursor);

        listView.setEmptyView(view);
        listView.setAdapter(cursorAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {

                editOrDeleteDialog(id);
            }
        });

        cursorAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {

        cursorAdapter.swapCursor(null);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayItems();
    }

    @Override
    protected void onDestroy() {
        dbHelper.close();
        super.onDestroy();
    }

    //    Create an alertDialog when the delete all pets button is clicked
    private void deleteConfirmationDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setMessage(getString(R.string.delete_all_items_message)).setTitle(R.string.confirm_delete);

//        Add the buttons
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(MainActivity.this, "Items deleted", Toast.LENGTH_SHORT).show();
                deleteAllItems();
            }
        });

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void editOrDeleteDialog(final long id) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setMessage("Edit or Buy item").setTitle("Buy or Edit");

        builder.setPositiveButton("Edit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent intent = new Intent(getApplicationContext(), EditorActivity.class);

                Uri uri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, id);

                intent.putExtra("title", "Edit item");
                intent.setData(uri);

                startActivity(intent);
            }
        });

        builder.setNegativeButton("Buy", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                editOrDelete = true;
                Intent intent = new Intent(getApplicationContext(), EditorActivity.class);

                Uri uri = ContentUris.withAppendedId(ItemContract.ItemEntry.CONTENT_URI, id);

                intent.putExtra("title", getString(R.string.buy_item));
                intent.setData(uri);

                startActivity(intent);
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
