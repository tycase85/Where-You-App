package mobiledev.unb.ca.whereyouapp;

import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;

public class PlayWithFirebase extends AppCompatActivity {

    EditText mLocationNameTxt;
    EditText mLatTxt;
    EditText mLngTxt;
    EditText mCountTxt;
    Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_with_firebase);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        mLocationNameTxt = (EditText) findViewById(R.id.txtLocationName);
        mLatTxt = (EditText) findViewById(R.id.txtLatitude);
        mLngTxt = (EditText) findViewById(R.id.txtLongitude);
        mCountTxt = (EditText) findViewById(R.id.txtCount);

        Firebase.setAndroidContext(this);
        ref = new Firebase(getResources().getString(R.string.firebaseUrl) + "/locations");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAndUpdateFirebase();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_play_with_firebase, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void addAndUpdateFirebase(){
        String name = mLocationNameTxt.getText().toString();
        double a = getDouble(mLatTxt);
        double b = getDouble(mLngTxt);
        long c = getLong(mCountTxt);

        LocationData ld = new LocationData(name, a, b, c);
        ref.push().setValue(ld, new Firebase.CompletionListener() {
            @Override
            public void onComplete(FirebaseError firebaseError, Firebase firebase) {
                if (firebaseError != null) {
                    Toast.makeText(PlayWithFirebase.this, "Data could not be saved. " + firebaseError.getMessage(), Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(PlayWithFirebase.this, "Saved Location!. ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private double getDouble(EditText text){
        return Double.parseDouble(text.getText().toString());
    }

    private long getLong(EditText text){
        return Long.parseLong(text.getText().toString());
    }

}
