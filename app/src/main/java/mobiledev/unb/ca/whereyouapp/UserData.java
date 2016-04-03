package mobiledev.unb.ca.whereyouapp;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

/**
 * Created by userone on 16-03-19.
 */
public class UserData {

    private double lat;
    private double lng;
    private String email;

    public UserData(){};

    public UserData(String email, double lat, double lng){
        this.email = email;
        this.lat = lat;
        this.lng = lng;
    }

    public double getLat(){ return lat; }

    public double getLng(){
        return lng;
    }

    public String getEmail() {return email;}

}
