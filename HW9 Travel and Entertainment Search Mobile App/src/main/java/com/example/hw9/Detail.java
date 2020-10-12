package com.example.hw9;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Detail  {

    private static Detail instance;

    public static Detail getInstance() {
        if (instance == null)
            instance = new Detail();
        return instance;
    }



    private String placeId;
    private float lat, lon;
    private String name;
    private String address;
    private String phone;
    private String website;
    private String rating;
    private String url;
    private JSONArray google_reviews;
    private JSONArray yelp_reviews;
    private ArrayList<String > photos;
    private String price;
    private JSONArray hours;
    private Boolean open_now;
    private JSONArray periods;
    private String utc_offset;


    private Place curPlaceObj;

    private Detail () {}

    public void addDetail(JSONArray curDet){
        instance = new Detail(curDet);
    }

    private Detail (JSONArray curDet) {
        try {
            JSONObject det = curDet.getJSONObject(0);
            placeId = det.getString("id");
            try { name = det.getString("name");
            } catch (Exception e){  name = ""; }

            try { address = det.getString("address");
            } catch (Exception e){ address = ""; }
            try { phone = det.getString("phone");
            } catch (Exception e) { phone = ""; }

            try { lat = Float.parseFloat(det.getString("lat"));
            } catch ( Exception e) { lat = (float)(0.0); }

            try { lon = Float.parseFloat(det.getString("lon"));
            } catch ( Exception e) { lon = (float)(0.0);}

            try { website = det.getString("website");
            } catch ( Exception e) {website = "";}

            try { url = det.getString("url"); }
            catch (Exception e ) { url = ""; }

            try { rating = det.getString("rating"); }
            catch (Exception e ) {rating = "";}

            try {  google_reviews = det.getJSONArray("google_reviews");
            } catch (JSONException e) { google_reviews = new JSONArray();}

            try {            yelp_reviews = det.getJSONArray("yelp_reviews"); }
            catch (Exception e) {yelp_reviews = new JSONArray();}
            //photos =
            try {            price = det.getString("price"); }
            catch (Exception e) {price = "";}

            try { hours = det.getJSONArray("hours");}
            catch (Exception e) {hours = new JSONArray();}

            try {            open_now = det.getBoolean("open_now"); }
            catch ( Exception e ) { open_now = new Boolean(null);}

            try {            periods = det.getJSONArray("periods"); }
            catch (Exception e) {periods = new JSONArray();}

            try { utc_offset = det.getString("utc_offset"); }
            catch (Exception e) {utc_offset = "";}




        } catch (JSONException e) {
            e.printStackTrace();
            System.out.println("Can't instantiate detail item");
        }



    }

    public void setPhotos (ArrayList<String> urls) {
        this.photos = urls;
    }

    public void setPlaceObj (Place p) {
        this.curPlaceObj = p;
    }

    public Place getPlaceObj (){
        return this.curPlaceObj;
    }

    public String getAddress() {
        return address;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    public JSONArray getPeriods() {
        return periods;
    }

    public Boolean getOpen_now() {
        return open_now;
    }

    public JSONArray getGoogle_reviews() {
        return google_reviews;
    }

    public JSONArray getHours() {
        return hours;
    }


    public String getName() {
        return name;
    }


    public String getPhone() {
        return phone;
    }

    public String getPlaceId() {
        return placeId;
    }


    public String getPrice() {
        return price;
    }


    public String getRating() {
        return rating;
    }

    public String getUrl() {
        return url;
    }

    public String getUtc_offset() {
        return utc_offset;
    }


    public String getWebsite() {
        return website;
    }


    public JSONArray getYelp_reviews() {
        return yelp_reviews;
    }

    public String toString(){
        return placeId + " " + name + " " + address + " " + lat + "," + lon + " " + url + " " + website ;
    }



}
