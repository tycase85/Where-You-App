package mobiledev.unb.ca.whereyouapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

public class MapActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ImageButton tab1 = (ImageButton) findViewById(R.id.friendTab);
        final ImageButton tab2 = (ImageButton) findViewById(R.id.settingsTab);


        tab1.setOnClickListener
                (new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        Intent intent = new Intent(MapActivity.this, FriendActivity.class);
                        startActivity(intent);

                    }
                });
        tab2.setOnClickListener
                (new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {

                        Intent intent = new Intent(MapActivity.this, SettingsActivity.class);
                        startActivity(intent);

                    }
                });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}