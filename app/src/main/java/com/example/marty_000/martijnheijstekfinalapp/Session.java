package com.example.marty_000.martijnheijstekfinalapp;

public class Session {

    public int dateID;
    public String spotName;
    public String surferType;
    public int startTime;
    public int endTime;

    public Session (int dateID, String spotName, String surferType, int startTime, int endTime){
        this. dateID =  dateID;
        this.spotName = spotName;
        this.surferType = surferType;

        this.startTime = startTime;
        this.endTime = endTime;
    }
    @Override
    public String toString() {
        return dateID + spotName;
    }
}
