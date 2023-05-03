package com.example.sampleassignment1;

import com.google.firebase.database.IgnoreExtraProperties;

import java.time.LocalDateTime;

@IgnoreExtraProperties
public class UserLocEntry {

    public String address;
    public String date;
    public double latitude;
    public double longitude;

    public UserLocEntry(String address, String date, double latitude, double longitude) {
        this.address = address;
        this.date = date;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}