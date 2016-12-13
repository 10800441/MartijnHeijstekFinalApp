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

        //Instanciate all the nesscecairy elements
        Auth = FirebaseAuth.getInstance();
        user = Auth.getCurrentUser();
        Intent PrevScreenIntent = getIntent();
        spotName = PrevScreenIntent.getStringExtra("surfSpot");
        spotLink= PrevScreenIntent.getStringExtra("spotLink");
        relativeDate  = PrevScreenIntent.getIntExtra("calendarDate", 0);
        spotSaved =  PrevScreenIntent.getBooleanExtra("savedSpot", false);
        database = FirebaseDatabase.getInstance();

        // Set the title of the activity
        TextView title = (TextView) findViewById(R.id.spotTitle);
        title.setText(spotName);
        weatherDescription = (TextView) findViewById(R.id.weatherDescription);
        saveSpotButton = (Button) findViewById(R.id.saveSpotButton);
        saveSpotTitle = (TextView) findViewById(R.id.saveSpotTitle);
        // retrieve saved data
        if (user != null) {


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
                                        saveSpotButton.setText(R.string.removeSpot);
                                        saveSpotTitle.setText(R.string.removeSpotTitle);
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
                    // ...
                }
            });
        }

        // access API to retrieve spot specifics
        retrieveSpotWeather();

        // Make a small calendar to change the date
        dateView = (TextView) findViewById(R.id.pickDateTV);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);

        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
        showDate(year, month + 1, day);



        
        // Action listener for signing out
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




    // This method retieves the wheather for the chosen day
    public void retrieveSpotWeather() {
        try {
            completeUrl = new URL("http://api.wunderground.com/api/3eedcfbf42e02e5e/forecast/" + spotLink + ".json");

            // Get dict from the AsyncTask
            JSONObject jsonDict = new JSONObject(new WeatherAsyncTask().execute(completeUrl).get());
            JSONObject response = jsonDict.getJSONObject("forecast");
            System.out.println(response.toString());
            JSONObject txt = response.getJSONObject("txt_forecast");
            System.out.println(txt.toString());
            JSONArray forecast = txt.getJSONArray("forecastday");

            for (int i = 0; i < forecast.length(); i++){
                JSONObject day = forecast.getJSONObject(i);
                // Get the weather for the required date
                if (day.getString("period").equals(String.valueOf(relativeDate))){
                    weatherDescription.setText(day.getString("fcttext_metric"));
                }
            } if (forecast.length() == 0) {
                weatherDescription.setText(R.string.weatherError);
            }

        } catch (InterruptedException | ExecutionException | JSONException | MalformedURLException e) {
            e.printStackTrace();
        }
    }
    public void onClick(View v) {
        switch (v.getId()) {

            // Save a surfSpot to fireBase
            case R.id.saveSpotButton:
                if (!spotSaved) {
                    Toast.makeText(this, "Spot is saved", Toast.LENGTH_SHORT).show();
                    surfSpotReference.child(spotName)
                            .setValue(new SurfSpot(spotName, spotLink, relativeDate, country));
                    saveSpotButton.setText(R.string.removeSpot);
                    spotSaved = true;
                } else if(spotSaved) {
                    Toast.makeText(this, "Spot is removed", Toast.LENGTH_SHORT).show();
                    surfSpotReference.child(spotName).removeValue();
                    spotSaved = false;
                    saveSpotButton.setText(R.string.saveSpot);
                }
                break;

            case R.id.saveSessionButton:
                final int saveDay = day;
                final int saveMonth = month;
                final int saveYear= year;
                    surfSessionReference.child((saveDay + "-" + (saveMonth+1) + "-" + saveYear))
                            .setValue(new Session(saveDay, saveMonth,saveYear, spotName));
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

    private DatePickerDialog.OnDateSetListener myDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    showDate(arg1, arg2 + 1, arg3);
                }
            };

    private void showDate(int year, int month, int day) {
        dateView.setText(new StringBuilder().append(day).append("/")
                .append(month).append("/").append(year));
    }
//    private boolean afterCurrent() {
//        boolean beforeToday = false;
//        try{
//
//            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//            Date currentDate = sdf.parse(calendar.get(Calendar.YEAR)+"-"+calendar.get(Calendar.MONTH)+"-"+  calendar.get(Calendar.DAY_OF_MONTH));
//            Date pickedDate = sdf.parse(year + "-" + month + "-" + day);
//            System.out.println(currentDate.toString());
//            System.out.println(pickedDate.toString());
//            if(pickedDate.before(currentDate)){
//                beforeToday = true;
//                System.out.println("");
//            }
//
//        }catch(ParseException ex){
//            ex.printStackTrace();
//        }
//        return beforeToday;
//    }

}
