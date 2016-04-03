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


        import android.net.Uri;

        import com.google.android.gms.location.Geofence;

/** Constants used in companion app. */
public final class Constants {

    private Constants() {
    }

    public static final String TAG = "GeoConstants";

    // Request code to attempt to resolve Google Play services connection failures.
    public final static int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;
    // Timeout for making a connection to GoogleApiClient (in milliseconds).
    public static final long CONNECTION_TIME_OUT_MS = 100;
    public static final long GEOFENCE_RADIUS = 150;

    // For the purposes of this demo, the geofences are hard-coded and should not expire.
    // An app with dynamically-created geofences would want to include a reasonable expiration time.
    public static final long GEOFENCE_EXPIRATION_TIME = Geofence.NEVER_EXPIRE;

    // Path for the DataItem containing the last geofence id entered.
    public static final String GEOFENCE_DATA_ITEM_PATH = "/geofenceid";
    public static final String KEY_GEOFENCE_ID = "geofence_id";

    // Keys for flattened geofences stored in SharedPreferences.
    public static final String KEY_LATITUDE = "mobiledev.unb.ca.whereyouapp.KEY_LATITUDE";
    public static final String KEY_LONGITUDE = "mobiledev.unb.ca.whereyouapp.KEY_LONGITUDE";
    public static final String KEY_RADIUS = "mobiledev.unb.ca.whereyouapp.KEY_RADIUS";
    public static final String KEY_EXPIRATION_DURATION =
            "mobiledev.unb.ca.whereyouapp.KEY_EXPIRATION_DURATION";
    public static final String KEY_TRANSITION_TYPE =
            "mobiledev.unb.ca.whereyouapp.KEY_TRANSITION_TYPE";
    // The prefix for flattened geofence keys.
    public static final String KEY_PREFIX = "com.example.wearable.geofencing.KEY";

    // Invalid values, used to test geofence storage when retrieving geofences.
    public static final long INVALID_LONG_VALUE = -999l;
    public static final float INVALID_FLOAT_VALUE = -999.0f;
    public static final int INVALID_INT_VALUE = -999;

}