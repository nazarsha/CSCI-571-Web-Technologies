package com.example.hw9;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteActivity extends AppCompatActivity {

    public static final String TAG = "AutoCompleteActivity";
    private static final int AUTO_COMP_REQ_CODE = 1;

    //protected GeoDataClient geoDataClient;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_complete);
        Intent intent = getIntent();

       // Toolbar tb = findViewById(R.id.toolbar);
        //setSupportActionBar(tb);
        //tb.setSubtitle("Auto Complete");

        //set place types spinner data from array
        //Spinner placeType = findViewById(R.id.place_type);
        //ArrayAdapter<CharSequence> spinnerAdapter =
        //        ArrayAdapter.createFromResource(this,
        //                R.array.placeTypes, android.R.layout.simple_spinner_item);
        //spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        //placeType.setAdapter(spinnerAdapter);

        //Set adapter for autocomplete text view
        //AutoCompleteTextView searchPlace = findViewById(R.id.search_place);

        //CustomAutoCompleteAdapter adapter =  new CustomAutoCompleteAdapter(this);
        //searchPlace.setAdapter(adapter);
        //searchPlace.setOnItemClickListener(onItemClickListener);

        try {
            Intent autoCompIntent =
                    new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                            .build(this);
            startActivityForResult(autoCompIntent, 1);
        }  catch (GooglePlayServicesRepairableException e) {
            //Log.e(TAG, e.getStackTrace().toString());
            Log.e(TAG, "Didn't start successfully GooglePlayServicesRepairableException");
            // TODO: Handle the error.
        } catch (GooglePlayServicesNotAvailableException e) {
            // TODO: Handle the error.
            //Log.e(TAG, e.getStackTrace().toString());
            Log.e(TAG, "Didn't start successfully GooglePlayServicesNotAvailableException");
        }


    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.i(TAG, "Place: " + place.getName());
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                // TODO: Handle the error.
                Log.i(TAG, "onActivityResult: " + status.getStatusMessage());

            } else if (resultCode == RESULT_CANCELED) {
                //Log.e(TAG, e.getStackTrace().toString());
                Log.e(TAG, "Didn't start ");
                // The user canceled the operation.
            }
        }
    }

/*    private AdapterView.OnItemClickListener onItemClickListener =
            new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                    Toast.makeText(AutoCompleteActivity.this,
                            "selected place "
                            , Toast.LENGTH_SHORT).show();
                    //do something with the selection
                    searchScreen();
                }
            };

    public void searchScreen(){
        Intent i = new Intent();
        i.setClass(this, AutoCompleteActivity.class);
        startActivity(i);
    } */
}