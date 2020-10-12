package com.example.hw9;

public class Place {
    private String placeId;
    private String icon;
    private float lat, lon;
    private String name;
    private String address;

    //used for autocomplete activities
    private String placeText;

    public String getPlaceId() {
        return placeId;
    }
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public String getPlaceName() {
        return name;
    }
    public void setPlaceName(String name ) {
        this.name  = name ;
    }

    public String getPlaceAddress() {
        return address;
    }
    public void setPlaceAddress(String add ) {
        this.address  = add ;
    }


    public String getPlaceIcon() {
        return icon;
    }
    public void setPlaceIcon(String icon) {
        this.icon = icon;
    }

    public float getPlaceLat() {
        return this.lat;
    }
    public void setPlaceLat(String lat) { this.lat = Float.parseFloat(lat); }


    public float getPlaceLon() {
        return this.lon;
    }
    public void setPlaceLon(String lon) {  this.lon = Float.parseFloat(lon); }

    public String getPlaceText() {
        return placeText;
    }

    public void setPlaceText(String placeText) {
        this.placeText = placeText;
    }

    /*public String toString(){
        return placeId + " " + name + " " + address + " " + lat + "," + lon ;
    }*/
    public String toString(){
        if (placeText == null){
            return placeId + " " + name + " " + address + " " + lat + "," + lon ;
        }
        else if (placeText.length() == 0) {
            return placeId + " " + name + " " + address + " " + lat + "," + lon ;
        }

        return placeText.toString();
    }

}