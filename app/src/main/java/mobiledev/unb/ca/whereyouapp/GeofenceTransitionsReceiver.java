package mobiledev.unb.ca.whereyouapp;

/*
 * Copyright (C) 2014 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

        import static mobiledev.unb.ca.whereyouapp.Constants.TAG;

        import android.app.IntentService;
        import android.content.Context;
        import android.content.Intent;
        import android.os.Bundle;
        import android.os.Handler;
        import android.os.Looper;
        import android.support.v4.content.WakefulBroadcastReceiver;
        import android.util.Log;
        import android.widget.Toast;

        import com.firebase.client.DataSnapshot;
        import com.firebase.client.Firebase;
        import com.firebase.client.FirebaseError;
        import com.firebase.client.Query;
        import com.firebase.client.ValueEventListener;
        import com.google.android.gms.common.ConnectionResult;
        import com.google.android.gms.common.api.GoogleApiClient;
        import com.google.android.gms.location.Geofence;
        import com.google.android.gms.location.GeofencingEvent;
        import com.google.android.gms.wearable.PutDataMapRequest;
        import com.google.android.gms.wearable.Wearable;

        import java.util.ArrayList;
        import java.util.List;
        import java.util.concurrent.TimeUnit;

/**
 * Listens for geofence transition changes.
 */
public class GeofenceTransitionsReceiver extends WakefulBroadcastReceiver {

    Context context;

    private GoogleApiClient mGoogleApiClient;
    final static String TAG = "TRANSITION";

    public GeofenceTransitionsReceiver() {
        Log.i(TAG, "Constructed");
    }

    /**
     * Handles incoming intents.
     * @param intent The Intent sent by Location Services. This Intent is provided to Location
     * Services (inside a PendingIntent) when addGeofences() is called.
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "Received");
        this.context = context;
        final GeofencingEvent geoFenceEvent = GeofencingEvent.fromIntent(intent);
        final List<Geofence> fences = geoFenceEvent.getTriggeringGeofences();
        final ArrayList<String> ids = new ArrayList<>();
        final ArrayList<Firebase> locations = new ArrayList<>();

        if (geoFenceEvent.hasError()) {
            int errorCode = geoFenceEvent.getErrorCode();
            Log.e(TAG, "Location Services error: " + errorCode);
        } else {
            final int transitionType = geoFenceEvent.getGeofenceTransition();

            if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {
                Log.i(TAG, "Enter");
            } else if (Geofence.GEOFENCE_TRANSITION_EXIT == transitionType) {
                Log.i(TAG, "Exit");
            }

            for(Geofence geofence : fences)
                locations.add(new Firebase(context.getResources().getString(R.string.firebaseUrl) + "/locations/" + geofence.getRequestId()));

            for(final Firebase firebase : locations) {
                firebase.addListenerForSingleValueEvent(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        long count = (long) snapshot.child("count").getValue();

                        if (Geofence.GEOFENCE_TRANSITION_ENTER == transitionType) {
                            Log.i(TAG, "Enter");
                            firebase.child("count").setValue(count + 1);
                        } else if (Geofence.GEOFENCE_TRANSITION_EXIT == transitionType) {
                            Log.i(TAG, "Exit");
                            firebase.child("count").setValue(count - 1);
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {

                    }
                });
            }
        }
    }

    /**
     * Showing a toast message, using the Main thread
     */
    private void showToast(final Context context, final String message) {
        Handler mainThread = new Handler(Looper.getMainLooper());
        mainThread.post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_LONG).show();
            }
        });
    }

}