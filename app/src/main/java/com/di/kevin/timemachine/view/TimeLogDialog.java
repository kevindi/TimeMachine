package com.di.kevin.timemachine.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.di.kevin.timemachine.R;
import com.di.kevin.timemachine.TimeLogDataSource;
import com.di.kevin.timemachine.util.DateTimeUtil;

import org.w3c.dom.Text;

import java.util.Date;

/**
 * Created by dike on 25/5/2015.
 */
public class TimeLogDialog extends DialogFragment {

    public static final String KEY_LOCATION_ID = "key_loc_id";

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
        Cursor cursor = dataSource.getTimeLogByLocationId(locationId);

        cursor.moveToFirst();
        while (cursor.moveToNext()) {
            messageStr += cursor.getString(cursor.getColumnIndex(TimeLogDataSource.COLUMN_TABLE_TIME_LOG_LOG_TIME)) + "\n";
        }

        dataSource.close();

        message.setText(messageStr);
        builder.setView(view);

        return builder.create();
    }
}
