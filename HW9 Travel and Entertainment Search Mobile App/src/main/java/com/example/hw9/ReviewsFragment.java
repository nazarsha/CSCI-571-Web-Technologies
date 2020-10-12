package com.example.hw9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class ReviewsFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private JSONArray googleJson, yelpJson;
    private ArrayList<Review> googleRevs, yelpRevs;
    private static Context mContext;
    private TextView message;


    // TODO: Rename and change types of parameters

    //private OnFragmentInteractionListener mListener;

    public ReviewsFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ReviewsFragment newInstance(Context c) {
        ReviewsFragment fragment = new ReviewsFragment();
        Bundle args = new Bundle();
        mContext = c;

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        googleRevs = new ArrayList<Review> ();
        yelpRevs = new ArrayList<Review>();


        if (getArguments() != null) {
            googleJson = Detail.getInstance().getGoogle_reviews();
            yelpJson = Detail.getInstance().getYelp_reviews();
            System.out.println(googleJson);
            System.out.println(yelpJson);

            for (int i=0; i < googleJson.length(); i++){
                try {
                    Review r = new Review(googleJson.getJSONObject(i));
                    googleRevs.add(r);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            for (int i=0; i < yelpJson.length(); i++){
                try {
                    yelpRevs.add(new Review(yelpJson.getJSONObject(i)));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }
    }

   @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

       View view = inflater.inflate(R.layout.fragment_reviews, container, false);
       TextView rm = (TextView) view.findViewById(R.id.photos_message);
       message = (TextView) view.findViewById(R.id.review_message);



       final RecyclerView recyclerView;
       recyclerView = view.findViewById(R.id.table_revs);
       LinearLayoutManager recyclerLayoutManager =
               new LinearLayoutManager(mContext);
       recyclerView.setLayoutManager(recyclerLayoutManager);



       final Spinner reviewType = (Spinner) view.findViewById(R.id.reviewType);
       List<String> list = new ArrayList<String>();
       list.add("Google Reviews");
       list.add("Yelp Reviews");
       // Create an ArrayAdapter using the string array and a default spinner layout
       ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, list);
       // Specify the layout to use when the list of choices appears
       adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       // Apply the adapter to the spinner
       reviewType.setAdapter(adapter);


       Spinner  reviewOrder = (Spinner) view.findViewById(R.id.reviewOrder);
       List<String> list2 = new ArrayList<String>();
       list2.add("Default Order");
       list2.add("Highest Rating");
       list2.add("Lowest Rating");
       list2.add("Most Recent");
       list2.add("Least Recent");
       // Create an ArrayAdapter using the string array and a default spinner layout
       ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(mContext, android.R.layout.simple_spinner_item, list2);
       // Specify the layout to use when the list of choices appears
       adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
       // Apply the adapter to the spinner
       reviewOrder.setAdapter(adapter2);


       ReviewsRecycleViewAdapter recyclerViewAdapter = new
               ReviewsRecycleViewAdapter(googleRevs, mContext);
       recyclerView.setAdapter(recyclerViewAdapter);


       reviewType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
               // your code here
               switch (position){
                   case 0:
                       ReviewsRecycleViewAdapter recyclerViewAdapter = new
                               ReviewsRecycleViewAdapter(googleRevs, mContext);
                       recyclerView.setAdapter(recyclerViewAdapter);
                       if (googleRevs.size() == 0){
                           message.setText("No Reviews Found!");
                       } else {
                           message.setText("");
                       }
                       break;
                   case 1:
                        recyclerViewAdapter = new
                               ReviewsRecycleViewAdapter(yelpRevs, mContext);
                       recyclerView.setAdapter(recyclerViewAdapter);
                       if (yelpRevs.size() == 0){
                           message.setText("No Reviews Found!");
                       } else {
                           message.setText("");
                       }
                        break;
                    default:
                         recyclerViewAdapter = new
                                ReviewsRecycleViewAdapter(googleRevs, mContext);
                        recyclerView.setAdapter(recyclerViewAdapter);
                        if (googleRevs.size() == 0){
                            message.setText("No Reviews Found!");
                        } else {
                            message.setText("");
                        }

                        break;
               }

           }

           @Override
           public void onNothingSelected(AdapterView<?> parentView) {
               // your code here
           }

       });

//https://stackoverflow.com/questions/1337424/android-spinner-get-the-selected-item-change-event
       reviewOrder.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
           @Override
           public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
               // your code here
               switch (position){
                   case 0:
                       ArrayList<Review> curRevs = ( reviewType.getSelectedItemPosition() == 0  ) ? new ArrayList<Review>(googleRevs) : new ArrayList<Review>(yelpRevs);
                       curRevs = ( reviewType.getSelectedItemPosition() == 0  ) ? googleRevs : yelpRevs;
                       ReviewsRecycleViewAdapter recyclerViewAdapter = new
                               ReviewsRecycleViewAdapter(curRevs, mContext);
                       recyclerView.setAdapter(recyclerViewAdapter);
                       break;
                   case 1:
                       curRevs = ( reviewType.getSelectedItemPosition() == 0  ) ? new ArrayList<Review>(googleRevs) : new ArrayList<Review>(yelpRevs);
                       Collections.sort(curRevs, Review.reviewRating);
                       Collections.reverse(curRevs);
                       recyclerViewAdapter = new
                               ReviewsRecycleViewAdapter(curRevs, mContext);
                       recyclerView.setAdapter(recyclerViewAdapter);
                       break;
                   case 2:
                       curRevs = ( reviewType.getSelectedItemPosition() == 0  ) ? new ArrayList<Review>(googleRevs) : new ArrayList<Review>(yelpRevs);
                       Collections.sort(curRevs, Review.reviewRating);
                       recyclerViewAdapter = new
                               ReviewsRecycleViewAdapter(curRevs, mContext);
                       recyclerView.setAdapter(recyclerViewAdapter);
                       break;
                   case 3:
                       curRevs = ( reviewType.getSelectedItemPosition() == 0  ) ? new ArrayList<Review>(googleRevs) : new ArrayList<Review>(yelpRevs);
                       Collections.sort(curRevs, Review.reviewTimeComp);
                       Collections.reverse(curRevs);
                       recyclerViewAdapter = new
                               ReviewsRecycleViewAdapter(curRevs, mContext);
                       recyclerView.setAdapter(recyclerViewAdapter);
                       break;
                   case 4:
                       curRevs = ( reviewType.getSelectedItemPosition() == 0  ) ? new ArrayList<Review>(googleRevs) : new ArrayList<Review>(yelpRevs);
                       Collections.sort(curRevs, Review.reviewTimeComp);
                       recyclerViewAdapter = new
                               ReviewsRecycleViewAdapter(curRevs, mContext);
                       recyclerView.setAdapter(recyclerViewAdapter);
                       break;
                   default:
                       curRevs = ( reviewType.getSelectedItemPosition() == 0  ) ? new ArrayList<Review>(googleRevs) : new ArrayList<Review>(yelpRevs);
                       recyclerViewAdapter = new
                               ReviewsRecycleViewAdapter(curRevs, mContext);
                       recyclerView.setAdapter(recyclerViewAdapter);
                       break;
               }

           }

           @Override
           public void onNothingSelected(AdapterView<?> parentView) {
               // your code here
           }

       });


        // Inflate the layout for this fragment
        return view;
    }

    /*     // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    } */
}
