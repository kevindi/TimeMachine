package com.di.kevin.timemachine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.di.kevin.timemachine.object.MyLocation;

import java.util.HashMap;

/**
 * Created by dike on 20/5/2015.
 */
public class LocationDataSource {

    private final DBHelper dbHelper;
    private SQLiteDatabase database;

    public static final String TABLE_LOCATION = "LOCATION_TABLE";

    public static final String COLUMN_TABLE_LOCATION_LOCATION_ID = "LOCATION_ID";
    public static final String COLUMN_TABLE_LOCATION_LOCATION_NAME = "LOCATION_NAME";
    public static final String COLUMN_TABLE_LOCATION_LOCATION_LAT = "LOCATION_LAT";
    public static final String COLUMN_TABLE_LOCATION_LOCATION_LNG = "LOCATION_LNG";

    public static final String CRATE_TABLE_LOCATION = "create table "
            + TABLE_LOCATION + "("
            + COLUMN_TABLE_LOCATION_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + COLUMN_TABLE_LOCATION_LOCATION_NAME + " text not null, "
            + COLUMN_TABLE_LOCATION_LOCATION_LAT + " real not null, "
            + COLUMN_TABLE_LOCATION_LOCATION_LNG + " real not null "
            + ");";

    public LocationDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long createMyLocation(MyLocation location) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TABLE_LOCATION_LOCATION_NAME, location.getLocationName());
        values.put(COLUMN_TABLE_LOCATION_LOCATION_LAT, location.getLocationLat());
        values.put(COLUMN_TABLE_LOCATION_LOCATION_LNG, location.getLocationLng());

        return database.insert(TABLE_LOCATION, null, values);
    }

    public Cursor getAllMyLocations() {
        return database.query(TABLE_LOCATION, new String[] {COLUMN_TABLE_LOCATION_LOCATION_ID,
                COLUMN_TABLE_LOCATION_LOCATION_NAME, COLUMN_TABLE_LOCATION_LOCATION_LAT,
                COLUMN_TABLE_LOCATION_LOCATION_LNG }, null, null, null, null, null);
    }
}
