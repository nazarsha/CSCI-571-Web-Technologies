package com.example.hw9;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;



public class InfoFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private String address;
    private String phone  ;
    private String price  ;
    private String rating ;
    private String url    ;
    private String website;



    //private OnFragmentInteractionListener mListener;

    public InfoFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static InfoFragment newInstance() {
        InfoFragment fragment = new InfoFragment();
        Bundle args = new Bundle();
        args.putString("address", Detail.getInstance().getAddress());
        args.putString("phone"  , Detail.getInstance().getPhone()  );
        args.putString("price"  , Detail.getInstance().getPrice()  );
        args.putString("rating" , Detail.getInstance().getRating() );
        args.putString("url"    , Detail.getInstance().getUrl()    );
        args.putString("website", Detail.getInstance().getWebsite());
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            address = getArguments().getString("address");
            phone = getArguments().getString("phone");
            price = getArguments().getString("price");
            rating = getArguments().getString("rating");
            url = getArguments().getString("url");
            website = getArguments().getString("website");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_info, container, false);
        //TODO set the table

        TextView info_add_key =  (TextView) view.findViewById(R.id.info_address_key);
        TextView info_add_val =  (TextView) view.findViewById(R.id.info_address_val);
        if (address.length() > 0) {
            info_add_key.setText("Address");
            info_add_val.setText(address);
            info_add_key.setHeight(200);
            info_add_val.setHeight(200);
        }

        TextView info_phone_key =  (TextView) view.findViewById(R.id.info_phone_key);
        TextView info_phone_val =  (TextView) view.findViewById(R.id.info_phone_val);
        if (phone.length() > 0) {
            info_phone_key.setText("Phone Number");
            String curUrl = "<a href='" + phone + "'>" + phone + "</a>";
            info_phone_val.setText(Html.fromHtml(curUrl) );
            info_phone_val.setMovementMethod(LinkMovementMethod.getInstance());
            info_phone_val.setHeight(200);
            info_phone_val.setHeight(200);

            //info_phone_val.setText(phone);
        }

        TextView info_rating_key =  (TextView) view.findViewById(R.id.info_rating_key );
        RatingBar info_rating_val =  (RatingBar) view.findViewById(R.id.info_rating_val);
        if (rating.length() > 0) {
            info_rating_key.setText("Rating");
            info_rating_val.setRating(Float.parseFloat(rating));
            info_rating_key.setHeight(200);
        } else {
            info_rating_val.setVisibility(View.GONE);
            info_rating_key.setHeight(0);
        }

        TextView info_price_key =  (TextView) view.findViewById(R.id.info_price_key);
        TextView info_price_val =  (TextView) view.findViewById(R.id.info_price_val);
        if (price.length() > 0) {
            info_price_key.setText("Price Level");
            String p = "";
            for (int i=0; i < Integer.parseInt(price); i++){
                p += "$";
            }
            info_price_val.setText(p);
            info_price_key.setHeight(200);
            info_price_val.setHeight(200);

        }

        TextView info_url_key =  (TextView) view.findViewById(R.id.info_url_key);
        TextView info_url_val =  (TextView) view.findViewById(R.id.info_url_val);
        if (url.length() > 0) {
            info_url_key.setText("Google Page");
            String curUrl = "<a href='" + url + "'>" + url + "</a>";
            info_url_val.setText(Html.fromHtml(curUrl) );
            info_url_val.setMovementMethod(LinkMovementMethod.getInstance());
            info_url_val.setHeight(200);
            info_url_val.setHeight(200);

        }

        TextView info_website_key =  (TextView) view.findViewById(R.id.info_website_key);
        TextView info_website_val =  (TextView) view.findViewById(R.id.info_website_val);
        if (website.length() > 0) {
            info_website_key.setText("Website");
            String curUrl = "<a href='" + website + "'>" + website + "</a>";
            info_website_val.setText(Html.fromHtml(curUrl) );
            info_website_val.setMovementMethod(LinkMovementMethod.getInstance());
            info_website_val.setHeight(200);
            info_website_val.setHeight(200);

        }


        return view;
    }

/*    // TODO: Rename method, update argument and hook method into UI event
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
