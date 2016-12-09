package com.example.marty_000.martijnheijstekfinalapp;

/**
 * Created by marty_000 on 5-12-2016.
 */

public class SurfSpot {
    public int dateID;
    public String spotName;
    public String country;
    public String surferType;
    public String spotWhoeid;
    public int totalSurfers;

    public SurfSpot (int dateID, String spotName, int totalSurfers) {
        this.dateID = dateID;
        this.spotName = spotName;
        this.totalSurfers= totalSurfers;
    }
    public SurfSpot (String spotName, String country, String spotWhoeid){
        this.spotName = spotName;
        this.country = country;
        this.spotWhoeid = spotWhoeid;
    }
    }
