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
 * Created by dike on 26/5/2015.
 */
public class SuperTableDialog extends DialogFragment {

    private static final String TAG = SuperTableDialog.class.getSimpleName();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.activity_dialog, null);
        String messageStr = "";

        TimeLogDataSource dataSource = new TimeLogDataSource(getActivity());
        dataSource.open();

        Cursor cursor = dataSource.getAllTimeLogs();
        if (cursor.moveToFirst()) {
             do {
                int locationId = cursor.getInt(cursor.getColumnIndex(TimeLogDataSource.COLUMN_TABLE_TIME_LOG_LOCATION_ID));
                String enterTime = cursor.getString(cursor.getColumnIndex(TimeLogDataSource.COLUMN_TABLE_TIME_LOG_ENTER_TIME));
                String leaveTime = cursor.getString(cursor.getColumnIndex(TimeLogDataSource.COLUMN_TABLE_TIME_LOG_LEAVE_TIME));

                messageStr += "loc_id: " + locationId + " enter_time: " + enterTime + " leave_time: " + leaveTime + "\n";
                Log.d(TAG, messageStr);
            } while (cursor.moveToNext());
        }


        cursor.close();
        dataSource.close();

        TextView tvMessage = (TextView) view.findViewById(R.id.tv_msg);
        tvMessage.setText(messageStr);

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Time Log Table");
        builder.setView(view);

        return builder.create();
    }
}
