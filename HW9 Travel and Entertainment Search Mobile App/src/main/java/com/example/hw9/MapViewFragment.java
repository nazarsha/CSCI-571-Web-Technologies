package com.example.hw9;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.hw9.ResultActivityView.TAG;


public class MapViewFragment extends Fragment implements OnMapReadyCallback {

    MapView mMapView;
    private GoogleMap googleMap;
    private  static Context mContex;
    SupportMapFragment mapFragment;
    private  ArrayList<Polyline> polyLines;
    private String destId;

    public MapViewFragment() {
        // Required empty public constructor
    }

    public static MapViewFragment newInstance( Context c) {
        MapViewFragment fragment = new MapViewFragment();
        mContex = c;

        return fragment;
    }


   /* @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    } */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map_view, container, false);

        polyLines = new ArrayList<Polyline>();
        destId = "";

        mapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment == null){
            FragmentManager fm = getFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            mapFragment = SupportMapFragment.newInstance();
            ft.replace(R.id.map, mapFragment).commit();
        }

        final Spinner travelMode = (Spinner) rootView.findViewById(R.id.travelModeSpinner);
        List<String> list = new ArrayList<String>();
        list.add("DRIVING");
        list.add("BICYCLING");
        list.add("TRANSIT");
        list.add("WALKING");
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContex, android.R.layout.simple_spinner_item, list);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        travelMode.setAdapter(adapter);


        AutoCompleteTextView destination = rootView.findViewById(R.id.destinationMap);

        CustomAutoCompleteAdapter destinationAdapter =  new CustomAutoCompleteAdapter(mContex);
        destination.setAdapter(destinationAdapter);
        destination.setOnItemClickListener( new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                /*Toast.makeText(mContex,
                        "selected place "
                                + ((Place)adapterView.
                                getItemAtPosition(i)).getPlaceText()
                        , Toast.LENGTH_LONG).show(); */
                //do something with the selection
                //searchScreen();

                //System.out.print("Selected place is: " + txt );
                //destination.setText();

                for (int j=0; j < polyLines.size(); j++){
                    polyLines.get(j).remove();
                }

                String txt = ((Place)adapterView.
                        getItemAtPosition(i)).getPlaceText();

                String dest = ((Place)adapterView.
                        getItemAtPosition(i)).getPlaceId();

                destId = dest;
                sendDirectionReq(travelMode.getSelectedItemPosition(), Detail.getInstance().getPlaceId(), dest);

            }
        } );


/*
final TextView destination = (TextView) rootView.findViewById(R.id.destinationMap);
        destination.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String str1 = destination.getText().toString().trim();
                    System.out.println("Destination specified" + str1);
                    if (str1.length() == 0 ){
                        return;
                    } else {

                        sendDirectionReq(travelMode.getSelectedItemPosition(), Detail.getInstance().getPlaceId(), str1);

                    }
                }
            }
        }); */

        /*PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getActivity().getFragmentManager().findFragmentById(R.id.map_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {

            @Override
            public void onPlaceSelected(com.google.android.gms.location.places.Place thisPlace) {
                Log.i(TAG, "Place: " + thisPlace.getName());
                //TODO handle case when destination is chosen and transit mode is changed. see the condition for that.


                for (int i=0; i < polyLines.size(); i++){
                    polyLines.get(i).remove();
                }

                String str1 = thisPlace.getId();
                System.out.println("Destination specified from autocomplete" + str1);
                if (str1.length() == 0 ){
                    return;
                } else {

                    sendDirectionReq(travelMode.getSelectedItemPosition(), Detail.getInstance().getPlaceId(), str1);

                }


            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i(TAG, "An error occurred: " + status);
                Toast toast = Toast.makeText(mContex,"Destination not found", Toast.LENGTH_SHORT);
                toast.show();
            }
        });

        */

        travelMode.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                // your code here
                for (int i=0; i < polyLines.size(); i++){
                    polyLines.get(i).remove();
                }
                if (destId == null) {return; }
                 String str1 = destId.trim();
                if (str1.length() == 0 ){
                    return;
                } else {

                    System.out.println("Spinner has changed");
                    sendDirectionReq(travelMode.getSelectedItemPosition(), Detail.getInstance().getPlaceId(), str1);

                }



            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });

        mapFragment.getMapAsync(this);

        return rootView;
    }


/*    private AdapterView.OnItemClickListener onItemClickListener =
            new AdapterView.OnItemClickListener(){
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {


                    Toast.makeText(mContex,
                            "selected place "
                                    + ((Place)adapterView.
                                    getItemAtPosition(i)).getPlaceText()
                            , Toast.LENGTH_LONG).show();
                    //do something with the selection
                    //searchScreen();

                    //System.out.print("Selected place is: " + txt );
                    //destination.setText();
                    String txt = ((Place)adapterView.
                            getItemAtPosition(i)).getPlaceText();

                    String dest = ((Place)adapterView.
                            getItemAtPosition(i)).getPlaceId();


                }
            };

    public void searchScreen(){
        Intent i = new Intent();
        i.setClass(mContex, AutoCompleteActivity.class);
        startActivity(i);
    }  */

    @Override
    public void onMapReady(GoogleMap gMap) {
        MapsInitializer.initialize(mContex);
        this.googleMap = gMap;

        googleMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(Detail.getInstance().getLat(), Detail.getInstance().getLon());
        Marker src = googleMap.addMarker(new MarkerOptions().position(sydney).title(Detail.getInstance().getName()));
        src.showInfoWindow();
        CameraPosition lib = CameraPosition.builder().target(sydney).zoom(15).bearing(0).build();
        googleMap.moveCamera(CameraUpdateFactory.newCameraPosition(lib));

        googleMap.getUiSettings().setMyLocationButtonEnabled(false);

        googleMap.getUiSettings().setZoomControlsEnabled(true);

        //googleWebDirections key:





    }

    public  void sendDirectionReq (Integer travelMode, String source, String destination){
        String tMode = "DRIVING";
        switch (travelMode){
            case 1:
                tMode = "BICYCLING";
                break;
            case 2:
                tMode = "TRANSIT";
                break;
            case 3:
                tMode = "WALKING";
                break;
            default:
                tMode = "DRIVING";
                break;
        }


        String url = "https://maps.googleapis.com/maps/api/directions/json?key=&";
        url += "origin=place_id:"+ source + "&destination=place_id:" + destination + "&mode=" + tMode.toLowerCase();
        RequestQueue queue = Volley.newRequestQueue(mContex);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.toString());
                        try {
                            JSONObject resp = new JSONObject(response);
                            System.out.println("Got the route: " + resp);
                            DataParser dParaser = new DataParser();
                            List<List<HashMap<String, String>>> routes = routes = dParaser.parse(resp);
                            drawPolyLine(routes);

                        } catch (JSONException e) {
                            e.printStackTrace();
                            System.out.println("Couldn't convert string response to json");
                            Toast toast = Toast.makeText(mContex,"Failed to get the directions", Toast.LENGTH_SHORT);
                            toast.show();

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
                System.out.println("Couldn't get routes" + error);
                Log.i(TAG, "Request Failed" + error );

            }

        });

        queue.add(stringRequest);

    }


/*   @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

    } */

    //@Override
    protected void drawPolyLine(List<List<HashMap<String, String>>> result) {
        ArrayList<LatLng> points = new ArrayList<LatLng>();
        PolylineOptions lineOptions = null;

        // Traversing through all the routes
        for (int i = 0; i < result.size(); i++) {
            //points = new ArrayList<>();
            lineOptions = new PolylineOptions();

            // Fetching i-th route
            List<HashMap<String, String>> path = result.get(i);

            // Fetching all the points in i-th route
            for (int j = 0; j < path.size(); j++) {
                HashMap<String, String> point = path.get(j);

                double lat = Double.parseDouble(point.get("lat"));
                double lng = Double.parseDouble(point.get("lng"));
                LatLng position = new LatLng(lat, lng);

                points.add(position);
            }

            // Adding all the points in the route to LineOptions
            lineOptions.addAll(points);
            lineOptions.width(10);
            lineOptions.color(Color.BLUE);

            Log.d("onPostExecute","onPostExecute lineoptions decoded");

        }

        // Drawing polyline in the Google Map for the i-th route
        if(lineOptions != null) {
            Polyline curPoly = googleMap.addPolyline(lineOptions);
            polyLines.add(curPoly);
            if (points.size() < 2) return;
            LatLng second = points.get(points.size()-1);
            googleMap.addMarker(new MarkerOptions().position(second));



            //if (points.size() < 2) return;
            LatLng first = points.get(0);
            LatLngBounds.Builder builder = LatLngBounds.builder();
            builder.include(first);
            builder.include(second);
            //LatLng second = points.get(points.size()-1);
            final LatLngBounds newBounds = builder.build();
                googleMap.moveCamera(CameraUpdateFactory.newLatLngBounds(newBounds, 100));


        }
        else {
            Log.d("onPostExecute","without Polylines drawn");
        }
    }

}