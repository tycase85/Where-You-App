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
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendActivity extends AppCompatActivity {
    private String m_Text = "";

    private RecyclerView.Adapter mAdapter;
    private RecyclerView mRecyclerView;
    private RecyclerView.LayoutManager mLayoutManager;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final SharedPreferences pref = getSharedPreferences("userInfo", 0);

        Firebase.setAndroidContext(this);
        final Firebase ref = new Firebase(getResources().getString(R.string.firebaseUrl) + "/users/");

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        ArrayList<FriendData> friends = new ArrayList<>();
        mAdapter = new MyAdapter(friends);
        mRecyclerView.setAdapter(mAdapter);

        final ImageButton tab1 = (ImageButton) findViewById(R.id.mapTab);
        final ImageButton tab2 = (ImageButton) findViewById(R.id.settingsTab);

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
                ArrayList<FriendData> adapterList = new ArrayList<>();

                    for (DataSnapshot snap : dataSnapshot.getChildren()) {
                        FriendData f = new FriendData((String) snap.getValue(), (String) snap.getKey());
                        adapterList.add(f);
                    }

                    mAdapter = new MyAdapter(adapterList);
                    mRecyclerView.setAdapter(mAdapter);
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
                input.setInputType(InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        m_Text = input.getText().toString();
                        final Query queryRef = ref.orderByChild("email").equalTo(m_Text);
                        int itemCount = mAdapter.getItemCount();

                        queryRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                if(dataSnapshot.getChildrenCount() != 1)
                                    Toast.makeText(FriendActivity.this, "User does not exist", Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCancelled(FirebaseError firebaseError) {

                            }
                        });

                        queryRef.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
                                if (snapshot.child("email").getValue().equals(m_Text)) {
                                    ref.child(pref.getString("uid", "")).child("friends").child(snapshot.getKey()).setValue(m_Text);
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
                                Toast.makeText(FriendActivity.this, "User with that email does not exist.", Toast.LENGTH_LONG).show();
                            }

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

    public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

        ArrayList<FriendData> list;

        public class ViewHolder extends RecyclerView.ViewHolder {
            public TextView mTextView;
            public String key;

            public ViewHolder(View v) {
                super(v);
                mTextView = (TextView) v.findViewById(R.id.friendEmail);
            }
        }

        public MyAdapter(ArrayList<FriendData> list) {
            this.list = list;
        }

        @Override
        public MyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            //Inflate the layout, initialize the View Holder
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_layout, parent, false);
            final ViewHolder holder = new ViewHolder(v);
            v.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(final View v) {
                    {
                        AlertDialog.Builder alert = new AlertDialog.Builder(
                                FriendActivity.this);
                        alert.setTitle("Remove Friend");
                        alert.setMessage("Are you sure to delete record?");
                        alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String uid = getSharedPreferences("userInfo", 0).getString("uid", "");
                                String key = holder.key;
                                new Firebase(getResources().getString(R.string.firebaseUrl) + "/users/" + uid + "/friends/" + key).setValue(null);
                            }
                        });
                        alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {

                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                                dialog.dismiss();
                            }
                        });

                        alert.show();
                    }
                    return false;
                }
            });

            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            //Use the provided View Holder on the onCreateViewHolder method to populate the current row on the RecyclerView
            holder.mTextView.setText(list.get(position).email);
            holder.key = list.get(position).key;
        }

        @Override
        public int getItemCount() {
            //returns the number of elements the RecyclerView will display
            return list.size();
        }

        @Override
        public void onAttachedToRecyclerView(RecyclerView recyclerView) {
            super.onAttachedToRecyclerView(recyclerView);
        }

        // Insert a new item to the RecyclerView on a predefined position
        public void insert(int position, FriendData data) {
            list.add(position, data);
            notifyItemInserted(position);
        }

        // Remove a RecyclerView item containing a specified Data object
        public void remove(FriendData data) {
            int position = list.indexOf(data);
            list.remove(position);
            notifyItemRemoved(position);
        }

    }

}
