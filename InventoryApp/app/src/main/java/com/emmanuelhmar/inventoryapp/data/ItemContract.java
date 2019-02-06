package com.emmanuelhmar.inventoryapp.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public class ItemContract {

    public static final String CONTENT_AUTHORITY = "com.emmanuelhmar.inventoryapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_ITEMS = "entry";

    public static final String SOLD_ITEMS = "sold";

    public static class ItemEntry implements BaseColumns {

        //        The content URI to access the item data
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_ITEMS);

        public static final Uri SOLD_ITEMS_CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, SOLD_ITEMS);


//        For getType in Content Provider
        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" +
                CONTENT_AUTHORITY + "/" + PATH_ITEMS;

        public static final String TABLE_NAME = "entry";
        public static final String TABLE_ID = BaseColumns._ID;
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_SUPPLIER = "supplier";
        public static final String COLUMN_NAME_TOTAL = "total";
        public static final String COLUMN_NAME_PICTURE = "picture";

        public static final String SOLD_TABLE_NAME = "sold";


    }


}
