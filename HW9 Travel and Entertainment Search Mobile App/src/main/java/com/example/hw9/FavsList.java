package com.example.hw9;

import java.util.ArrayList;

public class FavsList {

    private static FavsList instance;

    public static FavsList getInstance() {
        if (instance == null)
            instance = new FavsList();
        return instance;
    }

    private FavsList() {
        favs = new ArrayList<Place>();
    }

    private ArrayList<Place> favs;

    public ArrayList<Place> getFavs() {
        return favs;
    }


    public  Integer getSize() {
        return  this.favs.size();
    }

    public  void addFav (Place f) {
        if (!isFav(f.getPlaceId())){
            this.favs.add(f);
            System.out.println("Added item to favs" + f.getPlaceId());
            System.out.println("Added item to favs" + this.favs.size());
        } else {
            System.out.println("Cant add fav item" + f.getPlaceId());
        }
    }

    public void removeFav (String ID) {
        boolean removed = false;
       for (Integer i=0; i < favs.size(); i++){
           if (favs.get(i).getPlaceId().trim().equals(ID) ){
               favs.remove(favs.get(i));
               removed = true;
           }
       }
        if (removed == false)
        {
            System.out.println("Cant remove fav item" + ID);
        }
    }


    public Boolean isFav (String ID){
        for (Integer i=0; i < favs.size(); i++){
            if (favs.get(i).getPlaceId().trim().equals(ID)){
                return true;
            }
        }

        return false;
    }


    public  String  toString(){
        String out= "";
        for (Integer i=0; i < favs.size(); i++) {
            out += "i " + favs.get(i) + "\n";
        }
         return  out;
    }

}


