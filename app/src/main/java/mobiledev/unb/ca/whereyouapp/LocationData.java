package mobiledev.unb.ca.whereyouapp;


/**
 * Created by brionon 16-02-20.
 * Contains data pertaining to locations found through google play
 */
public class LocationData {


    private String id;
    private String name;
    private double lat;
    private double lng;
    private long count;

    public LocationData(String name, double lat, double lng, long peopleCount){
        this.name = name;
        this.lat = lat;
        this.lng = lng;
        this.count = peopleCount;
    }

    public LocationData(String name, double lat, double lng){
        this.name = name;
        this.lat = lat;
        this.lng = lng;
    }

    public String toString(){
        return name + "\nLatitude: " + lat + "\nLongitude: " + lng;
    }

    public String getName(){
        return name;
    }

    public double getLat(){
        return lat;
    }
    public double getLng(){
        return lng;
    }

    public String getId(){
        return id;
    }

    public long getCount(){
        return count;
    }

    public void setId(String id){
        this.id = id;
    }

}
