package com.di.kevin.timemachine.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.di.kevin.timemachine.data.LocationDataSource;
import com.di.kevin.timemachine.data.TimeLogDataSource;

/**
 * Created by dike on 20/5/2015.
 */

public class DBHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "location.db";
    private static final int DATABASE_VERSION = 1;

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL(LocationDataSource.CRATE_TABLE_LOCATION);
        database.execSQL(TimeLogDataSource.CREATE_TABLE_TIME_LOG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}