package com.example.android.pets.data;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.pets.R;

import static android.content.ContentValues.TAG;

public class PetCursorAdapter extends CursorAdapter {

    /**
     * @param context the context
     * @param c       the cursor from which to get the data
     */
    public PetCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * @param context app context
     * @param cursor  cursor to get data. It's been moved to the correct position.
     * @param parent  the parent to which the new view is attached to
     * @return
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * @param view    existing view, that was returned by newView
     * @param context app context
     * @param cursor  cursor to get data. Moved to correct position.
     */
    @Override
    public void bindView(View view, Context context, Cursor cursor) {

//        Find views from the list item
        TextView textName = (TextView) view.findViewById(R.id.name);
        TextView textSummary = (TextView) view.findViewById(R.id.summary);

        Log.d(TAG, "bindView: cursor changed?");

//        Find the columns we want from the database
        String name = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_PET_NAME));
        String breed = cursor.getString(cursor.getColumnIndexOrThrow(PetContract.PetEntry.COLUMN_PET_BREED));

//        Update the views with the items above
        textName.setText(name);
        textSummary.setText(breed);

    }
}
