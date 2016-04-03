package mobiledev.unb.ca.whereyouapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.google.android.gms.plus.model.people.Person;

import java.util.ArrayList;
import java.util.List;

public class FriendActivity extends AppCompatActivity {
    private String m_Text = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final EditText input = new EditText(this);

        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase(getResources().getString(R.string.firebaseUrl) + "/users/");


        List<FriendData> data = fill_with_data();

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        Recycler_View_Adapter adapter = new Recycler_View_Adapter(data, getApplication());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);

        final ImageButton tab1 = (ImageButton) findViewById(R.id.mapTab);
        final ImageButton tab2 = (ImageButton) findViewById(R.id.settingsTab);

        //final TextView friendTab1 = (TextView) findViewById(R.id.friend1);
        //final TextView friendTab2 = (TextView) findViewById(R.id.friend2);
        //final TextView friendTab3 = (TextView) findViewById(R.id.friend3);

        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(View view, int position) {
                        {

                            AlertDialog alertDialog = new AlertDialog.Builder(FriendActivity.this).create();

// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                            input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                            alertDialog.setView(input);
                            alertDialog.setMessage("Edit Info");
                            alertDialog.setButton("Open Window", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });



                            alertDialog.show();
                        }
                    }
                })
        );

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

       /* friendTab1.setOnClickListener
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
*/

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(FriendActivity.this);
                builder.setTitle("Add Contact");

// Set up the input
                final EditText input = new EditText(FriendActivity.this);
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

// Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        final Query queryRef = ref.orderByChild("email").equalTo(m_Text);

                        queryRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                                if(snapshot.child("email").getValue().equals(m_Text)) {
                                    ref.child()
                                }
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                            // ....
                        });
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }

    public List<FriendData> fill_with_data() {

        List<FriendData> data = new ArrayList<>();

        data.add(new FriendData("Alex F.", "Settings", R.drawable.contact));
        data.add(new FriendData("Beth C.", "Settings", R.drawable.contact));
        data.add(new FriendData("Frank P.", "Settings", R.drawable.contact));
        data.add(new FriendData("Megan L.", "Settings", R.drawable.contact));
        data.add(new FriendData("Oscar W.", "Settings", R.drawable.contact));
        data.add(new FriendData("Robert S.", "Settings", R.drawable.contact));

        return data;
    }

    private void setFirebaseListeners(Firebase ref){
       // Query queryRef = ref.orderByChild("email").equalTo(email.getValue());

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot snapshot, String str) {
               System.out.println(snapshot.getKey());

            }

            @Override
            public void onChildRemoved(DataSnapshot snapshot) {
                String title = (String) snapshot.child("title").getValue();
                System.out.println("The blog post titled " + title + " has been deleted");
            }

            @Override
            public void onChildChanged(DataSnapshot snapshot, String str){

            }

            @Override
            public void onChildMoved(DataSnapshot snapshot, String str){

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                System.out.println("The read failed: " + firebaseError.getMessage());
            }
        });
    }

}
