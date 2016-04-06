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
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.ToggleButton;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {
    Firebase settingsRef;
    Switch mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mToggle = (Switch) findViewById(R.id.locationSwitch);
        Boolean checked = getSharedPreferences("userInfo", 0).getBoolean("shareLocation", false);
        mToggle.setChecked(checked);
        final SharedPreferences pref = getSharedPreferences("userInfo", 0);
        settingsRef = new Firebase(getResources().getString(R.string.firebaseUrl) + "/users/" + pref.getString("uid", "")).child("shareLocation");

        mToggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (mToggle.isChecked()) {
                    settingsRef.setValue(true);
                    pref.edit().putBoolean("shareLocation", true).apply();
                } else {
                    settingsRef.setValue(false);
                    pref.edit().putBoolean("shareLocation", false).apply();
                }
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
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        startActivity(intent);
                    }
                });
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
