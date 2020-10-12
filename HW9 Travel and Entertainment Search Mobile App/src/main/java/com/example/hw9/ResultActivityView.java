package com.example.hw9;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import static android.nfc.tech.MifareUltralight.PAGE_SIZE;
import static java.lang.Integer.max;
import static java.lang.Math.min;


public class ResultActivityView extends AppCompatActivity {

    public static final String TAG = "Volley Response Tag";
    public Integer pageNum;
    public  ArrayList<Place> places;
    public Context mContext;
    public Button next;


    //
    protected RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_view);
        pageNum = 0;
        places = new ArrayList<Place>();
        mContext = this;

        //loading dialog in the begining
        loadProgressDialog();

        System.out.println("In oncreate places");


        // Get the Intent that started this activity and extract the string
        setTitle("Search results");

        Intent intent = getIntent();
        String url = getIntent().getStringExtra("url");
        try {
            URL url_formatted = new URL(url);
            URI uri = url_formatted.toURI();
            url = uri.toString();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        //System.out.println ("URI : " + Uri.parse(url));

        final TextView mTextView = (TextView) findViewById(R.id.res_message);
        //mTextView.setText(url);
        //Log.i(TAG, "Places request" + url );
        System.out.println(url);

        recyclerView = findViewById(R.id.table_favs);
        final LinearLayoutManager recyclerLayoutManager =
                new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);

        final Button prev = (Button) findViewById(R.id.previousButtonResults);
        next = (Button) findViewById(R.id.nextButtonResults);
        prev.setEnabled(false);
        next.setEnabled(true);

        prev.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageNum <= 1) {
                    pageNum = 0;
                } else {
                    pageNum =  pageNum-1;
                }

                if (pageNum == 0 ){
                    prev.setEnabled(false);
                }
                else{
                    prev.setEnabled(true);
                }
                if (pageNum == 2 || (pageNum == 1 && places.size() < 40) || (pageNum == 0 && places.size() < 20)){
                    next.setEnabled(false);
                } else {
                    next.setEnabled(true);
                }



                List<Place> placesList = places.subList(pageNum*20, (pageNum+1)*20);
                PlacesRecyclerViewAdapter recyclerViewAdapter = new
                        PlacesRecyclerViewAdapter(placesList, mContext, false);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        });

        next.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (pageNum >= 1) {
                    pageNum = 2;
                } else {
                    pageNum =  pageNum+1;
                }

                prev.setEnabled(true);
                if (pageNum == 2 || (pageNum == 1 && places.size() < 40) || (pageNum == 0 && places.size() < 20)){
                    next.setEnabled(false);
                } else {
                    next.setEnabled(true);
                }



                List<Place> placesList = places.subList(pageNum*20, min ( (pageNum+1)*20, places.size()));
                PlacesRecyclerViewAdapter recyclerViewAdapter = new
                        PlacesRecyclerViewAdapter(placesList, mContext, false);
                recyclerView.setAdapter(recyclerViewAdapter);
            }
        });



        //mTextView.setText("Results are ready." + places.size());


/*        try {
            JSONArray placesData = new JSONArray(allPlaces);
            System.out.println("parser: " + placesData.length());
            this.places = createPlaces(placesData);
             System.out.println("places: " + places.size());
            List<Place> placesList = places.subList(0, min ( places.size(), 20) );



             PlacesRecyclerViewAdapter recyclerViewAdapter = new
                     PlacesRecyclerViewAdapter(placesList, this, false);
             recyclerView.setAdapter(recyclerViewAdapter);


             //mTextView.setText("Results are ready." + places.size());
            if (places.size() == 0) {
                mTextView.setText("No Results Found!");
            }

        } catch (JSONException e) {
            e.printStackTrace();
        } */

        RequestQueue queue = Volley.newRequestQueue(this);
        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        //mTextView.setText("Response is: "+ response.toString());
                        try {
                            //JSONArray parser = new JSONArray(response);

                            JSONArray placesData = new JSONArray(response);
                            places = createPlaces(placesData);
                            System.out.println("Total number of places retrieved: "+ places.size());
                            placeList.getInstance().setPlaceList(places);
                            //setPlacesList(places);
                            List<Place> placesList = places.subList(0, min ( places.size(), 20) );

                            if ((pageNum == 2) || (pageNum == 1 && places.size() < 40) || (pageNum == 0 && places.size() < 20)){
                                next.setEnabled(false);
                                System.out.println("pageNum " + pageNum + " totalplaces: " + places.size());
                            }

                            progressDiag.dismiss();
                            PlacesRecyclerViewAdapter recyclerViewAdapter = new
                                    PlacesRecyclerViewAdapter(placesList, mContext, false);
                            recyclerView.setAdapter(recyclerViewAdapter);


                            //mTextView.setText("Results are ready." + places.size());
                            if (places.size() == 0) {
                                mTextView.setText("No Results Found!");
                            }


                        } catch (JSONException e) {
                            progressDiag.dismiss();
                            e.printStackTrace();
                            System.out.println("Couldn't convert string response to json");
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //mTextView.setText("That didn't work!");
                Log.i(TAG, "Request Failed" + error );
                progressDiag.dismiss();
                pageNum = 0;
                if ( places.size() < 20){
                    next.setEnabled(false);
                    System.out.println("pageNum " + pageNum + " totalplaces: " + places.size());
                }

            }

        });




        stringRequest.setRetryPolicy(new RetryPolicy() {
            @Override
            public int getCurrentTimeout() {
                return 50000;
            }

            @Override
            public int getCurrentRetryCount() {
                return 50000;
            }

            @Override
            public void retry(VolleyError error) throws VolleyError {

            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);



    }


    public void setPlacesList(ArrayList<Place> p){
        this.places = p;
    }


    private ArrayList<Place> createPlaces(JSONArray placesData) throws JSONException {
        //processes the places data returned by backend

        ArrayList<Place> places= new ArrayList<Place>();
        for (Integer i=0; i < placesData.length(); i++){
            JSONObject cur = placesData.getJSONObject(i);
            Place newPl = new Place();
            //System.out.println(cur.toString());
            newPl.setPlaceIcon(cur.getString("icon"));
            newPl.setPlaceAddress(cur.getString("address"));
            newPl.setPlaceId(cur.getString("id"));
            newPl.setPlaceLat(cur.getString("lat"));
            newPl.setPlaceLon(cur.getString("lon"));
            newPl.setPlaceName(cur.getString("name"));
            places.add(newPl);

        }

        return places;


    }


    @Override protected void onResume() {
        super.onResume();

        //places = createPlaces(placesData);
        this.places = placeList.getInstance().getPlaceList();
        this.pageNum = 0;
/*        final Button next = (Button) findViewById(R.id.nextButtonResults);
        if (pageNum == 2 || (pageNum == 1 && places.size() < 40) || (pageNum == 0 && places.size() < 20)){
            next.setEnabled(false);
        } else {
            next.setEnabled(true);
        } */
        System.out.println("In onResume places");

        List<Place> placesList = this.places.subList(0, min ( places.size(), 20) );
        PlacesRecyclerViewAdapter recyclerViewAdapter = new
                PlacesRecyclerViewAdapter(placesList, mContext, false);
        recyclerView.setAdapter(recyclerViewAdapter);
//        progressDiag.dismiss();



    }

    ProgressDialog progressDiag;

    public void loadProgressDialog(){
        //https://www.journaldev.com/9652/android-progressdialog-example
        progressDiag = new ProgressDialog(this);
        progressDiag.setMax(100);
        progressDiag.setMessage("Fetching results");
        //progressDoalog.setTitle("ProgressDialog bar example");
        //progressDoalog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDiag.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDiag.show();

    }



}


//public final String allPlaces = "[ { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.1423376, lon: -118.2619206, id: 'ChIJHZPurvnAwoARr1cE7CnTOgA', name: 'Sista Mary\\'s Soul Food', photos: [ [Object] ], address: '420 W Colorado St, Glendale' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.9024443, lon: -118.3951853, id: 'ChIJGRk6Lv-zwoARddWEpRC2Zkk', name: 'True Food Kitchen', photos: [ [Object] ], address: '860 S Sepulveda Blvd Suite 100, El Segundo' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0598901, lon: -118.4459704, id: 'ChIJK3ByJIK8woAR5TgqOE-ON7I', name: 'Native Foods Cafe', photos: [ [Object] ], address: '1114 Gayley Ave, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.9431005, lon: -118.398948, id: 'ChIJJzkfXymxwoARUh_rp0IjtSI', name: 'Panda Express', photos: [ [Object] ], address: '800 World Way, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0430878, lon: -118.3092378, id: 'ChIJRa2173q4woAR0HVbXdm1otg', name: 'Carl\\'s Jr.', photos: [ [Object] ], address: '1611 S Western Ave, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.9016536, lon: -118.2930363, id: 'ChIJX8uKBnrKwoARI7LEeBXYB_Q', name: 'Long John Silver\\'s', photos: [ [Object] ], address: '1078 W Rosecrans Ave, Gardena' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.9466926, lon: -118.4074192, id: 'ChIJW9mL5SaxwoARGeAi91j8B0E', name: 'Shake Shack - LAX', photos: [ [Object] ], address: 'Terminal 3, 1 World Way, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0444352, lon: -118.2560782, id: 'ChIJy6tth7XHwoARgPOJOiKZkO4', name: 'Shake Shack - Downtown LA', photos: [ [Object] ], address: '400 W 8th St, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0621724, lon: -118.3497829, id: 'ChIJnfI81CG5woARwLHvOD8m6fY', name: 'Five Guys', photos: [ [Object] ], address: '5550 Wilshire Blvd, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0270583, lon: -118.2763772, id: 'ChIJv6eDft3HwoARkr8tW31wfhY', name: 'Five Guys', photos: [ [Object] ], address: '530 W 27th St, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0871449, lon: -118.3812408, id: 'ChIJz98x8aW-woARijQewQDhP28', name: 'Five Guys', photos: [ [Object] ], address: '8731 Santa Monica Boulevard, West Hollywood' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.1454806, lon: -118.1539142, id: 'ChIJtUeFt3bDwoARdj3_2AProOg', name: 'True Food Kitchen', photos: [ [Object] ], address: '168 W Colorado Blvd, Pasadena' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0492038, lon: -118.2605172, id: 'ChIJ_9Vx3rDHwoARvomUYXADA4s', name: 'Five Guys', photos: [ [Object] ], address: '735 S Figueroa St, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.985858, lon: -118.394787, id: 'ChIJUyoBEauwwoARnQz30LNtjwk', name: 'Five Guys', photos: [ [Object] ], address: 'Culver City Location, 6000 Sepulveda Blvd, Culver City' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0785873, lon: -118.3764199, id: 'ChIJ12ENnLO-woAR4HeJnqmtJOA', name: 'Real Food Daily', photos: [ [Object] ], address: '414 N La Cienega Blvd, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.9406033, lon: -118.4064977, id: 'ChIJyyALcSaxwoARYGP9nSoZWJg', name: 'Real Food Daily', photos: [ [Object] ], address: '1 World Way, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/cafe-71.png', lat: 33.9962172, lon: -118.4563494, id: 'ChIJ0zR6mSu7woAR4GeLs8NKAnQ', name: 'Superba Food + Bread', photos: [ [Object] ], address: '1900 S Lincoln Blvd, Venice' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.029913, lon: -118.2687188, id: 'ChIJY4FmP9vHwoARvmT0O120Sp4', name: 'Food Haus Cafe', photos: [ [Object] ], address: '2106 S Olive St, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0908993, lon: -118.347975, id: 'ChIJS6QwtNe-woARLQt0PE-Ck0I', name: 'FOOD LAB', photos: [ [Object] ], address: '7253 Santa Monica Blvd, West Hollywood' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0427223, lon: -118.2529723, id: 'ChIJwfCk1crHwoARLRa9EWdHDws', name: 'Wild Living Foods', photos: [ [Object] ], address: '760 S Main St, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0458431, lon: -118.4481072, id: 'ChIJtWb7inC7woARNRLODSLVqbY', name: 'Nina\\'s Mexican Food', photos: [ [Object] ], address: '1651 Sawtelle Blvd, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.1410298, lon: -118.1318744, id: 'ChIJpyiWk17DwoARBcWxdZ9woFQ', name: 'Real Food Daily', photos: [ [Object] ], address: '899 E Del Mar Blvd, Pasadena' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0603987, lon: -118.4450372, id: 'ChIJtSJ9j4G8woARVGVwDskvFJA', name: 'TLT Food', photos: [ [Object] ], address: '1116 Westwood Blvd, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.90291029999999, lon: -118.3943099, id: 'ChIJ1eh4H_-zwoAR6tv2EM5BFzk', name: 'Superba Food + Bread', photos: [ [Object] ], address: '830 S Sepulveda Blvd #100, El Segundo' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0111369, lon: -118.291906, id: 'ChIJDWnRkwbIwoARrXpagVP1hSw', name: 'Carl\\'s Jr.', photos: [ [Object] ], address: '1001 W Martin Luther King Jr Blvd, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.9614098, lon: -118.3721805, id: 'ChIJvRH9UCK3woARPzhysp06QpI', name: 'Cafe Cancun Mexican Food', photos: [ [Object] ], address: '909 W Manchester Blvd, Inglewood' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.9102972, lon: -118.3263, id: 'ChIJ1aQ2tsO1woARiUHHHTMj1M8', name: 'Alberto\\'s Mexican Food', photos: [ [Object] ], address: '13416 Crenshaw Blvd, Gardena' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.98221410000001, lon: -118.2824861, id: 'ChIJ32FsTjHIwoARFbKsL77s4zw', name: 'The Family Soul Food Restaurant', photos: [ [Object] ], address: '6300 S Figueroa St, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0580434, lon: -118.4193676, id: 'ChIJh2dXfoy7woAR5D7cEBzQHfo', name: 'Century City Mall Food Court', photos: [ [Object] ], address: '10250 California State Route 2, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.9984509, lon: -118.3311251, id: 'ChIJ8XK86I63woARr8aszMpIvGk', name: 'Dulan\\'s On Crenshaw Soul Food Restaurant', photos: [ [Object] ], address: '4859 Crenshaw Blvd, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.9292872, lon: -118.1621186, id: 'ChIJlZNXgFPMwoARk6EsAPmMBl8', name: 'Alberts Mexican Food', photos: [ [Object] ], address: '7347 Imperial Hwy, Downey' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0235001, lon: -118.1353829, id: 'ChIJTUhB-cfPwoARF3G5kXa2UIM', name: 'Alberto\\'s Mexican Food', photos: [ [Object] ], address: '3000 W Beverly Blvd, Montebello' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.025159, lon: -118.2529136, id: 'ChIJLYFYSNXHwoARGbfGQj08MEY', name: 'Chinatown Fast Food', photos: '', address: '1000 E Washington Blvd, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.959751, lon: -118.2892021, id: 'ChIJbZgNaNvJwoARRAd9Q4zpFos', name: 'Lamberto\\'s Mexican Food', photos: [ [Object] ], address: '894 W Manchester Ave, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.9618627, lon: -118.3569237, id: 'ChIJEb8nRAO3woARfqm9O_Qz_HE', name: 'LOS GUZMAN MEXICAN FOOD', photos: [ [Object] ], address: '143 W Manchester Blvd, Inglewood' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.056006, lon: -118.2678216, id: 'ChIJMUGYMqTHwoAR9ZXM6qXz0gY', name: 'Thai Food Express', photos: [ [Object] ], address: '1406 W 6th St, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.9932192, lon: -118.256118, id: 'ChIJV4ml11nIwoARL-o_PXjkIjg', name: 'Granny\\'s Kitchen Southern Style Soul Food', photos: [ [Object] ], address: '4728, 5440 S Central Ave, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0544305, lon: -118.2630842, id: 'ChIJGWstlK_HwoARdk8XUDrsTls', name: 'Chipotle Mexican Grill', photos: [ [Object] ], address: '1122 W 6th St, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.041991, lon: -118.258407, id: 'ChIJd0TBKcrHwoARbdU8Wx2vFuU', name: 'Chipotle Mexican Grill', photos: [ [Object] ], address: '301 W Olympic Blvd Suite A, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.8821691, lon: -118.3093858, id: 'ChIJY3iaJnq1woAReiagCAKEm90', name: 'Los 3 Potosino\\'s Mexican Food', photos: [ [Object] ], address: '16323 S Western Ave, Gardena' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0734649, lon: -118.1727648, id: 'ChIJea2x77rFwoARTcGNDy9VC2M', name: 'Albert Mexican Food', photos: [ [Object] ], address: '5189 Alhambra Ave, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.9646419, lon: -118.3510364, id: 'ChIJz3EetAa3woARRFYSr46F9rY', name: 'Randy\\'s Donuts & Chinese Food', photos: [ [Object] ], address: '210 N Market St, Inglewood' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.9182519, lon: -118.4158289, id: 'ChIJlZMz5EGxwoAR93FQkJxuTck', name: 'El Tarasco Mexican Food', photos: [ [Object] ], address: '210 Main St, El Segundo' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.9591147, lon: -118.3958441, id: 'ChIJOXdqds2wwoARQ9umaHunYWg', name: 'El Tarasco Mexican Food', photos: [ [Object] ], address: '8620 S Sepulveda Blvd, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/shopping-71.png', lat: 33.988638, lon: -118.354489, id: 'ChIJSwdnhnC3woAR_f2MCa7ftaE', name: 'Simply Wholesome', photos: [ [Object] ], address: '4508 W Slauson Ave, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.078487, lon: -118.128456, id: 'ChIJc9ZG3jjFwoARBXTRCHYQ3sc', name: 'Pepe\\'s Finest Mexican Food', photos: [ [Object] ], address: '511 W Valley Blvd, Alhambra' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.9937208, lon: -118.2741638, id: 'ChIJfzIPM0fIwoARzM8a7vxYUPA', name: 'Lucky\\'s Chinese fast food', photos: [ [Object] ], address: '5333 S Main St #103, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.9355791, lon: -118.3773878, id: 'ChIJL7xZX8a2woARpJo5V-gXq2Y', name: 'The Proud Bird Food Bazaar & Events Center', photos: [ [Object] ], address: '11022 Aviation Blvd, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0949936, lon: -118.1523069, id: 'ChIJXQR_EfzEwoARyUVk6uysecE', name: 'Super Albert Mexican Food', photos: [ [Object] ], address: '2600 W Main St, Alhambra' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0205325, lon: -118.3560945, id: 'ChIJH0qxyLe5woARgsl_TO9xPUg', name: 'Kay Kay Chinese Food', photos: [ [Object] ], address: '3625 South La Brea Ave, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.1086482, lon: -118.1940523, id: 'ChIJa1KwfxDEwoARi9Vdf8T3SF0', name: 'Folliero\\'s Italian Food and Pizza', photos: [ [Object] ], address: '5566 N Figueroa St, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.1249451, lon: -118.265129, id: 'ChIJfRTqEejAwoARQNYYHAEfID8', name: 'Del Taco', photos: [ [Object] ], address: '3020 Los Feliz Blvd, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.9611988, lon: -118.3536954, id: 'ChIJqYLcygO3woARJ994uCiWfZA', name: 'Mutiara Food & Market', photos: [ [Object] ], address: '225 South La Brea Ave, Inglewood' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.9888277, lon: -118.3340017, id: 'ChIJvUwNU5e3woARBZfnQqcxJPc', name: 'Mr. Wisdom Organic Health Food Hare Krishna', photos: [ [Object] ], address: '3526 W Slauson Ave, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.9545156, lon: -118.2109105, id: 'ChIJM_mEwlXJwoARS1wQ9jqh8Rs', name: 'Albert\\'s Mexican Food', photos: [ [Object] ], address: '3300 Firestone Blvd, South Gate' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.076231, lon: -118.158023, id: 'ChIJUwZjzAnFwoAReqeYcXBMZso', name: 'Aloha Food Factory', photos: [ [Object] ], address: '2990 W Valley Blvd, Alhambra' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.1195629, lon: -118.2276413, id: 'ChIJZR6twUnBwoARn8sV6slkMA0', name: 'Nanay Gloria\\'s Filipino Restaurant', photos: [ [Object] ], address: '3756 W Ave 40 L, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 33.94526159999999, lon: -118.307409, id: 'ChIJA1vkI0K2woARQ6Gpc1sroVQ', name: 'Dulan\\'s Soul Food Kitchen', photos: [ [Object] ], address: '1714 W Century Blvd, Los Angeles' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.094919, lon: -118.1568935, id: 'ChIJ4ST2zfjEwoARD6VcNfgxNSU', name: 'Caramba Mexican Food', photos: [ [Object] ], address: '3020 W Main St, Alhambra' }, { icon: 'https://maps.gstatic.com/mapfiles/place_api/icons/restaurant-71.png', lat: 34.0474499, lon: -118.2830637, id: 'ChIJ66i-upDHwoARv50nFobSbBc', name: 'Margarita\\'s Mexican Food', photos: [ [Object] ], address: '2101 W Pico Blvd # 5, Los Angeles' } ]";