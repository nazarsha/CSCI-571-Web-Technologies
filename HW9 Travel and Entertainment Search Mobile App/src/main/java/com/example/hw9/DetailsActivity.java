package com.example.hw9;

import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.os.DeadObjectException;
import android.support.design.widget.TabLayout;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.io.Serializable;

public class DetailsActivity extends AppCompatActivity {


    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        mContext = this;


        if (Detail.getInstance().getName() == "")
            setTitle("Details");
        else
            setTitle(Detail.getInstance().getName());

        Intent intent = getIntent();

        //TextView textView = findViewById(R.id.textView2);
        //textView.setText(message);

        Detail details = Detail.getInstance();

        ViewPager viewPager = (ViewPager) findViewById(R.id.viewpager);




        //viewPager.setAdapter(new DetailsPageAdapter(this));
        viewPager.setAdapter(new DetailFragPagerAdapter(getSupportFragmentManager(), this));



        TabLayout tabLayout = (TabLayout) findViewById(R.id.detailsTabs);
        tabLayout.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        //tabLayout.addTab(tabLayout.newTab().setText("Tab 1"));
        //tabLayout.addTab(tabLayout.newTab().setText("Tab 2"));
        //tabLayout.addTab(tabLayout.newTab().setText("Tab 3"));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setTabTextColors(ColorStateList.valueOf(Color.WHITE));
        tabLayout.setMinimumWidth(30);


        /*
        tabLayout.getTabAt(0).setText("INFO");
        tabLayout.getTabAt(0).setIcon(R.drawable.info_outline);
        tabLayout.setMinimumWidth(100);
        tabLayout.getTabAt(1).setText("PHOTOS");
        tabLayout.getTabAt(1).setIcon(R.drawable.photos);
        tabLayout.getTabAt(2).setText("MAP");
        tabLayout.getTabAt(2).setIcon(R.drawable.maps);
        tabLayout.getTabAt(3).setText("REVIEWS");
        tabLayout.getTabAt(3).setIcon(R.drawable.review); */



/*        PagerTabStrip pagerTabStrip = (PagerTabStrip) findViewById(R.id.detailsTabs);
        pagerTabStrip.setDrawFullUnderline(true);
        pagerTabStrip.setBackgroundColor(getResources().getColor(R.color.colorPrimary));
        pagerTabStrip.setPadding(0,0,0,0);
        pagerTabStrip.setGravity(Gravity.RIGHT);
        pagerTabStrip.setTextColor(Color.WHITE);
        pagerTabStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20f);
        //pagerTabStrip.se
*/



        System.out.println("Catched Details in DetailsActivity: " + details);



    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.actions, menu);

/*        Toolbar myToolbar = (Toolbar) findViewById(R.id.detailTabsToolbar);
        //setSupportActionBar(myToolbar);
        Menu tabsMenu = myToolbar.getMenu();
        getMenuInflater().inflate(R.menu.tabs, tabsMenu); */

        String tempPLID = Detail.getInstance().getPlaceId();
        if (FavsList.getInstance().isFav(tempPLID)){
            menu.getItem(1).setIcon(R.drawable.heart_fill_white);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.topFavIcon:
                if (FavsList.getInstance().isFav(Detail.getInstance().getPlaceId())){
                    Toast.makeText(this, "Removed " + Detail.getInstance().getName()+ " from favorites", Toast.LENGTH_SHORT).show();
                    //System.out.println("Item trying to remove: " + Detail.getInstance().getPlaceId());
                    //System.out.println("Item trying to: " + Detail.getInstance().getPlaceObj().getPlaceId());
                    FavsList.getInstance().removeFav(Detail.getInstance().getPlaceId());
                    item.setIcon(R.drawable.heart_outline_white);



                } else {
                    FavsList.getInstance().addFav(Detail.getInstance().getPlaceObj());
                    item.setIcon(R.drawable.heart_fill_white);
                    Toast.makeText(this, "Added " + Detail.getInstance().getName()+ " to favorites", Toast.LENGTH_SHORT).show();
                }

                // User chose the "Settings" item, show the app settings UI...
                return true;

            case R.id.topShareIcon:
                // User chose the "Favorite" action, mark the current item
                // as a favorite...

                String url = "https://twitter.com/intent/tweet?text=";
                String txt = "";
                txt += "Check out ";
                if (Detail.getInstance().getName().length() > 0)
                    txt += Detail.getInstance().getName();
                if (Detail.getInstance().getAddress().length() > 0)
                    txt += " located at " + Detail.getInstance().getAddress();
                if (Detail.getInstance().getUrl().length() > 0 || Detail.getInstance().getWebsite().length() > 0 )
                    txt += ". Website: " ;
                url += txt;
                if (Detail.getInstance().getWebsite().length() > 0)
                    url += "&url="+Detail.getInstance().getWebsite();
                else if (Detail.getInstance().getUrl().length() > 0)
                    url += "&url="+Detail.getInstance().getUrl();
                url += "&hashtags=TravelAndEntertainmentSearch";


                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse(url));
                startActivity(intent);

                return true;

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }


}
