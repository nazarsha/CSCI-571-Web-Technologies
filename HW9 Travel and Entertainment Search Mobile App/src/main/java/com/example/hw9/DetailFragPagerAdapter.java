package com.example.hw9;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ImageSpan;

public class DetailFragPagerAdapter extends FragmentPagerAdapter {
    private static int NUM_ITEMS = 4;
    private Context mContext;

    public DetailFragPagerAdapter(FragmentManager fragmentManager, Context m) {
        super(fragmentManager);
        mContext = m;
    }

    // Returns total number of pages.
    @Override
    public int getCount() {
        return NUM_ITEMS;
    }

    // Returns the fragment to display for a particular page.
    @Override
    public Fragment getItem(int position) {
/*        switch (position) {
            case 0:
                return InfoFragment.newInstance("Fragment 1", R.layout.fragment_info);
            case 1:
                return InfoFragment.newInstance("Fragment 2", R.drawable.android_2);
            case 2:
                return InfoFragment.newInstance("Fragment 3", R.drawable.android_3, R.drawable.android_4);
            default:
                return null;
        } */

        switch (position) {
            case 0:
                return InfoFragment.newInstance();
            case 1:
                return PhotosFragment.newInstance(mContext);
            case 2:
                return MapViewFragment.newInstance(mContext);
            case 3:
                return ReviewsFragment.newInstance(mContext);

            default:
                return InfoFragment.newInstance();

        }

    }

    // Returns the page title for the top indicator
    @Override
    public CharSequence getPageTitle(int position) {
        SpannableStringBuilder sb;
        Drawable drawable;
        ImageSpan span;

        switch (position) {
            case 0:
                sb = new SpannableStringBuilder("  INFO"); // space added before text for convenience

                drawable = mContext.getResources().getDrawable( R.drawable.info_outline );
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                return sb;//R.drawable.info_outline + "Info" ;
            case 1:
                SpannableStringBuilder sbp = new SpannableStringBuilder("  PHOTOS"); // space added before text for convenience

                Drawable drawableP = mContext.getResources().getDrawable( R.drawable.photos );
                drawableP.setBounds(0, 0, drawableP.getIntrinsicWidth(), drawableP.getIntrinsicHeight());
                ImageSpan spanP = new ImageSpan(drawableP, ImageSpan.ALIGN_BOTTOM);
                sbp.setSpan(spanP, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                return sbp;//R.drawable.info_outline + "Info" ;
            case 3:
                sb = new SpannableStringBuilder("  REVIEWS"); // space added before text for convenience

                drawable = mContext.getResources().getDrawable( R.drawable.review );
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                return sb;//R.drawable.info_outline + "Info" ;

            case 2:
                sb = new SpannableStringBuilder("  MAP"); // space added before text for convenience

                drawable = mContext.getResources().getDrawable( R.drawable.maps );
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                span = new ImageSpan(drawable, ImageSpan.ALIGN_BOTTOM);
                sb.setSpan(span, 0, 1, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

                return sb;//R.drawable.info_outline + "Info" ;

            default:
                return "Tab " + position;

        }
    }

}