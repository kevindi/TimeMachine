package com.di.kevin.timemachine.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.di.kevin.timemachine.R;
import com.di.kevin.timemachine.listener.IconChosenListener;

/**
 * Created by dike on 27/5/2015.
 */
public class ChooseIconDialog extends DialogFragment {

    private int[] icons = {R.drawable.icon_bank, R.drawable.icon_home, R.drawable.icon_airport,
            R.drawable.icon_resturant, R.drawable.icon_hospital, R.drawable.icon_sport,
            R.drawable.icon_police, R.drawable.icon_post_office, R.drawable.icon_barber,
            R.drawable.icon_cat, R.drawable.icon_dog, R.drawable.icon_fish, R.drawable.icon_prawn,
            R.drawable.icon_christmas_tree, R.drawable.icon_ice_skate, R.drawable.icon_advanture,
            R.drawable.icon_ufo, R.drawable.icon_skull, R.drawable.icon_heart, R.drawable.icon_football,
            R.drawable.icon_baby, R.drawable.icon_gas_station, R.drawable.icon_court_house,
            R.drawable.icon_baseball_cap, R.drawable.icon_bra, R.drawable.icon_coat,
            R.drawable.icon_men_shoe, R.drawable.icon_women_shoe, R.drawable.icon_beer,
            R.drawable.icon_donut, R.drawable.icon_ingredient, R.drawable.icon_waiter,
            R.drawable.icon_chip, R.drawable.icon_microphone, R.drawable.icon_eiffel_tower,
            R.drawable.icon_beach, R.drawable.icon_big_ben, R.drawable.icon_statue_of_libery,
            R.drawable.icon_sun, R.drawable.icon_barbell, R.drawable.icon_swimming,
            R.drawable.icon_skiing, R.drawable.icon_controller, R.drawable.icon_train,
            R.drawable.icon_taxi, R.drawable.icon_bus, R.drawable.icon_handshake, R.drawable.icon_grenade,
            R.drawable.icon_gun, R.drawable.icon_mortar, R.drawable.icon_magazine, R.drawable.icon_radio,
            R.drawable.icon_rpg, R.drawable.icon_medal
    };
    private IconChosenListener listener;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        View view = getActivity().getLayoutInflater().inflate(R.layout.choose_icon_dialog, null);
        GridView iconGrid = (GridView) view.findViewById(R.id.gv_icon_grid);
        iconGrid.setAdapter(gridAdapter);

        iconGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if (listener != null) {
                    listener.OnIconChosen(icons[position]);
                }
                ChooseIconDialog.this.getDialog().dismiss();
            }
        });

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        builder.setTitle(getString(R.string.choose_icon_for_location));
        builder.setView(view);

        return builder.create();
    }

    public void setIconChosenListener(IconChosenListener listener) {
        this.listener = listener;
    }

    private BaseAdapter gridAdapter = new BaseAdapter() {
        @Override
        public int getCount() {
            return icons.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView imageView = new ImageView(getActivity());
            imageView.setImageResource(icons[position]);
            imageView.setPadding(0, 10, 0, 10);
            return imageView;
        }
    };

//    private ListAdapter gridAdapter = new ListAdapter() {
//        @Override
//        public boolean areAllItemsEnabled() {
//            return false;
//        }
//
//        @Override
//        public boolean isEnabled(int position) {
//            return false;
//        }
//
//        @Override
//        public void registerDataSetObserver(DataSetObserver observer) {
//
//        }
//
//        @Override
//        public void unregisterDataSetObserver(DataSetObserver observer) {
//
//        }
//
//        @Override
//        public int getCount() {
//            return icons.length;
//        }
//
//        @Override
//        public Object getItem(int position) {
//            return null;
//        }
//
//        @Override
//        public long getItemId(int position) {
//            return 0;
//        }
//
//        @Override
//        public boolean hasStableIds() {
//            return false;
//        }
//
//        @Override
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            ImageView imageView = new ImageView(getActivity());
//            imageView.setImageResource(icons[position]);
//
//            return imageView;
//        }
//
//        @Override
//        public int getItemViewType(int position) {
//            return position == currentSelected ? TYPE_SELECTED: TYPE_NONSELECTED;
//        }
//
//        @Override
//        public int getViewTypeCount() {
//            return 2;
//        }
//
//        @Override
//        public boolean isEmpty() {
//            return false;
//        }
//    };

}
