package mobiledev.unb.ca.whereyouapp;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

public class WebServiceTest extends AppCompatActivity {

    private String testOutput;
    private TextView mResultText;
    private Button mButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_web_service_test);
        mResultText = (TextView) findViewById(R.id.tvQueryResult);
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
        String testQuery = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?location=-33.8670522,151.1957362&radius=500&type=restaurant&key=" + "AIzaSyC_TQ_NRLl5v4cNFdxtV0oMuo068i-5ORU";

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

        @Override
        protected void onPostExecute(String result){
            mResultText.setText(result);
        }
    }
}
