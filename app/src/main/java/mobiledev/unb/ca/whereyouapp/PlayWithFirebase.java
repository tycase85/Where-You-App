package mobiledev.unb.ca.whereyouapp;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.firebase.client.Firebase;

public class PlayWithFirebase extends AppCompatActivity {

    EditText mAddValueTxt;
    EditText mUpdateKeyTxt;
    EditText mUpdateValuebTxt;
    Firebase ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_with_firebase);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        //setSupportActionBar(toolbar);


        mAddValueTxt = (EditText) findViewById(R.id.txtAddToFirebase);
        mUpdateKeyTxt= (EditText) findViewById(R.id.txtUpdateKey);
        mUpdateValuebTxt = (EditText) findViewById(R.id.txtUpdateValue);

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
        String add = mAddValueTxt.getText().toString();
        if((!add.equals("")) && (!add.equals("Value"))){
            ref.push().setValue(add);
        }
    }

}
