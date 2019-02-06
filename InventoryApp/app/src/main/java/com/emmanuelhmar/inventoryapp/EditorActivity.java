
package com.emmanuelhmar.inventoryapp;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.emmanuelhmar.inventoryapp.data.ItemContract;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String TAG = EditorActivity.class.getSimpleName();
    private final int PICK_IMAGE_REQUEST = 1;
    private Uri uri;
    private TextInputEditText item_name;
    private TextInputEditText item_price;
    private TextInputEditText item_quantity;
    private TextInputEditText item_supplier;
    private ImageView item_image;
    private boolean itemWasTouched;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

//        get the data passed from MainActivity
        uri = getIntent().getData();

//        Set the title from data that was passed
        setTitle(getIntent().getStringExtra("title"));

        item_name = findViewById(R.id.item_name);
        item_price = findViewById(R.id.item_price);
        item_quantity = findViewById(R.id.item_quantity);
        item_supplier = findViewById(R.id.item_supplier);
        item_image = findViewById(R.id.item_image);

//        Set the onTouchListener for when item is touched
        item_name.setOnTouchListener(itemTouched);
        item_price.setOnTouchListener(itemTouched);
        item_quantity.setOnTouchListener(itemTouched);
        item_supplier.setOnTouchListener(itemTouched);
        item_image.setOnTouchListener(itemTouched);

//        If the data passed is null, set the Activity title to "Add Item" instead
        if (getTitle().equals(getString(R.string.add_item))) {
            invalidateOptionsMenu();
        } else {
            invalidateOptionsMenu();
            getLoaderManager().initLoader(0, null, this);
        }
    }


    private void saveItemData() {

//        TODO: add errors for views here

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

        if (getTitle().equals(getString(R.string.add_item))) {

            Uri newURI = getContentResolver().insert(ItemContract.ItemEntry.CONTENT_URI, values);

            if (newURI != null) {
                Toast.makeText(getApplicationContext(), "Item inserted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error inserting", Toast.LENGTH_SHORT).show();
            }
            finish();
        } else if (getTitle().equals(getString(R.string.edit_item))) {
            int updatedRow = getContentResolver().update(uri, values, null, null);

            if (updatedRow != 0) {
                Toast.makeText(this, "Item updated", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "error updating", Toast.LENGTH_SHORT).show();
            }

            finish();
        } else if (getTitle().equals(getString(R.string.buy_item))) {
//            Need to add the total price for the bought items tables
            int totalPrice = price * quantity;

            values.put(ItemContract.ItemEntry.COLUMN_NAME_TOTAL,totalPrice);

            Uri newUri = getContentResolver().insert(ItemContract.ItemEntry.SOLD_ITEMS_CONTENT_URI, values);

            Log.d(TAG, "saveItemData: NEWURI : " + newUri);

            if (newUri != null) {
                Toast.makeText(getApplicationContext(), "Item inserted", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Error inserting", Toast.LENGTH_SHORT).show();
            }
            finish();
        }
    }

    //    Get an image from gallery when the button is pressed, this is called when image is returned
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if (requestCode == PICK_IMAGE_REQUEST) {
            if (resultCode == RESULT_OK) {
                Uri imageUri = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                    ImageView image = findViewById(R.id.item_image);

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
    public boolean onPrepareOptionsMenu(Menu menu) {

        MenuItem delete = menu.findItem(R.id.delete_item);
        MenuItem cart = menu.findItem(R.id.buy_item);
        MenuItem save = menu.findItem(R.id.save_item);

        if (getTitle().equals(getString(R.string.add_item))) {
//            hide the delete button
            delete.setVisible(false);

//            Hide the buy button
            cart.setVisible(false);
        } else if (getTitle().equals(getString(R.string.edit_item))) {
            cart.setVisible(false);
        } else if (getTitle().equals(getString(R.string.buy_item))) {
            save.setVisible(false);
            delete.setVisible(false);
        }

        return super.onPrepareOptionsMenu(menu);
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
                saveItemData();
                return true;
            case R.id.delete_item:
                deleteConfirmationDialog();
                return true;
            case R.id.buy_item:
                buyConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!itemWasTouched) {
                    NavUtils.navigateUpFromSameTask(this);
                    return true;
                }

                DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask(EditorActivity.this);
                    }
                };

                returnHomeDialog(dialogInterface);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void returnHomeDialog(DialogInterface.OnClickListener dialogInterface) {
        AlertDialog.Builder builder = new AlertDialog.Builder(EditorActivity.this);
        builder.setMessage("Discard changes?").setTitle("Go home");

        builder.setPositiveButton("Yes", dialogInterface);

        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {
        Log.d(TAG, "deleteItem: URI: " + uri);
        int rowDeleted = getContentResolver().delete(uri, null, null);

        if (rowDeleted != 0) {
            Toast.makeText(this, "Item deleted", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Error deleting", Toast.LENGTH_SHORT).show();
        }
        finish();
    }

    // When the Select Image button is clicked, create an intent that lets you choose pictures
    public void buttonClick(View view) {
        Log.d(TAG, "onClick: Button");
        Intent intent = new Intent(Intent.ACTION_PICK);
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

    public Bitmap decodeSampleBitmapFromResource(InputStream inputStream, Uri uri, int reqWidth, int reqHeight) throws FileNotFoundException {

//        First decode with InJustDecodeBounds = true to check the dimensions
        Log.d(TAG, "decodeSampleBitmapFromResource: INPUTSTREAM : " + inputStream);
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, options);

//        Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

//        decode bitmap with insamplesize set
        options.inJustDecodeBounds = false;

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        return BitmapFactory.decodeStream(getContentResolver().openInputStream(uri), null, o2);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {

        if (!itemWasTouched) {
            NavUtils.navigateUpFromSameTask(EditorActivity.this);
            finish();
        } else {

            DialogInterface.OnClickListener dialogInterface = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            };
            returnHomeDialog(dialogInterface);
        }
    }

    private void deleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage(R.string.delete_item).setTitle(R.string.confirm_delete);

        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteItem();
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

        AlertDialog alertDialog = builder.create();

        alertDialog.show();
    }

    private void buyConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("Buy this item?").setTitle("Buy Confirmation");
        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                saveItemData();
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

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    //    Change the onTouch to true if views were touched
    private View.OnTouchListener itemTouched = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            itemWasTouched = true;
            return false;
        }
    };

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {ItemContract.ItemEntry._ID, ItemContract.ItemEntry.COLUMN_NAME_NAME, ItemContract.ItemEntry.COLUMN_NAME_QUANTITY, ItemContract.ItemEntry.COLUMN_NAME_PRICE,
                ItemContract.ItemEntry.COLUMN_NAME_SUPPLIER, ItemContract.ItemEntry.COLUMN_NAME_PICTURE};

        return new CursorLoader(getApplicationContext(), uri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        if (cursor.moveToFirst()) {

            item_name.setText(cursor.getString(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_NAME_NAME)));
            item_price.setText(cursor.getString(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_NAME_PRICE)));
            item_quantity.setText(cursor.getString(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_NAME_QUANTITY)));
            item_supplier.setText(cursor.getString(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_NAME_SUPPLIER)));
            byte[] bytes = cursor.getBlob(cursor.getColumnIndexOrThrow(ItemContract.ItemEntry.COLUMN_NAME_PICTURE));
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
            item_image.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        item_name.getText().clear();
        item_price.getText().clear();
        item_quantity.getText().clear();
        item_supplier.getText().clear();
        item_image.setImageBitmap(null);
    }
}
