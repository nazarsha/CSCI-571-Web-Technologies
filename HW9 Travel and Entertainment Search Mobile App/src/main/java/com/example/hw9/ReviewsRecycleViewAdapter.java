package com.example.hw9;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.lang.Integer.max;

public class ReviewsRecycleViewAdapter extends
        RecyclerView.Adapter<ReviewsRecycleViewAdapter.ViewHolder> {

    private ArrayList<Review>  reviews;
    private Context mcontex;



    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView review_name;
        public TextView review_text;
        public TextView review_time;
        public ImageView review_picture;
        public RatingBar review_rating;



        public ViewHolder(View view) {

            super(view);

            review_name = view.findViewById(R.id.review_name);
            review_text = view.findViewById(R.id.review_text);
            review_time = view.findViewById(R.id.review_time);
            review_picture= view.findViewById(R.id.review_picture);
            review_rating = view.findViewById(R.id.review_rating);



        }
    }


    public ReviewsRecycleViewAdapter(ArrayList<Review> reviews,  Context ctx) {


        mcontex = ctx;
        this.reviews = reviews;

    }


    @Override
    public int getItemCount() {
        return this.reviews .size();
    }

    @Override
    public ReviewsRecycleViewAdapter.ViewHolder
    onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_item, parent, false);

        ReviewsRecycleViewAdapter.ViewHolder viewHolder =
                new ReviewsRecycleViewAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ReviewsRecycleViewAdapter.ViewHolder holder, int position) {


        Review c = reviews.get(position);

        //TODO add a linearlayout to ratingbar

        //holder.review_name.setText(c.getName());
        String url = c.getUrl();
        String curUrl = "<a href='" + url + "' >" + c.getName() + "</a>";
        holder.review_name.setText(Html.fromHtml(curUrl),  TextView.BufferType.SPANNABLE );
        holder.review_name.setMovementMethod(LinkMovementMethod.getInstance());
        //holder.review_name.setTextColor(Color.BLUE);
        //https://stackoverflow.com/questions/2730706/highlighting-text-color-using-html-fromhtml-in-android

        holder.review_text.setText(c.getText());
        //holder.review_picture.setImageResource();
        holder.review_rating.setRating(c.getRating());
        holder.review_time.setText(c.getTimeFormatted());

        new PlacesRecyclerViewAdapter.DownloadImageTask(holder.review_picture)
                .execute(c.getPicture());
        //Review curRev = new Review()


    }

}
