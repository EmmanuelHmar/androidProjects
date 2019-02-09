package com.emmanuelhmar.inventoryapp.data;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.emmanuelhmar.inventoryapp.R;


public class ItemCursorAdapter extends CursorAdapter {
    private String state = "unsold";
    private String supplier;

    public ItemCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    public ItemCursorAdapter(Context context, Cursor c, String state) {
        super(context, c, 0);
        this.state = state;
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
//        byte[] blob = cursor.getBlob(cursor.getColumnIndexOrThrow("picture"));
        String blob = cursor.getString(cursor.getColumnIndexOrThrow("picture"));

        setSupplier(supplier);

//        Bitmap bitmap = BitmapFactory.decodeByteArray(blob, 0, blob.length);

        TextView total = view.findViewById(R.id.item_total);
        TextView totalHolder = view.findViewById(R.id.total_holder);
        TextView tracking = view.findViewById(R.id.item_tracking);
        TextView trackingHolder = view.findViewById(R.id.tracking_holder);

//        If we are not on sales page, then hide the total price views
        if (state.equals("sales")) {
            int amount = cursor.getInt(cursor.getColumnIndexOrThrow("total"));
            String sum = "$" + amount;
            total.setText(sum);

            int max = 900000;
            int min = 100000;
            int range = max - min + 1;
            tracking.setText(String.valueOf((int) ((Math.random() * range) + min)));
        } else {
            total.setVisibility(View.GONE);
            totalHolder.setVisibility(View.GONE);
            tracking.setVisibility(View.GONE);
            trackingHolder.setVisibility(View.GONE);
        }

        itemName.setText(name);
        itemPrice.setText(String.valueOf("$"+ price));
        itemQuantity.setText(String.valueOf(quantity));
        itemSupplier.setText(supplier);
        itemImage.setImageURI(Uri.parse(blob));
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
}
