package com.example.sampleassignment1;

import com.google.firebase.database.IgnoreExtraProperties;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Comparator;

@IgnoreExtraProperties
public class UserLocEntry {

    public String address;
    public String date;
    public double latitude;
    public double longitude;
    public Long epoch;

    public UserLocEntry(String address, LocalDateTime date, double latitude, double longitude) {
        this.address = address;
        this.date = date.toString();
        this.latitude = latitude;
        this.longitude = longitude;
        this.epoch = date.toEpochSecond(ZoneOffset.UTC);
    }

    @Override
    public String toString() {
        return "UserLocEntry{" +
                "address='" + address + '\'' +
                ", date='" + date + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", epoch=" + epoch +
                '}';
    }
}