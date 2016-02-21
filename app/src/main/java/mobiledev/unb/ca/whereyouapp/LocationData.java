package mobiledev.unb.ca.whereyouapp;

import android.location.Location;

/**
 * Created by brionon 16-02-20.
 * Contains data pertaining to locations found through google play
 */
public class LocationData {

    public String id;
    public String name;
    public double lat;
    public double lng;

    public LocationData(String id, String name, double lat, double lng){
        this.id = id;
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public String toString(){
        return id + "\n" + name;
    }

}
