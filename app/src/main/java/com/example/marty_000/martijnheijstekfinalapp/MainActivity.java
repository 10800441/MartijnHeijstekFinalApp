package com.example.marty_000.martijnheijstekfinalapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    private ArrayAdapter<SurfSpot> spotAdapter;
    private ArrayAdapter<Session> sessionAdapter;
    SharedPreferences prefs;
    public static final String ANONYMOUS = "anonymous";
    String mUsername;
    private static final String TAG = "MainActivity";
    public static final String MESSAGES_CHILD = "messages";
    private FirebaseAuth.AuthStateListener authListener;
    ArrayList<SurfSpot> userSavedSpots = new ArrayList<>();
    ArrayList<Session> userSavedSessions = new ArrayList<>();

    ListView spotListView;
    ListView sessionListView;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser mFirebaseUser;
    private GoogleApiClient mGoogleApiClient;
    Context context;

    private DatabaseReference mFirebaseDatabaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mUsername = ANONYMOUS;
        prefs = getApplicationContext().getSharedPreferences("listNames", MODE_PRIVATE);
        context = getApplicationContext();
        TextView subtitle = (TextView) findViewById(R.id.subtitleMain);
        spotListView = (ListView) findViewById(R.id.spotListView);
        sessionListView = (ListView) findViewById(R.id.sessionListView);

        // upon first visit retrieve username
        if (!prefs.contains("username")) {
            retrieveUsername();
            SharedPreferences.Editor prefsEditor = prefs.edit();
            if (mUsername != null) prefsEditor.putString("username", mUsername);
            prefsEditor.apply();
        } else mUsername = prefs.getString("username", "");


        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mFirebaseUser = mFirebaseAuth.getCurrentUser();
        if (mFirebaseUser == null) {

            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        } else {
            mUsername = mFirebaseUser.getDisplayName();
            subtitle.setText("You are signed in as "+ mUsername);
        }

        // peace where the adapter is set to the listView
        setAdapters(getApplicationContext());



    }

private void setAdapters(Context context){
    spotAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_list_item_1, userSavedSpots);
    spotListView.setAdapter(spotAdapter);
    sessionAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_list_item_1, userSavedSessions);
    sessionListView.setAdapter(sessionAdapter);

    //TODO set actionlisteners

    spotListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            SurfSpot surfSpot = (SurfSpot) spotListView.getItemAtPosition(i);
            Intent SurfSpot = new Intent(MainActivity.this, SurfSpotActivity.class);
            SurfSpot.putExtra("surfSpot", surfSpot.spotName);
            SurfSpot.putExtra("calendarDate", surfSpot.dateID);
            startActivity(SurfSpot);
        }
    });
}

    public void searchSurfSpot(View v) {
        switch (v.getId()) {
            case R.id.SearchButton:
                EditText search = (EditText) findViewById(R.id.SearchEditText);
                String query = search.getText().toString();
                if (query.length() != 0) {
                    Intent intent = new Intent(getApplication(), SearchActivity.class);
                    intent.putExtra("searchQuery", query);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Please enter the name of a spot of place", Toast.LENGTH_SHORT).show();
                }
        }
    }

private void retrieveUsername() {
    AlertDialog.Builder alert = new AlertDialog.Builder(this);
    alert.setTitle("Welcome User");
    alert.setMessage("Before you van view your preferences you first need to log-in");
    // Open the soft keyboard
    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);


    alert.setPositiveButton("Okey", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
               dialog.cancel();
            }
    });
    }
}
