package mobiledev.unb.ca.whereyouapp;

/**
 * Created by rcase on 13/02/16.
 */

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends FragmentActivity {

    Button mAppInfoBtn;
    Boolean mShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button button1 = (Button) findViewById(R.id.mapButt);
        final Button button2 = (Button) findViewById(R.id.friendButt);
        final Button button3 = (Button) findViewById(R.id.settingsButt);
        mAppInfoBtn = (Button) findViewById(R.id.aboutButt);

        button1.setOnClickListener
                (new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, MapActivity.class);
                        startActivity(intent);
                    }
                });
        button2.setOnClickListener
                (new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(MainActivity.this, FriendActivity.class);
                        startActivity(intent);
                    }
                });
        button3.setOnClickListener
                (new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                        startActivity(intent);

                    }
                });

        mAppInfoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(MainActivity.this); // Context, this, etc.
                dialog.setContentView(R.layout.about_popup);
                dialog.setTitle(R.string.dialog_title);
                dialog.show();
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
    public void onResume(){
        super.onResume();

        String userId = getSharedPreferences("userInfo", 0).getString("uid", "");

        Firebase ref = new Firebase(getResources().getString(R.string.firebaseUrl)).child("users").child(userId).child("shareLocation");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                SharedPreferences.Editor edit = getSharedPreferences("userInfo", 0).edit();
                mShared = (Boolean) dataSnapshot.getValue();
                edit.putBoolean("shareLocation", mShared).apply();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });
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