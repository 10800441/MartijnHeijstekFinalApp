package com.example.marty_000.martijnheijstekfinalapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SurfSpotActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_surf_spot);

        Intent PrevScreenIntent = getIntent();
        String spotName = PrevScreenIntent.getStringExtra("surfSpot");
        String calendarDate  = PrevScreenIntent.getStringExtra("calendarDate");

        //TODO Set the textViews

        //TODO calendar thingy with calendarDate




    }
}
