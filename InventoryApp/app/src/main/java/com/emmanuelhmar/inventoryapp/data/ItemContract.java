package com.emmanuelhmar.inventoryapp.data;

import android.provider.BaseColumns;

public class ItemContract {

    public static class ItemEntry implements BaseColumns {
        public static final String TABLE_NAME = "entry";
        public static final String TABLE_ID = BaseColumns._ID;
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLUMN_NAME_PRICE = "price";
        public static final String COLUMN_NAME_QUANTITY = "quantity";
        public static final String COLUMN_NAME_SUPPLIER = "supplier";
        public static final String COLUMN_NAME_PICTURE = "picture";



    }


}
