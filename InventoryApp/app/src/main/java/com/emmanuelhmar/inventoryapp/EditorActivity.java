package com.emmanuelhmar.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.emmanuelhmar.inventoryapp.data.ItemContract;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class EditorActivity extends AppCompatActivity {
    private static final String TAG = EditorActivity.class.getSimpleName();
    private final int PICK_IMAGE_REQUEST = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

    }

    private void saveItemData() {
        TextInputEditText item_name = findViewById(R.id.item_name);
        TextInputEditText item_price = findViewById(R.id.item_price);
        TextInputEditText item_quantity = findViewById(R.id.item_quantity);
        TextInputEditText item_supplier = findViewById(R.id.item_supplier);
        ImageView item_image = findViewById(R.id.item_image);

        String name = item_name.getText().toString().trim();
        int price = Integer.valueOf(item_price.getText().toString().trim());
        int quantity = Integer.valueOf(item_quantity.getText().toString().trim());
        String supplier = item_supplier.getText().toString().trim();

//        Convert the image to blob
        Bitmap bitmap = ((BitmapDrawable) item_image.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.WEBP, 50, stream);
        byte[] blob = stream.toByteArray();

        ContentValues values = new ContentValues();

        values.put(ItemContract.ItemEntry.COLUMN_NAME_NAME, name);
        values.put(ItemContract.ItemEntry.COLUMN_NAME_PRICE, price);
        values.put(ItemContract.ItemEntry.COLUMN_NAME_QUANTITY, quantity);
        values.put(ItemContract.ItemEntry.COLUMN_NAME_SUPPLIER, supplier);
        values.put(ItemContract.ItemEntry.COLUMN_NAME_PICTURE, blob);

        Uri uri = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);

        if (uri != null) {
            Toast.makeText(getApplicationContext(), "Item inserted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getApplicationContext(), "Error inserting", Toast.LENGTH_SHORT).show();
        }

        finish();
    }


    //    Get an image from gallery when the button is pressed, this is called when image is returned
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                Log.d(TAG, "onActivityResult: URI: " + imageUri);
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    ImageView image = findViewById(R.id.item_image);

//                    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.panda);
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.WEBP, 50, stream);
                    InputStream inputStream = this.getContentResolver().openInputStream(imageUri);
                    image.setImageBitmap(decodeSampleBitmapFromResource(inputStream, imageUri, 150, 150));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
//        If the save button is hit
        switch (item.getItemId()) {
            case R.id.save_item:
                Toast.makeText(getApplicationContext(), "ITEM HIT", Toast.LENGTH_SHORT).show();
                saveItemData();
                return true;
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void buttonClick(View view) {
//        Toast.makeText(EditorActivity.this, "Button CLicked", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onClick: Button");
        Intent intent = new Intent(Intent.ACTION_PICK);
//        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        String[] mimeTypes = {"image/jpeg", "image/png"};
        intent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes);
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        startActivityForResult(Intent.createChooser(intent, "Select Image"),
                PICK_IMAGE_REQUEST);
    }

    //    Load a scaled down version
    public int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
//        Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

//            Calculate the largest sample value that is a power of 2 and keeps both height and width
//            larger than the requested height and width
            while ((halfHeight / inSampleSize) >= reqHeight
                    && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;

    }

    //    public Bitmap decodeSampleBitmapFromResource(Resources res, int resId, int reqWidth, int reqHeight) throws FileNotFoundException {
    public Bitmap decodeSampleBitmapFromResource(InputStream inputStream, Uri uri, int reqWidth, int reqHeight) throws FileNotFoundException {

//        First decode with InJustDecodeBounds = true to check the dimensions
//        InputStream inputStream = this.getContentResolver().openInputStream(uri);
        Log.d(TAG, "decodeSampleBitmapFromResource: INPUTSTREAM : " + inputStream);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);
//            BitmapFactory.decodeStream(inputStream, null, options);
//        BitmapFactory.decodeResource(res, resId, options);

//        Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

//        decode bitmap with insamplesize set
        options.inJustDecodeBounds = false;

//        return BitmapFactory.decodeResource(res, resId, options);

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);
//        return BitmapFactory.decodeStream(inputStream, null, options);

        return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, o2);
    }
}
