package com.di.kevin.timemachine.view;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.di.kevin.timemachine.LocationChangedListener;
import com.di.kevin.timemachine.LocationDataSource;
import com.di.kevin.timemachine.R;
import com.di.kevin.timemachine.object.MyLocation;

/**
 * Created by dike on 20/5/2015.
 */
public class CreateLocationDialog extends DialogFragment implements LocationChangedListener{

    public final static String KEY_LAT = "lat";
    public final static String KEY_LNG = "lng";
    public final static String KEY_FIX_LAT_LNG = "fix_lat_lng";
    private android.location.Location myLocation;

    private LocationCreateConfirmListener mLocationCreatedListener;

    private final static String TAG = CreateLocationDialog.class.getSimpleName();
    private EditText etLat;
    private EditText etLng;
    private EditText etLocationName;
    private boolean overWritePositiveButton = false;

    public void setCreateLocationListener(LocationCreateConfirmListener listener) {
        this.mLocationCreatedListener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();

        final AlertDialog  dialog = (AlertDialog)getDialog();
        if (dialog != null) {

            overWritePositiveButton = true;

            Button positiveButton = dialog.getButton(Dialog.BUTTON_POSITIVE);
            positiveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    saveCreatedLocation(dialog);
                }
            });
        }
    }

    private void saveCreatedLocation(DialogInterface dialog) {
        if (!isInputsCorrect()) {
            Toast.makeText(getActivity(), getString(R.string.check_input), Toast.LENGTH_LONG).show();
        } else {
            LocationDataSource dataSource = new LocationDataSource(getActivity());
            dataSource.open();

            MyLocation newLocation = new MyLocation();
            newLocation.setLocationName(etLocationName.getText().toString());
            newLocation.setLocationLat(Double.parseDouble(etLat.getText().toString()));
            newLocation.setLocationLng(Double.parseDouble(etLng.getText().toString()));

            long result = dataSource.createMyLocation(newLocation);
            dataSource.close();

            if (result > 0) {
                if (mLocationCreatedListener != null) {
                    //TODO inform listener there is a location created
                    newLocation.setLocationId(result);
                    mLocationCreatedListener.onCreateConfirm(newLocation);
                }
                Toast.makeText(getActivity(), getString(R.string.location_created), Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            } else {
                Toast.makeText(getActivity(), getString(R.string.database_error), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        double currentLat = 0d;
        double currentLng = 0d;
        boolean fixedLatLng = false;

        Bundle bundle = getArguments();
        if (bundle != null) {
            currentLat = bundle.getDouble(KEY_LAT);
            currentLng = bundle.getDouble(KEY_LNG);
            fixedLatLng = bundle.getBoolean(KEY_FIX_LAT_LNG);
        }

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.create_new_location));
        View view = getActivity().getLayoutInflater().inflate(R.layout.create_new_location_dialog_layout, null);

        etLocationName = (EditText)view.findViewById(R.id.et_location_name);

        etLat = (EditText) view.findViewById(R.id.et_lat);
        etLat.setText(String.valueOf(currentLat));

        etLng = (EditText) view.findViewById(R.id.et_lng);
        etLng.setText(String.valueOf(currentLng));
        Button getCurLoc = (Button) view.findViewById(R.id.btn_get_current_loc);

        if (fixedLatLng) {
            etLat.setEnabled(false);
            etLng.setEnabled(false);
            getCurLoc.setVisibility(View.GONE);
        }

        getCurLoc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (myLocation != null) {
                    double currentLat = myLocation.getLatitude();
                    double currentLng = myLocation.getLongitude();

                    etLat.setText(String.valueOf(currentLat));
                    etLng.setText(String.valueOf(currentLng));
                }
            }
        });

        builder.setView(view);
        builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if (!overWritePositiveButton) {
                    saveCreatedLocation(dialog);
                }
            }
        });

        builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        return builder.create();
    }

    public boolean isInputsCorrect() {
        if (etLocationName.getText().toString().matches("") || etLat.getText().toString().matches("")
                || etLng.getText().toString().matches(""))
            return false;
        else
            return true;
    }

    @Override
    public void onLocationChanged(android.location.Location newLocation) {
        Log.d(TAG, "onLocationChanged lat: " + newLocation.getLatitude() + " lng: " + newLocation.getLongitude());
        this.myLocation = newLocation;
    }

    public interface LocationCreateConfirmListener {
        void onCreateConfirm(MyLocation location);
    }

}
