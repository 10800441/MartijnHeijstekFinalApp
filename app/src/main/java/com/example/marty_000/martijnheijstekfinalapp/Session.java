package com.example.marty_000.martijnheijstekfinalapp;

/* App: SurfsUp
 * Course: Native App Studio
 * Created: 16-12-2016
 * Author: Martijn Heijstek, 10800441
 *
 * Description: Session
 * A Session is an planned surf activity;
 * A user must chose a place and a date for the activity.
 * Optionally a comment can be added.
 */

public class Session {

    public String spotName;
    public int year;
    public int month;
    public int day;
    public String comment;

    public Session (int day, int month, int year, String spotName){
        this.spotName = spotName;
        this.year = year;
        this.month = month;
        this.day = day;
    }
    public Session (int day, int month, int year, String spotName, String comment){
        this.spotName = spotName;
        this.year = year;
        this.month = month;
        this.day = day;
        this.comment = comment;
    }

    @Override
    public String toString() {
        return day + "/" + month + "/" + year + "; " + spotName;
    }
}
