package com.example.android.pets.data;

import android.provider.BaseColumns;

public final class PetContract {

    public static class PetEntry implements BaseColumns{
        public static final String TABLE_NAME = "pets";
        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_NAME_NAME = "name";
        public static final String COLOMN_NAME_BREED = "breed";
        public static final String COLUMN_NAME_GENDER = "gender";
        public static final String COLUMN_NAME_WEIGHT = "weight";

        /*
        * Possible values for the different gender values
        * */

        public static final int GENDER_UNKNOWN = 0;
        public static final int GENDER_MALE = 1;
        public static final int GENDER_FEMALE = 2;
    }

}
