package mobiledev.unb.ca.whereyouapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

public class FriendActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final ImageButton tab1 = (ImageButton) findViewById(R.id.mapTab);
        final ImageButton tab2 = (ImageButton) findViewById(R.id.settingsTab);

        final TextView friendTab1 = (TextView) findViewById(R.id.friend1);
        final TextView friendTab2 = (TextView) findViewById(R.id.friend2);
        final TextView friendTab3 = (TextView) findViewById(R.id.friend3);

        tab1.setOnClickListener
                (new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(FriendActivity.this, MapActivity.class);
                        startActivity(intent);

                    }
                });

        tab2.setOnClickListener
                (new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        Intent intent = new Intent(FriendActivity.this, SettingsActivity.class);
                        startActivity(intent);

                    }
                });

        friendTab1.setOnClickListener
                (new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        AlertDialog alertDialog = new AlertDialog.Builder(FriendActivity.this).create();
                        alertDialog.setMessage("Edit Info");
                        alertDialog.setButton("Open Window", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });



                        alertDialog.show();
                    }
                });

        friendTab2.setOnClickListener
                (new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog alertDialog = new AlertDialog.Builder(FriendActivity.this).create();
                        alertDialog.setMessage("Edit Info");
                        alertDialog.setButton("Open Window", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });



                        alertDialog.show();
                    }
                });
        friendTab3.setOnClickListener
                (new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog alertDialog = new AlertDialog.Builder(FriendActivity.this).create();
                        alertDialog.setMessage("Edit Info");
                        alertDialog.setButton("Open Window", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });



                        alertDialog.show();
                    }
                });


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(FriendActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}
