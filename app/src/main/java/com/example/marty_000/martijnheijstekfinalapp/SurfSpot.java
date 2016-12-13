package com.example.marty_000.martijnheijstekfinalapp;

/**
 * Created by marty_000 on 5-12-2016.
 */

public class SurfSpot {
    public int dateID;
    public String spotName;
    public String country;
    public String spotLink;

    public SurfSpot (String spotName, String country, String spotLink) {
        this.spotName = spotName;
        this.country = country;
        this.spotLink = spotLink;
    }
    public SurfSpot (String spotName, String spotLink, int dateID, String country){
        this.spotName = spotName;
        this.spotLink = spotLink;
        this.dateID = dateID;
        this.country = country;
    }
    public String toString() {
        return spotName + " ("+country + ")";
    }
    }
