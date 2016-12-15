package com.example.marty_000.martijnheijstekfinalapp;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/* App: SurfsUp
 * Course: Native App Studio
 * Created: 16-12-2016
 * Author: Martijn Heijstek, 10800441
 *
 * Description: SurfSpotActivity
 * This class displays an overview of a specific surfSpot including
 * the current waether.
 */

public class SurfSpotActivity extends AppCompatActivity {

    private FirebaseDatabase database;
    private ValueEventListener surfSpotListener;
    private DatabaseReference surfSpotReference;
    private DatabaseReference surfSessionReference;
    private FirebaseAuth Auth;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseUser user;
    private static final String TAG = "SurfSpotActivity";

    private String spotLink;
    private URL completeUrl;
    private boolean spotSaved;
    int relativeDate;
    private String spotName;
    private String country;

    private DatePicker datePicker;
    private Calendar calendar;
    private TextView dateView;
    private int year, month, day;

    TextView weatherDescription;
    Button saveSpotButton;
    TextView saveSpotTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surf_spot);

        //Instanciate all the necessary elements
        Auth = FirebaseAuth.getInstance();
        user = Auth.getCurrentUser();
        Intent PrevScreenIntent = getIntent();
        spotName = PrevScreenIntent.getStringExtra("surfSpot");
        spotLink= PrevScreenIntent.getStringExtra("spotLink");
        relativeDate  = PrevScreenIntent.getIntExtra("calendarDate", 0);
        spotSaved =  PrevScreenIntent.getBooleanExtra("savedSpot", false);
        country = PrevScreenIntent.getStringExtra("country");
        database = FirebaseDatabase.getInstance();

        // Set the title of the activity
        TextView title = (TextView) findViewById(R.id.spotTitle);
        title.setText(spotName);
        weatherDescription = (TextView) findViewById(R.id.weatherDescription);
        saveSpotButton = (Button) findViewById(R.id.saveSpotButton);
        saveSpotTitle = (TextView) findViewById(R.id.saveSpotTitle);

        // retrieve saved data
        if (user != null) {
            // retrieve user preferences
            retrievePreferences();
        }
        if(spotSaved) {
            saveSpotButton.setText(R.string.removeSpot);
            saveSpotTitle.setText(R.string.removeSpotTitle);
        }


        // access API to retrieve spot specifics
        retrieveSpotWeather();

        // Make a small calendar to change the date
        dateView = (TextView) findViewById(R.id.pickDateTV);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate();

        // Action listener for user signed out
        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                    SurfSpotActivity.this.finish();
                }
            }
        };
    }


    // Set the page corresponding to the users preferences
    private void retrievePreferences(){
        surfSpotReference = database.getReference("/users/" + user.getUid() + "/surfSpots/");
        surfSessionReference = database.getReference("/users/" + user.getUid() + "/surfSessions/");
        surfSpotReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                HashMap<String, HashMap<String, String>>  commentKey = (HashMap) dataSnapshot.getValue();
                if (commentKey != null){

                    for(Map.Entry<String, HashMap<String, String>> entry : commentKey.entrySet()) {
                        HashMap<String, String> a = entry.getValue();
                        Iterator it =a.entrySet().iterator();

                        while (it.hasNext()) {
                            Map.Entry pair = (Map.Entry)it.next();
                            if(pair.getKey().toString().equals("spotLink")){
                                String link = (String) pair.getValue();
                                if (link.equals(spotLink)){
                                    spotSaved = true;
                                }
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // Getting Post failed, log a message
                Log.w(TAG, "loadPost:onCancelled", databaseError.toException());
            }
        });
    }

    // This method retieves the wheather for the chosen day
    public void retrieveSpotWeather() {
        try {
            completeUrl = new URL("http://api.wunderground.com/api/3eedcfbf42e02e5e/forecast/" +
                    spotLink + ".json");

            // Get dict from the AsyncTask
            JSONObject jsonDict = new JSONObject(new WeatherAsyncTask().execute(completeUrl).get());
            if (jsonDict.getJSONObject("forecast")!=null) {

                // Get sublist of sublists
                JSONObject response = jsonDict.getJSONObject("forecast");
                JSONObject txt = response.getJSONObject("txt_forecast");
                JSONArray forecast = txt.getJSONArray("forecastday");

                for (int i = 0; i < forecast.length(); i++) {
                    JSONObject day = forecast.getJSONObject(i);
                    // Get the weather for the required date (default is current day)
                    if (day.getString("period").equals(String.valueOf(relativeDate))) {
                        weatherDescription.setText(day.getString("fcttext_metric"));
                    }
                }

            } else {  weatherDescription.setText(R.string.weatherError);
            }

        } catch (InterruptedException | ExecutionException | JSONException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void onClick(View v) {
        switch (v.getId()) {

            // Save a surfSpot to firebase
            case R.id.saveSpotButton:
                if (!spotSaved) {
                    Toast.makeText(this, "Spot is saved", Toast.LENGTH_SHORT).show();
                   surfSpotReference.child(spotName).setValue(new SurfSpot(spotName, spotLink, relativeDate, country));
                    saveSpotButton.setText(R.string.removeSpot);
                    spotSaved = true;
                }

                // remove a surfSpot from firebase
                else if(spotSaved) {
                    Toast.makeText(this, "Spot is removed", Toast.LENGTH_SHORT).show();
                    surfSpotReference.child(spotName).removeValue();
                    spotSaved = false;
                    saveSpotButton.setText(R.string.saveSpot);
                }
                break;

            // Save a session to firebase
            case R.id.saveSessionButton:

                // The user can save only one session per day
                surfSessionReference.
                        child((day + "-" + month + "-" + year)).
                        setValue(new Session(day, month, year, spotName));
                Toast.makeText(this, "Session is saved", Toast.LENGTH_SHORT).show();

                break;
        }
    }


    // The following methods are used for the calendar, source:
    // http://www.tutorialspoint.com/android/android_datepicker_control.htm
    @SuppressWarnings("deprecation")
    public void setDate(View view) {
        showDialog(999);
        Toast.makeText(getApplicationContext(), "ca",
                Toast.LENGTH_SHORT)
                .show();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        if (id == 999) {
            return new DatePickerDialog(this,
                    myDateListener, year, month, day);
        }
        return null;
    }

    // Get the date the user has chosen
    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    year = arg1;
                    month = arg2+1;
                    day = arg3;
                    showDate();
                }
            };

    private void showDate() {
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}
