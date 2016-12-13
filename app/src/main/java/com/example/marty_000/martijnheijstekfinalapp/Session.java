package com.example.marty_000.martijnheijstekfinalapp;

public class Session {

    public String spotName;
    public int year;
    public int month;
    public int day;

    public Session (int day, int month, int year, String spotName){
        this.spotName = spotName;
        this.year = year;
        this.month = month;
        this.day = day;
    }
    @Override
    public String toString() {
        return day + "/" + month + "/" + year + "; " + spotName;
    }
}
