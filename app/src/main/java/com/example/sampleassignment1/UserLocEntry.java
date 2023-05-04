package com.example.sampleassignment1;

import androidx.annotation.NonNull;

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

    @NonNull
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

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof UserLocEntry)) {
            return false;
        }

        UserLocEntry other = (UserLocEntry) o;

        return address.equals(other.address) && date.equals(other.date) && longitude == other.longitude && latitude == other.latitude && epoch.equals(other.epoch);
    }
}