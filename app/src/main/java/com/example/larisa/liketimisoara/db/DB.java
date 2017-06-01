package com.example.larisa.liketimisoara.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.larisa.liketimisoara.Attraction;
import com.example.larisa.liketimisoara.AttractionType;

import java.util.ArrayList;
import java.util.List;

/**
 * Database implementation.
 */

public class DB extends SQLiteOpenHelper {

    public static final String TAG = DB.class.getName();

    private static DB instance;

    private DB(Context context) {
        super(context, DBContract.DATABASE_NAME, null, DBContract.DATABASE_VERSION);
    }

    public static DB getInstance(Context context) {

        if (instance == null) {
            instance = new DB(context);
        }

        return instance;
    }

    /**
     * Called when the database is created for the first time.
     * This is where the creation of tables and the initial population
     * of the tables should happen.
     *
     * @param db the database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.AttractionsTable.CREATE_TABLE);
    }

    /**
     * Called when the database needs to be upgraded.
     * The implementation should use this method to drop tables,
     * add tables, or do anything else it needs to upgrade to the new schema version.
     *
     * @param db         the database
     * @param oldVersion the old database version
     * @param newVersion the new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(DBContract.AttractionsTable.DELETE_TABLE);
        onCreate(db);
    }

    @Override
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // delete old DB
        onUpgrade(db, oldVersion, newVersion);
    }

    public long insertAttraction(Attraction attraction) throws DBException {

        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBContract.AttractionsTable.COLUMN_TYPE, attraction.getType().ordinal());
        contentValues.put(DBContract.AttractionsTable.COLUMN_NAME, attraction.getName());
        contentValues.put(DBContract.AttractionsTable.COLUMN_INFO, attraction.getInfo());
        contentValues.put(DBContract.AttractionsTable.COLUMN_IMAGE_RESOURCE_ID, attraction.getImageResourceId());
        contentValues.put(DBContract.AttractionsTable.COLUMN_IS_TOP_10, String.valueOf(attraction.isTop10()));
        contentValues.put(DBContract.AttractionsTable.COLUMN_LONGITUDE, attraction.getLongitude());
        contentValues.put(DBContract.AttractionsTable.COLUMN_LATITUDE, attraction.getLatitude());

        long rowId = db.insert(DBContract.AttractionsTable.TABLE_NAME, null, contentValues);

        if (rowId == -1) {
            Log.w(TAG, "Error inserting attraction in DB");
            throw new DBException("cannot insert row");
        }

        return rowId;
    }

    public List<Attraction> getAttractions(AttractionType type) {

        List<Attraction> results = new ArrayList<>();

        SQLiteDatabase db = this.getReadableDatabase();

        // Define a projection that specifies which columns from the database you will actually use after this query.
        String[] projection = {
                DBContract.AttractionsTable._ID,
                DBContract.AttractionsTable.COLUMN_TYPE,
                DBContract.AttractionsTable.COLUMN_NAME,
                DBContract.AttractionsTable.COLUMN_INFO,
                DBContract.AttractionsTable.COLUMN_IMAGE_RESOURCE_ID,
                DBContract.AttractionsTable.COLUMN_IS_TOP_10,
                DBContract.AttractionsTable.COLUMN_LONGITUDE,
                DBContract.AttractionsTable.COLUMN_LATITUDE,
        };

        // The columns for the WHERE clause
        String selection = "";
        String whereOperator = "";
        if (type != null) {
            selection += whereOperator + " " + DBContract.AttractionsTable.COLUMN_TYPE + " = " + type.ordinal();
        }

        Cursor c = null;
        try {
            c = db.query(
                    DBContract.AttractionsTable.TABLE_NAME,     // The table to query
                    projection,                                 // The columns to return
                    selection,                                  // The columns for the WHERE clause
                    null,                                       // The values for the WHERE clause
                    null,                                       // don't group the rows
                    null,                                       // don't filter by row groups
                    null,                                       // The sort order
                    null
            );

            c.moveToFirst();

            while (!c.isAfterLast()) {
                long rowId = c.getLong(c.getColumnIndex(DBContract.AttractionsTable._ID));

                Attraction attraction = new Attraction();

                attraction.setType(AttractionType.values()[c.getInt(c.getColumnIndex(DBContract.AttractionsTable.COLUMN_TYPE))]);
                attraction.setName(c.getString(c.getColumnIndex(DBContract.AttractionsTable.COLUMN_NAME)));
                attraction.setInfo(c.getString(c.getColumnIndex(DBContract.AttractionsTable.COLUMN_INFO)));
                attraction.setImageResourceId(c.getInt(c.getColumnIndex(DBContract.AttractionsTable.COLUMN_IMAGE_RESOURCE_ID)));
                attraction.setTop10(Boolean.valueOf(c.getString(c.getColumnIndex(DBContract.AttractionsTable.COLUMN_IS_TOP_10))));
                attraction.setLongitude(c.getDouble(c.getColumnIndex(DBContract.AttractionsTable.COLUMN_LONGITUDE)));
                attraction.setLatitude(c.getDouble(c.getColumnIndex(DBContract.AttractionsTable.COLUMN_LATITUDE)));

                results.add(attraction);

                c.moveToNext();

            }

        } finally {
            if (c != null) {
                c.close();
            }
        }

        return results;
    }
//    public void updateEventLog(EventLogDbRow eventLog) throws DBException {
//
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        // New value for one column
//        ContentValues values = new ContentValues();
//        values.put(DBContract.EventLogTable.COLUMN_TIMESTAMP, eventLog.getEventLog().getTimestamp().getTime());
//        values.put(DBContract.EventLogTable.COLUMN_STATUS_CODE, eventLog.getEventLog().getStatusCode());
//        values.put(DBContract.EventLogTable.COLUMN_SOURCE, eventLog.getEventLog().getSource());
//        values.put(DBContract.EventLogTable.COLUMN_DATA, eventLog.getEventLog().getData());
//        values.put(DBContract.EventLogTable.COLUMN_USER_ID, eventLog.getEventLog().getUserId());
//        values.put(DBContract.EventLogTable.COLUMN_MESSAGE, eventLog.getEventLog().getMessage());
//        values.put(DBContract.EventLogTable.COLUMN_SENT, eventLog.isSentToServer());
//        values.put(DBContract.EventLogTable.COLUMN_READ, eventLog.isRead());
//        values.put(DBContract.EventLogTable.COLUMN_LEVEL, eventLog.getEventLog().getLevel().ordinal());
//        values.put(DBContract.EventLogTable.COLUMN_DEVICE_ID, eventLog.getEventLog().getDeviceId());
//
//        // Which row to update, based on the ID
//        String selection = DBContract.EventLogTable._ID + " = ?";
//        String[] selectionArgs = {Long.toString(eventLog.getRowId())};
//
//        int rowsAffected = db.update(
//                DBContract.EventLogTable.TABLE_NAME,
//                values,
//                selection,
//                selectionArgs
//        );
//
//        if (rowsAffected != 1) {
//            throw new DBException("no row found for update");
//        }
//
//        Log.d(TAG, "Updated event log, rows affected: " + rowsAffected);
//    }
//
//    /**
//     * Deletes an event log row
//     *
//     * @param eventLog the event log to be deleted
//     * @throws DBException
//     */
//    @Override
//    public void deleteEventLog(EventLogDbRow eventLog) throws DBException {
//
//        SQLiteDatabase db = this.getWritableDatabase();
//
//        // Which row to update, based on the ID
//        String whereClause = DBContract.EventLogTable._ID + " = ?";
//        String[] whereArgs = {Long.toString(eventLog.getRowId())};
//
//        int rowsAffected = db.delete(
//                DBContract.EventLogTable.TABLE_NAME,
//                whereClause,
//                whereArgs
//        );
//
//        if (rowsAffected != 1) {
//            throw new DBException("no row found for update");
//        }
//
//        Log.d(TAG, "deleted event log, rows affected: " + rowsAffected);
//    }
}
