package mobiledev.unb.ca.whereyouapp;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;
import com.firebase.ui.FirebaseRecyclerAdapter;
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
        final SharedPreferences pref = getSharedPreferences("userInfo", 0);

        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase(getResources().getString(R.string.firebaseUrl) + "/users/");

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        final RecyclerView.ItemAnimator itemAnimator = new DefaultItemAnimator();
        itemAnimator.setAddDuration(1000);
        itemAnimator.setRemoveDuration(1000);
        recyclerView.setItemAnimator(itemAnimator);
        final List<FriendData> adapterList = new ArrayList<>();
        final Recycler_View_Adapter adapter = new Recycler_View_Adapter(adapterList, getApplication());
        recyclerView.setAdapter(adapter);


        final ImageButton tab1 = (ImageButton) findViewById(R.id.mapTab);
        final ImageButton tab2 = (ImageButton) findViewById(R.id.settingsTab);
        final Intent intent = new Intent(FriendActivity.this, FriendActivity.class);
        recyclerView.addOnItemTouchListener(
                new RecyclerItemClickListener(this, new RecyclerItemClickListener.OnItemClickListener() {
                    @Override public void onItemClick(final View view, final int position) {
                        {
                            AlertDialog alertDialog = new AlertDialog.Builder(FriendActivity.this).create();
// Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                            input.setInputType(InputType.TYPE_CLASS_TEXT);
                            alertDialog.setView(input);
                            alertDialog.setMessage("Edit Name");
                            alertDialog.setButton("Open Window", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    recyclerView.getAdapter().notifyItemRemoved(position);
                                    //adapter.remove(adapterList.remove(position));
                                    //adapterList.remove(position);
                                    startActivity(intent);
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

        ref.child(pref.getString("uid", "")).child("friends").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                List<FriendData> adapterList = new ArrayList<>();

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    FriendData f = new FriendData((String) snap.getValue());
                    adapterList.add(f);
                }

                Recycler_View_Adapter adapter = new Recycler_View_Adapter(adapterList, getApplication());
                recyclerView.setAdapter(adapter);
                }

                @Override
                public void onCancelled(FirebaseError firebaseError) {

                }
            }
        );

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(FriendActivity.this);
                builder.setTitle("Add Contact");

                final EditText input = new EditText(FriendActivity.this);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        final Query queryRef = ref.orderByChild("email").equalTo(m_Text);

                        queryRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                                if(snapshot.child("email").getValue().equals(m_Text)) {
                                    ref.child(pref.getString("uid", "")).child("friends").child(snapshot.getKey()).setValue(m_Text);
                                }
                                else
                                {
                                    Toast.makeText(FriendActivity.this, "Some Message", Toast.LENGTH_LONG).show();
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
                builder.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        final Query queryRef = ref.orderByChild("email").equalTo(m_Text);

                        queryRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                                if (snapshot.child("email").getValue().equals(m_Text)) {
                                    ref.child(pref.getString("uid", "")).child("friends").child(snapshot.getKey()).setValue(null);
                                } else {
                                    Toast.makeText(FriendActivity.this, "Some Message", Toast.LENGTH_LONG).show();
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

                builder.show();
            }
        });
    }




}
