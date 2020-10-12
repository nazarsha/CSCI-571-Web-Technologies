package com.example.hw9;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class FavActivity extends AppCompatActivity {

    protected RecyclerView recyclerView;
    private  TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fav);


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


        setTitle("Favorites");

        System.out.println("In onCreate Favs");
        Intent intent = getIntent();

        //String message = intent.getStringExtra("message");
        textView = findViewById(R.id.textView2);
        //textView.setText(message);


        search_tab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(resultIntent);


            }
        });

        recyclerView = findViewById(R.id.table_favs);
        LinearLayoutManager recyclerLayoutManager =
                new LinearLayoutManager(this);
        recyclerView.setLayoutManager(recyclerLayoutManager);

        List<Place> favs = new ArrayList<Place>(FavsList.getInstance().getFavs());




        if (FavsList.getInstance().getSize() == 0) {
            textView.setText("No favorites Found");
        }

        Boolean isInFavTab =  true;
        PlacesRecyclerViewAdapter recyclerViewAdapter = new
                PlacesRecyclerViewAdapter(favs, this, isInFavTab);
        recyclerView.setAdapter(recyclerViewAdapter);


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
                if (x1 < x2){
                    Intent resultIntent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(resultIntent);
                }
                break;
        }

        return false;
        //return super.onTouchEvent(event);
    }



    @Override
    protected void onResume() {

        List<Place> favs = new ArrayList<Place>(FavsList.getInstance().getFavs());
        System.out.println("In onResume Favs");

        if (FavsList.getInstance().getSize() == 0) {
            textView.setText("No favorites Found");
        }

        Boolean isInFavTab =  true;
        PlacesRecyclerViewAdapter recyclerViewAdapter = new
                PlacesRecyclerViewAdapter(favs, this, isInFavTab);
        recyclerView.setAdapter(recyclerViewAdapter);


        super.onResume();


    }
}
