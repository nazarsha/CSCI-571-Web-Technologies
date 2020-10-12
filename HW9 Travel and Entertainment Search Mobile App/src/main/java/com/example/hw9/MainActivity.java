package com.example.hw9;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Criteria;
import android.location.LocationManager;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.location.places.Place;

import android.util.Log;

import android.Manifest;
import android.widget.Toast;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "AutoCompleteActivity";
    public static final int MY_PERMISSIONS_REQUEST_LOCATION = 99;
    LocationManager locationManager;
    private com.google.android.gms.location.FusedLocationProviderClient mFusedLocationClient;
    String provider;
    public String lat_lng = "";
    public String inputLocationValue = "";



    public boolean checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            // Should we show an explanation?
/*            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {

                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new AlertDialog.Builder(this)
                        .setMessage(R.string.title_location_permission)
                        .setPositiveButton("Allow", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //Prompt the user once explanation has been shown
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        MY_PERMISSIONS_REQUEST_LOCATION);
                            }
                        })
                        .create()
                        .show();


            } else { */
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        MY_PERMISSIONS_REQUEST_LOCATION);

            return false;
        } else {

            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                mFusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, new com.google.android.gms.tasks.OnSuccessListener<android.location.Location>() {
                        @Override
                        public void onSuccess(android.location.Location location) {
                            // Got last known location. In some rare situations this can be null.
                            if (location != null) {
                                // Logic to handle location object
                                lat_lng = location.getLatitude() + "," + location.getLongitude();
                            }
                            else {
                                lat_lng = "Failed to get location";
                            }
                        }
                    });
            return true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // permission was granted, yay! Do the
                    // location-related task you need to do.
                    if (ContextCompat.checkSelfPermission(this,
                            Manifest.permission.ACCESS_FINE_LOCATION)
                            == PackageManager.PERMISSION_GRANTED) {

                        //Request location updates:
                       //locationManager.requestLocationUpdates(provider,400,1, this);//(provider, 400, 1, this);
                        //provider =  locationManager.getBestProvider(new Criteria(), false);
                        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                        mFusedLocationClient.getLastLocation()
                            .addOnSuccessListener(this, new com.google.android.gms.tasks.OnSuccessListener<android.location.Location>() {
                                @Override
                                public void onSuccess(android.location.Location location) {
                                    // Got last known location. In some rare situations this can be null.
                                    if (location != null) {
                                        // Logic to handle location object
                                        lat_lng = location.getLatitude() + "," + location.getLongitude();
                                    }
                                    else {
                                        lat_lng = "Failed to get location";
                                    }
                                }
                            });
                    }

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    lat_lng = "Failed to get location";

                }
                return;
            }
        }};




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //asking for permission to get location
        checkLocationPermission();

        FavsList.getInstance();


        final String Categories [] = {"Default","Airport","Amusement Park","Aquarium","Art Gallery","Bakery","Bar","Beauty salon","Bowling alley","Bus station","Cafe","Campground","Car rental","Casino","Lodging","Movie theater","Museum","Night club","Park","Parking","Restaurant","Shopping mall","Stadium","Subway station","Taxi stand","Train station","Transit station","Travel agency","Zoo"};

        final Spinner category = (Spinner) findViewById(R.id.category);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, R.array.planets_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        category.setAdapter(adapter);

        final RadioButton location_here = (RadioButton) this.findViewById(R.id.radioButton_here);
        final RadioButton location_from = (RadioButton) this.findViewById(R.id.radioButton_from);
        final AutoCompleteTextView input_location = (AutoCompleteTextView) this.findViewById(R.id.input_location);
        input_location.setEnabled(false);
        final TextView errorMessage = (TextView) this.findViewById(R.id.errorMessage);
        errorMessage.setText("");

        /*
        input_location.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus){
                    Intent myAutoCompInt = new Intent(getApplicationContext(), AutoCompleteActivity.class);
                    //resultIntent.putExtra("url", url);
                    //loadProgressDialog();
                    startActivity(myAutoCompInt);

                }

            }
        }); */


        final TextView error_loc = (TextView) this.findViewById(R.id.error_loc);
        error_loc.setText("");

        final TextView error_keyword = (TextView) this.findViewById(R.id.error_keyword);
        error_keyword.setText("");

        final String badInputErrorMessage = "Please fix all fields with errors";

        final EditText keyword = (EditText) findViewById(R.id.keyword_box);
        final TextView keyword_text = (TextView) this.findViewById(R.id.keyword_text);

        final EditText distance = (EditText) findViewById(R.id.distance);




        final Button search_tab = (Button) this.findViewById(R.id.search_tab);
        final Button fav_tab = (Button) this.findViewById(R.id.fav_tab);

        SpannableStringBuilder sb;
        Drawable drawable;
        ImageSpan span;
        sb = new SpannableStringBuilder("  SEARCH"); // space added before text for convenience

        drawable = this.getResources().getDrawable( R.drawable.search );
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        search_tab.setText(sb);


        sb = new SpannableStringBuilder("  FAVORITES"); // space added before text for convenience

        drawable = this.getResources().getDrawable( R.drawable.heart_fill_white );
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
        sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        fav_tab.setText(sb);


        final Button search = (Button) this.findViewById(R.id.Search_button);
        final Button clear = (Button) this.findViewById(R.id.Clear_button);




        location_from.setOnCheckedChangeListener( new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){ // If the here button is on
                    // Enable the TextView
                    input_location.setEnabled(true);
                    //input_location.setBackgroundColor(Color.parseColor("#9e9e9e"));
                } else { // If the location button is off
                    // Disable the TextView
                    input_location.setEnabled(false);
                    error_loc.setText("");
                }
            } });



        CustomAutoCompleteAdapter inputLocAdapter =  new CustomAutoCompleteAdapter(this);
        input_location.setAdapter(inputLocAdapter);
        input_location.setOnItemClickListener( new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                /*Toast.makeText(getApplicationContext(),
                        "selected place "
                                + ((com.example.hw9.Place)adapterView.
                                getItemAtPosition(i)).getPlaceText()
                        , Toast.LENGTH_LONG).show(); */
                //do something with the selection
                String txt = ((com.example.hw9.Place)adapterView.
                        getItemAtPosition(i)).getPlaceText();

                inputLocationValue = txt;


            }
        } );



        keyword.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String str1 = keyword.getText().toString().trim();

                    if (str1.length() != 0 ){
                        error_keyword.setText(""); }
                    //} else {
                        //error_keyword.setText("");
                    //}

                }
            }
        });




        fav_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent resultIntent = new Intent(getApplicationContext(), FavActivity.class);
                String mess = "Youre now in fav tab";
                resultIntent.putExtra("message", mess);
                startActivity(resultIntent);
            }
        });


        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                // validate the inputs and take some action
                String in_keyword, in_distance, in_input_loc, in_loc, in_cat;

                Integer cat_index = category.getSelectedItemPosition();
                in_cat = Categories[cat_index];


                in_keyword = keyword.getText().toString().trim();
                in_distance = distance.getText().toString().trim();
                if (in_distance == "") in_distance = "10";
                in_input_loc = input_location.getText().toString().trim();
                in_loc =  location_here.isChecked() ? "here" : "loc";
                if (in_loc == "loc" && in_input_loc.length() == 0){
                    error_loc.setText("Please enter mandatory field");
                    //errorMessage.setText(badInputErrorMessage);
                    //errorMessage.setBackgroundColor(Color.parseColor("#EFEFEF"));

                    Toast.makeText(getApplicationContext(),
                            badInputErrorMessage, Toast.LENGTH_LONG).show();


                    return;
                }
                else if (in_keyword.length() == 0){
                    error_keyword.setText("Please enter mandatory field");
                    //errorMessage.setText(badInputErrorMessage);
                    //errorMessage.setBackgroundColor(Color.parseColor("#EFEFEF"));
                    Toast.makeText(getApplicationContext(),
                            badInputErrorMessage, Toast.LENGTH_LONG).show();



                    return;
                }
                else
                {
                    errorMessage.setText("");
                    errorMessage.setBackgroundColor(Color.WHITE);
                    error_loc.setText("");
                    error_keyword.setText("");
                    
                    if (lat_lng == "" || lat_lng == "Failed to get location")
                        lat_lng = "34.0266,-118.2831";
                    String url = "http://csci571hw8-198219.appspot.com/?places=1&keyword="
                            +
                            formatURL(in_keyword)
                            +
                            "&category="
                            +
                            formatURL(in_cat)
                            +
                            "&distance="
                            +
                            in_distance
                            +
                            "&dist_from="
                            +
                            in_loc
                            +
                            "&input_loc="
                            +
                            formatURL(in_input_loc)
                            +
                            "&cur_loc="
                            +
                            lat_lng;

                    placeList.getInstance().clearPlaces();
                    Intent resultIntent = new Intent(getApplicationContext(), ResultActivityView.class);
                    resultIntent.putExtra("url", url);
                    //loadProgressDialog();
                    startActivity(resultIntent);





                }

            }
        });


        clear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getWindowToken(), 0);

                keyword.setText("");
                distance.setText("");
                input_location.setText("");
                category.setSelection(0);
                location_here.setChecked(true);
                errorMessage.setText("");
                error_loc.setText("");
                error_keyword.setText("");
            }
        });



    }

    String formatURL(String data)
    {
        String result = "";
        try {
            result = URLEncoder.encode(data, "utf8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return result;
    }

    float x1, x2, y1, y2;


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()){
            case MotionEvent.ACTION_DOWN:
                x1 = event.getX();
                y1 = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                x2 = event.getX();
                y2 = event.getY();
            if (x1 > x2){
                Intent resultIntent;
                resultIntent = new Intent(getApplicationContext(), FavActivity.class);
                String mess = "Youre now in fav tab";
                resultIntent.putExtra("message", mess);
                startActivity(resultIntent);
            }
            break;
        }

        return false;
        //return super.onTouchEvent(event);
    }
}
