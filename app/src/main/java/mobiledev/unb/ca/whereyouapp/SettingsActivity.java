package mobiledev.unb.ca.whereyouapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Spinner;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    Firebase settingsRef;
    private Boolean val1 = true;
    Menu menu;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Spinner dropdown = (Spinner)findViewById(R.id.spinner1);
        String[] items = new String[]{"On", "Off"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, items);
        dropdown.setAdapter(adapter);
        SharedPreferences pref = getSharedPreferences("userInfo", 0);
        settingsRef = new Firebase(getResources().getString(R.string.firebaseUrl) + "/users/" + pref.getString("uid", "")).child("shareLocation");

        settingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue() == null || !(Boolean) dataSnapshot.getValue()){
                    dropdown.setSelection(1);
                } else {
                    dropdown.setSelection(0);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.i("", firebaseError.getDetails());
            }
        });

        dropdown.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = dropdown.getSelectedItem().toString();
                if (value.equals("On"))
                    settingsRef.setValue(true);
                else
                    settingsRef.setValue(false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        
        final ImageButton tab1 = (ImageButton) findViewById(R.id.mapTab);
        final ImageButton tab2 = (ImageButton) findViewById(R.id.friendTab);

        tab1.setOnClickListener
                (new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(SettingsActivity.this, MapActivity.class);
                        startActivity(intent);

                    }
                });
        tab2.setOnClickListener
                (new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(SettingsActivity.this, FriendActivity.class);
                        startActivity(intent);

                    }
                });


//       FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
//                startActivity(intent);
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_settings, menu);
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




}
