package com.example.marty_000.martijnheijstekfinalapp;


/*
    * I this activity the API checks for matching whoeid's and redui
 */
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class SearchActivity extends AppCompatActivity {

    ArrayList<SurfSpot> surfSpotList = new ArrayList<>();
    ListView listView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        listView = (ListView) findViewById(R.id.searchResultListView);
        Intent PrevScreenIntent = getIntent();
        String searchQuery = PrevScreenIntent.getStringExtra("searchQuery");

        //Get the tags the user provided
        URL completeUrl = null;

        try {
            String query = URLEncoder.encode(searchQuery.trim(), "UTF-8");

            completeUrl = new URL("http://where.yahooapis.com/v1/places.q('sydney%20opera%20house')?appid=dj0yJmk9TWtYeHdKcGxtdnZ5JmQ9WVdrOU5HRk9kVTFXTm1zbWNHbzlNQS0tJnM9Y29uc3VtZXJzZWNyZXQmeD00Yw--");
        } catch (IOException ex){
            ex.printStackTrace();
        }


        // Check for internet connection
        // Source: http://stackoverflow.com/questions/5474089/
        //          how-to-check-currently-internet-connection-is-available-or-not-in-android
        ConnectivityManager connectivityManager =
                (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if((connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() ==
                NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() ==
                        NetworkInfo.State.CONNECTED)) {

            // Retrieve the information from the API
            getMovieArray(completeUrl);

        } else {
            // Not connected to a network
            Toast.makeText(this, "No internet connection detected", Toast.LENGTH_LONG).show();
        }

        // Standard adapter
        ArrayAdapter<SurfSpot> moviesAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_list_item_1, surfSpotList);
        listView.setAdapter(moviesAdapter);

        // Actoin listener for the items in the listView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                SurfSpot surfSpot = (SurfSpot) listView.getItemAtPosition(i);
                Intent GoToSurfSpot = new Intent(SearchActivity.this, SurfSpotActivity.class);
                GoToSurfSpot.putExtra("surfSpot", surfSpot.spotName);
                GoToSurfSpot.putExtra("calendarDate", surfSpot.dateID);
                startActivity(GoToSurfSpot);
            }
        });
    }

    private void getMovieArray(URL url){
        try {
            // Get dict from the AsyncTask
            JSONObject jsonDict = new JSONObject(new WeatherAsyncTask().execute(url).get());
            System.out.println(jsonDict.getString("query"));
            if (jsonDict.getString("Response").equals("False")) {
                Toast.makeText(this, jsonDict.getString("Error"), Toast.LENGTH_SHORT).show();
            } else {
                JSONArray returnedSpots = new JSONArray(jsonDict.getString("Search"));
                for (int i = 0; i < returnedSpots.length(); i++) {
                    JSONObject surfSpotJSON = returnedSpots.getJSONObject(i);

                    surfSpotList.add(new SurfSpot(surfSpotJSON.get("city").toString(),
                            surfSpotJSON.get("region").toString(),
                            surfSpotJSON.get("country").toString()));
                    //json info title, movie.getString("Year"
                }
            }

        } catch (InterruptedException | ExecutionException | JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplication(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}

