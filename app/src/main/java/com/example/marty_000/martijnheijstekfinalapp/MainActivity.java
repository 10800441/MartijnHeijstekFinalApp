package com.example.marty_000.martijnheijstekfinalapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* App: SurfsUp
 * Course: Native App Studio
 * Created: 16-12-2016
 * Author: Martijn Heijstek, 10800441
 *
 * Description: MainActivity
 * This class displays the users's personal overview.
 * In this class the user's saved SurfSpots and Sessions are displayed.
 * Also the user can search for new SurfSpots by name.
 *
 * App icon source: https://www.iconfinder.com/icons/522934/marine_ocean_sea_surf_wave_wavy_icon
 */

public class MainActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private DatabaseReference surfSpotReference;
    private DatabaseReference surfSessionReference;
    SharedPreferences prefs;

    public static final String ANONYMOUS = "anonymous";
    String mUsername;
    private static final String TAG = "MainActivity";
    private ArrayAdapter<SurfSpot> spotAdapter;
    private ArrayAdapter<Session> sessionAdapter;
    ArrayList<SurfSpot> userSavedSpots = new ArrayList<>();
    ArrayList<Session> userSavedSessions = new ArrayList<>();
    ListView spotListView;
    ListView sessionListView;
    TextView subtitle;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    private FirebaseUser user;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Instantiate instances an get TextViews
        initialiseValues();

        // Send user to the SignInActivity
        if (user == null) {
            // Give the user a message to sign in
            welcomeUser();

            // Not signed in, launch the Sign In activity
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;

        } else {
            //
            storeUserName();

            // peace where the adapters are set to the listViews
            setSpotAdapter();
            setSessionAdapter();

            // Fill ListViews with saved items
            getSavedSpots();
            getSavedSessions();
            
        }



    }

    // Instantiate instances an get TextViews
    private void initialiseValues(){
        // Check if a firebase instance is present
        if (database == null) {
            database = FirebaseDatabase.getInstance();
        }
        mUsername = ANONYMOUS;
        prefs = getApplicationContext().getSharedPreferences("storeUsername", MODE_PRIVATE);
        context = getApplicationContext();

        // Initialize Firebase Auth and get user
        mFirebaseAuth = FirebaseAuth.getInstance();
        user =  mFirebaseAuth.getCurrentUser();

        subtitle = (TextView) findViewById(R.id.subtitleMain);
        spotListView = (ListView) findViewById(R.id.spotListView);
        sessionListView = (ListView) findViewById(R.id.sessionListView);
    }

    // Save the username in SharedPreferences
    private void storeUserName(){
        if (!prefs.contains("username")) {
            SharedPreferences.Editor prefsEditor = prefs.edit();
            mUsername = user.getDisplayName();
            if (mUsername != null) prefsEditor.putString("username", mUsername);
            prefsEditor.apply();
        } else {
            mUsername = prefs.getString("username", null);
        }
        subtitle.setText("Signed in as " +   mUsername);
    }

    // Fill the SurfSpot-listView
    private void setSpotAdapter() {
        spotAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, userSavedSpots);
        spotListView.setAdapter(spotAdapter);

        spotListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SurfSpot surfSpot = (SurfSpot) spotListView.getItemAtPosition(i);

                // Go to SutfSpotActivity
                Intent goToSurfSpot = new Intent(MainActivity.this, SurfSpotActivity.class);
                goToSurfSpot.putExtra("surfSpot", surfSpot.spotName);
                goToSurfSpot.putExtra("spotLink", surfSpot.spotLink);
                goToSurfSpot.putExtra("calendarDate", surfSpot.dateID);
                goToSurfSpot.putExtra("savedSpot", true);
                goToSurfSpot.putExtra("country", surfSpot.country);
                startActivity(goToSurfSpot);
            }
        });

        // Long press to delete a spot
        spotListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                SurfSpot spot = spotAdapter.getItem(pos);
                if (spot != null) {
                    surfSpotReference.child(spot.spotName).removeValue();
                    userSavedSpots.remove(spot);
                    spotAdapter.clear();
                    spotAdapter.notifyDataSetChanged();
                }
                return true;
            }
        });
    }

    // Fill the Session-listView
    private void setSessionAdapter() {

        sessionAdapter = new ArrayAdapter<>(this,
            android.R.layout.simple_list_item_1, userSavedSessions);
        sessionListView.setAdapter(sessionAdapter);

        sessionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Session session = (Session) sessionListView.getItemAtPosition(i);
                if(session.comment != null){
                    showComment(session);
                }
            }
        });

        // Long press to delete a session
        sessionListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1, int pos, long id) {
                Session session = sessionAdapter.getItem(pos);
                surfSessionReference.child(session.day+"-"+session.month+"-"+session.year+session.spotName).removeValue();
                userSavedSessions.remove(session);
                sessionAdapter.clear();
               sessionAdapter.notifyDataSetChanged();
                return true;
            }
        });
    }

    // Show a comment the user has given on a session
    private void showComment(Session session) {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("View your session");
        alert.setMessage("Date " + session.day + "/" + session.month + "/" + session.year+ "\nSurf spot "+
                session.spotName+ "\nComment:\n"+session.comment);

        // OK Button
        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });
        alert.show();
    }

    // Search for a surfSpot by name
    public void searchSurfSpot (View v) {
        switch (v.getId()) {
            case R.id.SearchButton:
                EditText search = (EditText) findViewById(R.id.SearchEditText);
                String query = search.getText().toString();

                if (query.length() != 0 && checkInternetConnection()) {
                    Intent intent = new Intent(getApplication(), SearchActivity.class);
                    intent.putExtra("searchQuery", query);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Please enter the name of a place", Toast.LENGTH_SHORT).show();
                }
        }
    }


    // Check for internet connection
    // Source: http://stackoverflow.com/questions/5474089/
    //          how-to-check-currently-internet-connection-is-available-or-not-in-android
    private boolean checkInternetConnection(){
        ConnectivityManager connectivityManager =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);

        if((connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() ==
                NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() ==
                        NetworkInfo.State.CONNECTED)) {

            // Internet connection present
            return true;

        } else {
            // Not connected to a network
            Toast.makeText(this, "No internet connection detected", Toast.LENGTH_LONG).show();
            return false;
        }
    }


    // Retrieve the user saved SurfSpots from firebase
    private void getSavedSpots() {

        surfSpotReference = database.getReference("/users/" + user.getUid() + "/surfSpots/");
        surfSpotReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String, HashMap<String, String>> commentKey = (HashMap) dataSnapshot.getValue();
                if (commentKey != null) {

                    // Every surfSpot has multiple values (name, country, link, and date)
                    for (Map.Entry<String, HashMap<String, String>> entry : commentKey.entrySet()) {

                        String link = null;
                        String name = null;
                        long date = 0;
                        String country = null;
                        HashMap<String, String> a = entry.getValue();
                        Iterator it = a.entrySet().iterator();

                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            if (pair.getKey().toString().equals("spotName")) {
                                name = (String) pair.getValue();
                            }
                            if (pair.getKey().toString().equals("spotLink")) {
                                link = (String) pair.getValue();
                            }
                            if (pair.getKey().toString().equals("dateID")) {
                                date = (long) pair.getValue();
                            }
                            if (pair.getKey().toString().equals("country")) {
                                country = (String) pair.getValue();
                            }
                        }
                        userSavedSpots.add(new SurfSpot(name, link, (int) date, country));
                    }
                }
                spotAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    // Retrieve the user saved sessions from firebase
    private void getSavedSessions() {

        surfSessionReference = database.getReference("/users/" + user.getUid() + "/surfSessions/");
        surfSessionReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                HashMap<String, HashMap<String, String>>  commentKey = (HashMap) dataSnapshot.getValue();
                if (commentKey != null) {

                    for(Map.Entry<String, HashMap<String, String>> entry : commentKey.entrySet()) {
                        long year = 0;
                        long month = 0;
                        long day = 0;
                        String spotName = null;
                        String comment = null;

                        HashMap<String, String> a = entry.getValue();
                        Iterator it = a.entrySet().iterator();


                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry) it.next();
                            if (pair.getKey().toString().equals("year")) {
                                year = (long) pair.getValue();
                            }
                            if (pair.getKey().toString().equals("month")) {
                                month = (long) pair.getValue();
                            }
                            if (pair.getKey().toString().equals("day")) {
                                day = (long) pair.getValue();
                            }
                            if (pair.getKey().toString().equals("spotName")) {
                                spotName = (String) pair.getValue();
                            }
                            if (pair.getKey().toString().equals("comment")) {
                                comment = (String) pair.getValue();
                            }
                        }
                        if (comment != null){
                            userSavedSessions.add(new Session((int) day, (int) month, (int) year, spotName, comment));
                        } else {
                            userSavedSessions.add(new Session((int) day, (int) month, (int) year, spotName));
                        }
                    }
                }
                sessionAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    // Give a message that he/she needs to log in
    private void welcomeUser() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Welcome User");
        alert.setMessage("Before you van view your preferences you first need to log-in");

        // OK Button
        alert.setPositiveButton("Okey", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
               dialog.cancel();
            }
        });
        alert.show();
    }

    // The user can return to the SignInActivity
    @Override
    public void onBackPressed() {
        // Ask the user if he/she wants to  log out
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Warning");
        alert.setMessage("are you sure you want to sign out?");

        // Sign the user out and rgo to the SignInActivity
        alert.setPositiveButton("Sign out", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getApplication(),SignInActivity.class);
                startActivity(intent);
                finish();

            }
        });
        alert.setNegativeButton("Stay signed in", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.cancel();
            }
        });

        alert.show();
    }
}