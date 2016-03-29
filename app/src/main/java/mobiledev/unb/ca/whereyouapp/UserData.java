package mobiledev.unb.ca.whereyouapp;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by userone on 16-03-19.
 */
public class UserData {

    private Location location;
    private double lat;
    private double lng;

    public UserData(){};

    public UserData(Location location){
        this.location = location;
        lat = location.getLatitude();
        lng = location.getLongitude();
    }

    public void setLocation(Location location){this.location = location;}

    public double getLat(){
        return lat;
    }

    public double getLng(){
        return lng;
    }

}
