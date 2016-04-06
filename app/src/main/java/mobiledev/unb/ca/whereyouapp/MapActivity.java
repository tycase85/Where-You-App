package mobiledev.unb.ca.whereyouapp;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Looper;
import android.provider.ContactsContract;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import static mobiledev.unb.ca.whereyouapp.Constants.GEOFENCE_EXPIRATION_TIME;

public class MapActivity extends FragmentActivity
        implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleMap mMap;
    private Marker yourMarker;
    private HashMap<String, Marker> locationMarkers = new HashMap<>();
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest locationRequest;
    private LocationListener locationListener;
    private Location mLastLocation;
    private String testOutput = "";
    private Firebase ref;
    private ArrayList<LocationData> fbLocations;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fbLocations = new ArrayList<LocationData>();
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Firebase.setAndroidContext(this);
        ref = new Firebase(getResources().getString(R.string.firebaseUrl) + "/locations");

        setFirebaseListeners(ref);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

    }

    @Override
    public void onConnected(Bundle connectionHint) {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }

        if(mMap != null){
            moveToCurrentLocation();
        }
    }

    public void moveToCurrentLocation(){
        LatLng current = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
        if(yourMarker == null)
            yourMarker = mMap.addMarker(new MarkerOptions().position(current).title("You!").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_MAGENTA)));
        else
            yourMarker.setPosition(current);
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(current, 15));
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to ins`ll
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
    }

    protected void onStart() {
        mGoogleApiClient.connect();

        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // We are not connected anymore!
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // We tried to connect but failed!
    }

    private void setFirebaseListeners(Firebase ref){
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String str) {
                String key = (String) snapshot.getKey();
                String name = (String) snapshot.child("name").getValue();
                double lat = (double) snapshot.child("lat").getValue();
                double lng = (double) snapshot.child("lng").getValue();
                long count = (long) snapshot.child("count").getValue();

                LocationData lc = new LocationData(name, lat, lng, count);
                fbLocations.add(lc);
                LatLng pos = new LatLng(lat, lng);
                float hue = BitmapDescriptorFactory.HUE_CYAN;
                if(count > 30 && count < 60)
                    hue = BitmapDescriptorFactory.HUE_YELLOW;
                else if(count > 60 && count < 90){
                    hue = BitmapDescriptorFactory.HUE_ORANGE;
                } else if(count >= 90)
                    hue = BitmapDescriptorFactory.HUE_RED;

                if(locationMarkers.containsKey(key)) {
                    Marker marker = locationMarkers.get(key);
                    marker.setPosition(pos);
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(hue));
                } else {
                    Marker marker = mMap.addMarker(new MarkerOptions().position(pos).title(name).icon(BitmapDescriptorFactory.defaultMarker(hue)).snippet("People: " + count));
                    locationMarkers.put(key, marker);
                    marker.showInfoWindow();
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {

            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String str){
                String key = (String) snapshot.getKey();
                String name = (String) snapshot.child("name").getValue();
                double lat = (double) snapshot.child("lat").getValue();
                double lng = (double) snapshot.child("lng").getValue();
                long count = (long) snapshot.child("count").getValue();

                LocationData lc = new LocationData(name, lat, lng, count);
                fbLocations.add(lc);
                LatLng pos = new LatLng(lat, lng);
                float hue = BitmapDescriptorFactory.HUE_CYAN;
                if(count > 30 && count < 60)
                    hue = BitmapDescriptorFactory.HUE_YELLOW;
                else if(count > 60 && count < 90){
                    hue = BitmapDescriptorFactory.HUE_ORANGE;
                } else if(count >= 90)
                    hue = BitmapDescriptorFactory.HUE_RED;

                if(locationMarkers.containsKey(key)) {
                    Boolean shown = yourMarker.isInfoWindowShown();
                    Marker marker = locationMarkers.get(key);
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(hue));
                    marker.setSnippet("People: " + count);
                    if(shown)
                        marker.showInfoWindow();
                }
            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String str){

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

}
