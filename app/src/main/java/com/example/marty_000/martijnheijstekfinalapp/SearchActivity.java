package com.example.marty_000.martijnheijstekfinalapp;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

/* App: SurfsUp
 * Course: Native App Studio
 * Created: 16-12-2016
 * Author: Martijn Heijstek, 10800441
 *
 * Description: SearchActivity
 * This class displays the results from the users query.
 * The users searches for a SurfSpot by name. This query
 * is passed to the "wunderground" API and a list of
 * matching cities is returned. A user can view click on a
 * surfspot to view more information.
 */

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

        //Get the tags the user provided and form a URL
        URL completeUrl = null;

        try {
            String query = URLEncoder.encode(searchQuery.trim(), "UTF-8");
            completeUrl = new URL("http://api.wunderground.com/api/3eedcfbf42e02e5e/geolookup/forecast/q/"+ query + ".json");

        } catch (IOException ex){
            ex.printStackTrace();
        }

        // Use the WeatherAsincTask
        getSpotArray(completeUrl);

        // Fill the Listview
        setAdapter();

    }

    // A standard ArrayAdapter that will fill the ListView with names of known Surfspots
    private void setAdapter() {
        if (surfSpotList.size() == 0 || surfSpotList == null) {

            // Give the user the option to retry a search
            TextView subtitle = (TextView) findViewById(R.id.searchSubtitle);
            subtitle.setText(R.string.noSpots);
            RelativeLayout searchBox = (RelativeLayout) findViewById(R.id.searchBox);
            RelativeLayout resultsListView = (RelativeLayout) findViewById(R.id.resultsListView);
            searchBox.setVisibility(View.VISIBLE);
            resultsListView.setVisibility(View.GONE);
        }

        // Fill the ListView with the aquired surfspots
        else {
            ArrayAdapter<SurfSpot> surfSpotAdapter = new ArrayAdapter<>(this,
                    android.R.layout.simple_list_item_1, surfSpotList);

            listView.setAdapter(surfSpotAdapter);

            // Actoin listener for the items in the listView
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    SurfSpot surfSpot = (SurfSpot) listView.getItemAtPosition(i);
                    Intent goToSurfSpot = new Intent(SearchActivity.this, SurfSpotActivity.class);
                    goToSurfSpot.putExtra("surfSpot", surfSpot.spotName);
                    goToSurfSpot.putExtra("spotLink", surfSpot.spotLink);
                    goToSurfSpot.putExtra("calendarDate", 0);   // CalendarDate 0 corresponds to today
                    goToSurfSpot.putExtra("country", surfSpot.country);
                    goToSurfSpot.putExtra("spotSaved", false);
                    startActivity(goToSurfSpot);
                }
            });
        }
    }

    // Initial search had no results, the user must retry
    public void retrySearch(View v) {
        switch (v.getId()) {
            case R.id.SearchButton:
                EditText search = (EditText) findViewById(R.id.retrySearchEditText);
                String query = search.getText().toString();

                if (query.length() != 0) {
                    Intent intent = new Intent(getApplication(), SearchActivity.class);
                    intent.putExtra("searchQuery", query);
                    startActivity(intent);
                } else {
                    Toast.makeText(this, "Please enter the name of a place", Toast.LENGTH_SHORT).show();
                }
        }
    }


    // Retrieve all spots corresponding to the users query
    private void getSpotArray(URL url){
        try {
            // Get dict from the AsyncTask
            JSONObject jsonDict = new JSONObject(new WeatherAsyncTask().execute(url).get());
            JSONObject response = jsonDict.getJSONObject("response");
            JSONArray spotNameArray = response.getJSONArray("results");

            for (int i = 0; i < spotNameArray.length(); i++) {
                JSONObject spotName = spotNameArray.getJSONObject(i);
                // "l" stands for surfspot link and is a unique Spot id.
                surfSpotList.add(new SurfSpot(spotName.getString("name"),
                        spotName.getString("country_name"),
                        spotName.getString("l")));
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

