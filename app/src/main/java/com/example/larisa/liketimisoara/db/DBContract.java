package com.example.larisa.liketimisoara.db;

import android.provider.BaseColumns;

/**
 * Contract class for the application's local database.
 */

class DBContract {

    // If you change the database schema, you must increment the database version.
    static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "LikeTimisoara.db";

    /* Data types used */
    private static final String VARCHAR = "VARCHAR";
    private static final String INTEGER = "INTEGER";
    private static final String REAL = "REAL";

    private DBContract() {
    }

    abstract static class AttractionsTable implements BaseColumns {

        public static final String TABLE_NAME = "attractions";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_INFO = "info";
        public static final String COLUMN_IMAGE_RESOURCE_ID = "imageResourceId";
        public static final String COLUMN_IS_TOP_10 = "isTop10";
        public static final String COLUMN_LONGITUDE = "longitude";
        public static final String COLUMN_LATITUDE = "latitude";

        public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
                + " ("
                + _ID + " INTEGER PRIMARY KEY"
                + ", " + AttractionsTable.COLUMN_TYPE + " " + INTEGER
                + ", " + AttractionsTable.COLUMN_NAME + " " + VARCHAR
                + ", " + AttractionsTable.COLUMN_INFO + " " + VARCHAR
                + ", " + AttractionsTable.COLUMN_IMAGE_RESOURCE_ID + " " + INTEGER
                + ", " + AttractionsTable.COLUMN_IS_TOP_10 + " " + INTEGER
                + ", " + AttractionsTable.COLUMN_LONGITUDE + " " + REAL
                + ", " + AttractionsTable.COLUMN_LATITUDE + " " + REAL
                + ")";

        public static final String DELETE_TABLE = "DROP TABLE IF EXISTS " + TABLE_NAME;

        private AttractionsTable() {
        }
    }
}
