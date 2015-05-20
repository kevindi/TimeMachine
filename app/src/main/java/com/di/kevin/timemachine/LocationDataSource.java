package com.di.kevin.timemachine;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.di.kevin.timemachine.model.Location;

/**
 * Created by dike on 20/5/2015.
 */
public class LocationDataSource {

    private final DBHelper dbHelper;
    private SQLiteDatabase database;

    public static final String TABLE_LOCATION = "LOCATION_TABLE";

    private static final String COLUMN_TABLE_LOCATION_LOCATION_ID = "LOCATION_ID";
    private static final String COLUMN_TABLE_LOCATION_LOCATION_NAME = "LOCATION_NAME";

    public static final String CRATE_TABLE_LOCATION = "create table "
            + TABLE_LOCATION + "("
            + COLUMN_TABLE_LOCATION_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL "
            + COLUMN_TABLE_LOCATION_LOCATION_NAME + " text not null "
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

    public boolean createLocation(Location location) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TABLE_LOCATION_LOCATION_NAME, location.getLocationName());
        long insertId = database.insert(TABLE_LOCATION, null, values);
        return insertId > 0;
    }
}
