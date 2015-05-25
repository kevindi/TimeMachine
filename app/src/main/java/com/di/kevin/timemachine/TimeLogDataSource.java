package com.di.kevin.timemachine;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import com.di.kevin.timemachine.object.TimeLog;
import com.di.kevin.timemachine.util.DateTimeUtil;

/**
 * Created by dike on 25/5/2015.
 */
public class TimeLogDataSource {
    private final DBHelper dbHelper;
    private SQLiteDatabase database;

    public static final String TABLE_TIME_LOG = "TIME_LOG";

    public static final String COLUMN_TABLE_TIME_LOG_TIME_LOG_ID = "TIME_LOG_ID";
    public static final String COLUMN_TABLE_TIME_LOG_LOCATION_ID = "LOCATION_ID";
    public static final String COLUMN_TABLE_TIME_LOG_LOG_TIME = "LOG_TIME";

    public static final String CREATE_TABLE_TIME_LOG = "create table "
            + TABLE_TIME_LOG + "("
            + COLUMN_TABLE_TIME_LOG_TIME_LOG_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, "
            + COLUMN_TABLE_TIME_LOG_LOCATION_ID + " integer not null, "
            + COLUMN_TABLE_TIME_LOG_LOG_TIME + " text not null "
            + ");";

    public TimeLogDataSource(Context context) {
        dbHelper = new DBHelper(context);
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public long insertTimeLog(TimeLog timeLog) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TABLE_TIME_LOG_LOCATION_ID, timeLog.getMyLocationId());

        String dateTime = DateTimeUtil.dateToString(timeLog.getLogTime(), DateTimeUtil.DATE_TIME_FORMAT);
        values.put(COLUMN_TABLE_TIME_LOG_LOG_TIME, dateTime);

        return database.insert(TABLE_TIME_LOG, null, values);
    }

    public Cursor getTimeLogByLocationId(long locationId) {
        return database.query(TABLE_TIME_LOG, new String[] {COLUMN_TABLE_TIME_LOG_TIME_LOG_ID,
                COLUMN_TABLE_TIME_LOG_LOCATION_ID, COLUMN_TABLE_TIME_LOG_LOG_TIME}, COLUMN_TABLE_TIME_LOG_LOCATION_ID + "=" + locationId, null, null, null, null);
    }

    public Cursor getAllTimeLogs() {
        return database.query(TABLE_TIME_LOG, new String[] {COLUMN_TABLE_TIME_LOG_TIME_LOG_ID,
                COLUMN_TABLE_TIME_LOG_LOCATION_ID, COLUMN_TABLE_TIME_LOG_LOG_TIME}, null, null, null, null, null);
    }

}
