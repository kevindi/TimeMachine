package com.di.kevin.timemachine.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.di.kevin.timemachine.R;
import com.di.kevin.timemachine.data.TimeLogDataSource;

/**
 * Created by dike on 25/5/2015.
 */
public class TimeLogDialog extends DialogFragment {

    public static final String KEY_LOCATION_ID = "key_loc_id";
    private static final String TAG = TimeLogDialog.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        View view = getActivity().getLayoutInflater().inflate(R.layout.activity_dialog, null);
        TextView message = (TextView) view.findViewById(R.id.tv_msg);

        Bundle bundle = getArguments();
        long locationId = -1;
        if (bundle != null) {
            locationId  = bundle.getLong(KEY_LOCATION_ID);
        }

        String messageStr = "";

        TimeLogDataSource dataSource = new TimeLogDataSource(getActivity());
        dataSource.open();
        Log.d(TAG, "getTimeLogByLocationId loc_id: " + locationId);
        Cursor cursor = dataSource.getTimeLogByLocationId(locationId);

        if (cursor.moveToFirst()) {
            do {
                messageStr += cursor.getInt(cursor.getColumnIndex(TimeLogDataSource.COLUMN_TABLE_TIME_LOG_LOCATION_ID))
                        + " " + cursor.getString(cursor.getColumnIndex(TimeLogDataSource.COLUMN_TABLE_TIME_LOG_ENTER_TIME))
                        + " " + cursor.getString(cursor.getColumnIndex(TimeLogDataSource.COLUMN_TABLE_TIME_LOG_LEAVE_TIME))
                        + "\n";

                Log.d(TAG, messageStr);
            } while (cursor.moveToNext());
        }
        cursor.close();
        dataSource.close();

        message.setText(messageStr);
        builder.setView(view);

        return builder.create();
    }
}
