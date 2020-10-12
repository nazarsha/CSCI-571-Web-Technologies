package com.example.hw9;

import java.util.ArrayList;

public class placeList {

    private static placeList instance;

    public static placeList getInstance() {
        if (instance == null)
            instance = new placeList();
        return instance;
    }

    private ArrayList<Place> pls;
    private placeList () {
        pls= new ArrayList<Place>();
    }


    public void setPlaceList(ArrayList<Place> p){
        pls = p;
    }


    public ArrayList<Place> getPlaceList(){
        return new ArrayList<Place>( this.pls);
    }

    public void clearPlaces(){
        this.pls = new ArrayList<Place>();
    }

    public void removePlace(String ID){
        boolean removed = false;
        for (Integer i=0; i < pls.size(); i++){
            if (pls.get(i).getPlaceId().trim().equals(ID) ){
                pls.remove(pls.get(i));
                removed = true;
            }
        }
        if (!removed)
        {
            System.out.println("Cant remove fav item" + ID);
        }


    }

}
