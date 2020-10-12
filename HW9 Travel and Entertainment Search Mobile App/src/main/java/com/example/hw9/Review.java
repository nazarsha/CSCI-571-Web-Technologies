package com.example.hw9;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class Review {


    private String name;
    private String picture;
    private String text;
    private Float rating;
    private String time;
    private String url;


    public Review(JSONObject revs){
        try {
            name = revs.getString("auth_name");
        } catch (Exception e) {name = "";}

        try {
            picture = revs.getString("pic");
        } catch ( Exception e) { picture = "";}

        try {
            text = revs.getString("text");
        } catch (Exception e) { text = "";}

        try{
            rating = Float.parseFloat(revs.getString("rating"));
        } catch (Exception e) {rating = new Float(0.0) ;}

        try {
            time = revs.getString("time");
        } catch (Exception e) { time = "";}

        try {
            url = revs.getString("url");
        } catch (Exception e) {url = "";}
    }


    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getPicture() {
        return picture;
    }

    public String getText() {
        return text;
    }

    public String getTime() {
        return time;
    }

    public String getTimeFormatted (){
        try {
            Date date = new Date(Long.parseLong(getTime()) * 1000L);
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.ENGLISH);
            sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
            String formattedDate = sdf.format(date);
            System.out.println(formattedDate); // Tuesday,November 1,2011 12:00,AM
            return formattedDate;
        } catch (Exception e){
            return this.getTime();
        }
    }

    public Float getRating() {
        return rating;
    }

    public static Comparator<Review> reviewTimeComp
            = new Comparator<Review>() {

        public int compare(Review r1, Review r2) {

            try {

                return Integer.parseInt(r1.getTime()) - Integer.parseInt(r2.getTime());
            } catch (Exception e) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                try {
                    Date date1 = sdf.parse(r1.getTime());
                    Date date2 = sdf.parse(r2.getTime());
                    return date1.compareTo(date2) ;

                } catch (ParseException e1) {
                    e1.printStackTrace();
                    return -1;
                }

            }

        }

    };

    public static Comparator<Review> reviewRating
            = new Comparator<Review>() {

        public int compare(Review r1, Review r2) {

            float rt = new Float (r1.getRating() - r2.getRating());
            return (int) rt;

        }

    };


}
