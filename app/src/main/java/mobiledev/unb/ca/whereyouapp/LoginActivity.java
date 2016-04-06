package mobiledev.unb.ca.whereyouapp;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.app.LoaderManager.LoaderCallbacks;

import android.content.CursorLoader;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;

import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.ResultCallbacks;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.Geofence;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.PACKAGE_USAGE_STATS;
import static android.Manifest.permission.READ_CONTACTS;
import static mobiledev.unb.ca.whereyouapp.Constants.GEOFENCE_EXPIRATION_TIME;

import com.firebase.client.AuthData;
import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.xml.transform.Result;

/**
 * A login screen that offers login via email/password.
 */

public class LoginActivity extends AppCompatActivity
    implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        ResultCallback<Status>{

    private UserLoginTask mAuthTask = null;
    private final static String TAG = "LOGIN -";
    private AuthData mAuth;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation = null;
    private PendingIntent geoIntent;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;

    private Firebase ref;
    private ArrayList<LocationData> mNearbyLocations;

    @Override
    public void onResult(Status status) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Firebase.setAndroidContext(this);
        ref = new Firebase(getResources().getString(R.string.firebaseUrl));
        // Set up the login form.
        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);

        mPasswordView = (EditText) findViewById(R.id.password);

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        Button mEmailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        mEmailSignInButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                attemptLogin();
            }
        });

        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);
    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid email, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.
            showProgress(true);
            mAuthTask = new UserLoginTask(email, password);
            mAuthTask.execute((Void) null);
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mLoginFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    /**
     * Represents an asynchronous login/registration task used to authenticate
     * the user.
     */
    public class UserLoginTask extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private boolean mCreationError = false;
        private AuthData mAuthData = null;

        UserLoginTask(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                return firebaseLogin();
            } catch (Exception e) {
                Log.e(TAG, e.getStackTrace().toString());
                return false;
            }
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            showProgress(false);
        }

        public boolean firebaseLogin(){
            ref.authWithPassword(mEmail, mPassword, new Firebase.AuthResultHandler() {

                @Override
                public void onAuthenticated(AuthData authData) {
                    Log.i("AUTH", "User ID: " + authData.getUid() + ", Provider: " + authData.getProvider());
                    mAuthData = authData;
                    SharedPreferences.Editor edit = getSharedPreferences("userInfo",0).edit();
                    edit.putString("uid", authData.getUid());
                    edit.putString("email", mEmail);
                    edit.apply();
                    Firebase user = ref.child("users/" + authData.getUid());
                    user.child("email").setValue(mEmail);
                    user.child("lat").setValue(mLastLocation.getLatitude());
                    user.child("lng").setValue(mLastLocation.getLongitude());
                    Log.i("FBLOG", "location saved");

                    showProgress(false);
                    startMainActivity();
                }

                @Override
                public void onAuthenticationError(FirebaseError firebaseError) {
                    if (firebaseError.getCode() != FirebaseError.EMAIL_TAKEN && firebaseError.getCode() != FirebaseError.INVALID_PASSWORD && !mCreationError) {
                        createFirebaseUser();
                    } else {
                        Toast.makeText(LoginActivity.this, "Auth error " + firebaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        showProgress(false);
                    }
                }
            });

            return true;
        }

        public void createFirebaseUser(){
            ref.createUser(mEmail, mPassword, new Firebase.ValueResultHandler<Map<String, Object>>() {
                @Override
                public void onSuccess(Map<String, Object> result) {
                    System.out.println("Successfully created user account with uid: " + result.get("uid"));
                    firebaseLogin();
                    Toast.makeText(LoginActivity.this, "New account created successfully", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onError(FirebaseError firebaseError) {
                    Log.e("FIREBASE", "error creating new user");
                    Toast.makeText(LoginActivity.this, "Error creating account, try a valid email", Toast.LENGTH_SHORT).show();
                    mCreationError = true;
                    showProgress(false);
                }
            });
        }
    }

    public void startMainActivity(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        this.finish();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // We are not connected anymore!
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        int i = 0;
    }

    @Override
    public void onConnected(Bundle connectionHint) {
        int permissionCheck = ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            new GooglePlacesWrapper(mLastLocation.getLatitude(), mLastLocation.getLongitude()).execute();
        }
    }

    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }

    public class GooglePlacesWrapper extends AsyncTask<Void, Void, ArrayList<String>> {

        private final static String TAG = "PLACESTEST";
        String mNearbySearchURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?type=bar";
        String mQueryJSON;

        public GooglePlacesWrapper(double lat,double lng) {
            mNearbySearchURL += "&location=" + Double.toString(lat) + ",";
            mNearbySearchURL += Double.toString(lng);
            mNearbySearchURL += "&radius=5000&key=" + getResources().getString(R.string.google_places_key);
        }

        @Override
        protected ArrayList<String> doInBackground(Void... params) {
            String data = "";
            HttpURLConnection httpUrlConnection = null;
            ArrayList<String> responses = new ArrayList<String>();
            String temp = mNearbySearchURL;
            InputStream in = null;
            HttpURLConnection conn = null;

            do {
                try {
                    conn = (HttpURLConnection) new URL(temp).openConnection();

                    in = new BufferedInputStream(conn.getInputStream());
                    data = readStream(in);
                    responses.add(data);
                    JSONObject response = new JSONObject(data);
                    if(data.contains("next_page_token"))
                        temp = mNearbySearchURL += "&pagetoken=" + response.getString("next_page_token");
                } catch (JSONException e){
                    Log.i(TAG, e.toString());
                    temp = "";
                } catch (MalformedURLException exception) {
                    Log.e(TAG, "MalformedURLException");
                } catch (IOException exception) {
                    Log.e(TAG, "IOException");
                } finally {
                    if (null != conn)
                        conn.disconnect();
                }
            } while(data.contains("next_page_token"));

            return responses;
        }

        private String readStream(InputStream in) {
            BufferedReader reader = null;
            StringBuilder data = new StringBuilder("");
            try {
                reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    data.append(line);
                }
            } catch (IOException e) {
                Log.e(TAG, "IOException");
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return data.toString();
        }

        private ArrayList<LocationData> parseToLocationObjects(ArrayList<String> responses){
            final ArrayList<LocationData> arr = new ArrayList<>();

            for(String data : responses) {
                try {
                    JSONObject response = new JSONObject(data);
                    JSONArray locationsResults = response.getJSONArray("results");

                    for (int i = 0; i < locationsResults.length(); i++) {
                        JSONObject results = locationsResults.getJSONObject(i);
                        JSONObject geo = results.getJSONObject("geometry");
                        JSONObject location = geo.getJSONObject("location");
                        final String placeID = results.getString("place_id");
                        final String name = results.getString("name");
                        final Double lat = location.getDouble("lat");
                        final Double lng = location.getDouble("lng");
                        LocationData loc = new LocationData(name, lat, lng);
                        loc.setId(placeID);
                        arr.add(loc);
                        ref.child("/locations/" + placeID).addListenerForSingleValueEvent(new ValueEventListener() {

                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if (dataSnapshot.getValue() == null) {
                                    LocationData loc = new LocationData(name, lat, lng);
                                    ref.child("/locations/" + placeID).setValue(loc);
                                }
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                Log.i(TAG, firebaseError.getDetails());
                            }
                        });
                    }
                } catch (JSONException e) {
                    Log.i(TAG, e.toString());
                }
            }
            return arr;
        }

        @Override
        protected void onPostExecute(ArrayList<String> responses){
            mNearbyLocations = parseToLocationObjects(responses);
            GeofencingRequest.Builder builder = new GeofencingRequest.Builder();

            ArrayList<Geofence> fences = new ArrayList<Geofence>();

            for(LocationData location : mNearbyLocations){

                fences.add(
                        new SimpleGeofence(
                                location.getId(),
                                location.getLat(),
                                location.getLng()
                        ).toGeofence()
                );
                Log.i("FENCE", "Added " + location.getName());
            }

            builder.setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER);
            builder.addGeofences(fences);

            GeofencingRequest req = builder.build();

            Intent intent = new Intent(LoginActivity.this, GeofenceTransitionsReceiver.class);
            intent.setAction("geofence_transition_action");
            geoIntent = PendingIntent.getBroadcast(LoginActivity.this, R.id.geofence_transition_intent, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            int permissionCheck = ContextCompat.checkSelfPermission(LoginActivity.this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            if(permissionCheck == PackageManager.PERMISSION_GRANTED) {
                LocationServices.GeofencingApi.addGeofences(
                        mGoogleApiClient,
                        req,
                        geoIntent
                ).setResultCallback(LoginActivity.this);
            }
        }
    }
}

