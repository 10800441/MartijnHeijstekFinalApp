package com.example.marty_000.martijnheijstekfinalapp;

/* App: SurfsUp
 * Course: Native App Studio
 * Created: 16-12-2016
 * Author: Martijn Heijstek, 10800441
 *
 * Description: SurfSpot
 * A surfSpot is a place where a surfer can practice surfing
 * every spot has a unique id (spotLink) to find it in the "wunderground" API.
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
