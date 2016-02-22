package mobiledev.unb.ca.whereyouapp;

import android.content.Context;
import android.location.Location;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class WebServiceTest extends AppCompatActivity {

    private String testOutput;
    private ListView mResultsList;
    private Button mButton;
    private Location currentLocation;
    private static final String ID = "id";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service_test);
        mResultsList = (ListView) findViewById(R.id.lvPlaces);
        mButton = (Button) findViewById(R.id.qpButton);

        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                getInfoFromPlaces();
            }
        });
    }

    public void getInfoFromPlaces(){

        GooglePlacesWrapper gw = new GooglePlacesWrapper();

        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);

        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            gw.execute();
        } else {
            Toast.makeText(this,
                    "No Network",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public class GooglePlacesWrapper extends AsyncTask<Void, Void, String> {

        private final static String TAG = "PLACESTEST";
        String mNearbySearchURL = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?";
        String mQueryJSON;
        String testQuery = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=45.9453764,-66.8066567&radius=5000&type=restaurant&key=AIzaSyC_TQ_NRLl5v4cNFdxtV0oMuo068i-5ORU";

        public GooglePlacesWrapper() {
            //will later build queries etc, but for now nothing needed
        }

        @Override
        protected String doInBackground(Void... params) {
            String data = "";
            HttpURLConnection httpUrlConnection = null;

            try {
                httpUrlConnection = (HttpURLConnection) new URL(testQuery)
                        .openConnection();

                InputStream in = new BufferedInputStream(
                        httpUrlConnection.getInputStream());

                data = readStream(in);

            } catch (MalformedURLException exception) {
                Log.e(TAG, "MalformedURLException");
            } catch (IOException exception) {
                Log.e(TAG, "IOException");
            } finally {
                if (null != httpUrlConnection)
                    httpUrlConnection.disconnect();
            }
            return data;
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

        private ArrayList<LocationData> parseToLocationObjects(String data){
            ArrayList<LocationData> arr = new ArrayList<LocationData>();

            try{
                JSONObject response = new JSONObject(data);
                JSONArray locationsResults = response.getJSONArray("results");

                for(int i = 0; i < locationsResults.length(); i++){
                    JSONObject location = locationsResults.getJSONObject(i);
                    String name = location.getString("name");
                    JSONObject geo = location.getJSONObject("geometry");
                    location = geo.getJSONObject("location");
                    Double lat = location.getDouble("lat");
                    Double lng = location.getDouble("lng");

                    arr.add(new LocationData(name, lat, lng));
                }
            } catch (JSONException e){
                Log.i(TAG, e.toString());
            }

            return arr;
        }

        @Override
        protected void onPostExecute(String data){
            ArrayList<LocationData> arr = parseToLocationObjects(data);

            ArrayAdapter<LocationData> adapter = new ArrayAdapter<LocationData>(
                            WebServiceTest.this,
                            android.R.layout.simple_list_item_1,
                            arr);

            mResultsList.setAdapter(adapter);
        }
    }
}
