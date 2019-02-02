package com.emmanuelhmar.inventoryapp.data;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.emmanuelhmar.inventoryapp.R;


public class ItemCursorAdapter extends CursorAdapter {

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    //    Inflate a new view and return it
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.item_entry, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView itemName = view.findViewById(R.id.item_name);
        TextView itemPrice = view.findViewById(R.id.item_price);
        TextView itemQuantity = view.findViewById(R.id.item_quantity);
        TextView itemSupplier = view.findViewById(R.id.item_supplier);
        ImageView itemImage = view.findViewById(R.id.item_image);

        String name = cursor.getString(cursor.getColumnIndexOrThrow("name"));
        int price = cursor.getInt(cursor.getColumnIndexOrThrow("price"));
        int quantity = cursor.getInt(cursor.getColumnIndexOrThrow("quantity"));
        String supplier = cursor.getString(cursor.getColumnIndexOrThrow("supplier"));
        byte[] blob = cursor.getBlob(cursor.getColumnIndexOrThrow("picture"));

        Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);

        itemName.setText(name);
        itemPrice.setText(String.valueOf("$"+ price));
        itemQuantity.setText(String.valueOf(quantity));
        itemSupplier.setText(supplier);
        itemImage.setImageBitmap(bitmap);
    }
}
