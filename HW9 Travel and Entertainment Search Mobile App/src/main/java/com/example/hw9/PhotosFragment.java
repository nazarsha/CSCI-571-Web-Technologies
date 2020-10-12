package com.example.hw9;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;


public class PhotosFragment extends Fragment {
    private static GeoDataClient mGeoDataClient;
    //protected GeoDataClient mGeoDataClient;
    private static ArrayList<Bitmap> photoURLs ;
    private static Context mContext;
    private  TextView pm;

    public PhotosFragment() {
        // Required empty public constructor
    }

    // Request photos and metadata for the specified place.
    private void getPhotos(final View view ) {
        final String placeId = Detail.getInstance().getPlaceId();
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {
                try {
                    // Get the list of photos.
                    PlacePhotoMetadataResponse photos = task.getResult();
                    // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                    PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                    // Get the first photo in the list.
                    final LinearLayout  ll = (LinearLayout) view.findViewById(R.id.photos_table);
                    if (photoMetadataBuffer == null) {
                        Toast.makeText(mContext, "No photos found", Toast.LENGTH_SHORT);

                        pm.setText("No photos found!");
                        return;
                    }

                    for ( int i=0; i < photoMetadataBuffer.getCount(); i++ ){
                        PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(i).freeze();
                        // Get the attribution text.
                        CharSequence attribution = photoMetadata.getAttributions();
                        // Get a full-size bitmap for the photo.
                        Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                        photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                            @Override
                            public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                                PlacePhotoResponse photo = task.getResult();
                                Bitmap bitmap = photo.getBitmap();
                                System.out.println("Photo url: " + bitmap);
                                photoURLs.add(bitmap);
                                ImageView img =  new ImageView(mContext);
                                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(1000, 1000);

                                lp.setMargins(10, 5, 10, 5);
                                img.setLayoutParams(lp);
                                img.setImageBitmap(bitmap);
                                //img.getLayoutParams().width = 1000;
                                //img.setMaxWidth(1000);
                                ll.addView(img);
                            }
                        });

                    }


                    System.out.println("Total number of photos: "+ photoURLs.size());
                    photoMetadataBuffer.release();
                    pm.setText("No photos found!");

                } catch ( Exception e) {
                    pm.setText("No photos found!");
                }
            }
        });
    }


    // TODO: Rename and change types and number of parameters
    public static PhotosFragment newInstance(Context c) {
        PhotosFragment fragment = new PhotosFragment();
        // Construct a GeoDataClient.
        mContext = c;
        mGeoDataClient = Places.getGeoDataClient(c, null);
        photoURLs = new ArrayList<Bitmap>();

        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //if (getArguments() != null) {
        //} else {
        //}
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_photos, container, false);
        pm = (TextView) view.findViewById(R.id.photos_message);
        getPhotos(view);


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
