package com.emmanuelhmar.inventoryapp;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.Toast;

import com.emmanuelhmar.inventoryapp.data.ItemContract;
import com.emmanuelhmar.inventoryapp.data.ItemCursorAdapter;
import com.emmanuelhmar.inventoryapp.data.ItemDbHelper;

import java.io.ByteArrayOutputStream;

public class MainActivity extends AppCompatActivity {
    private ItemDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new ItemDbHelper(this);

        displayItems();
    }

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
//                Toast.makeText(this, "TESTING DUMMY", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void insertDummyPet() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues contentValues = new ContentValues();

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.panda);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);

        byte[] b = stream.toByteArray();
//        String image = Base64.encode(b);

        contentValues.put(ItemContract.ItemEntry.COLUMN_NAME_NAME, "cake");
        contentValues.put(ItemContract.ItemEntry.COLUMN_NAME_PRICE, 20);
        contentValues.put(ItemContract.ItemEntry.COLUMN_NAME_QUANTITY, 10);
        contentValues.put(ItemContract.ItemEntry.COLUMN_NAME_SUPPLIER, "NIKE");
        contentValues.put(ItemContract.ItemEntry.COLUMN_NAME_PICTURE, b);

        long row = db.insert(ItemContract.ItemEntry.TABLE_NAME, null, contentValues);

        if (row != 0) {
            Toast.makeText(this, "added " + row, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "error " + row, Toast.LENGTH_SHORT).show();
        }
    }

    private void displayItems() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {ItemContract.ItemEntry._ID, ItemContract.ItemEntry.COLUMN_NAME_NAME,
                ItemContract.ItemEntry.COLUMN_NAME_PRICE, ItemContract.ItemEntry.COLUMN_NAME_PICTURE};

        Cursor cursor = db.query(ItemContract.ItemEntry.TABLE_NAME, projection, null, null, null, null, null);

        ListView listView = (ListView) findViewById(R.id.list_item);

        ItemCursorAdapter cursorAdapter = new ItemCursorAdapter(this, cursor);

        listView.setAdapter(cursorAdapter);

        cursorAdapter.changeCursor(cursor);
    }
}