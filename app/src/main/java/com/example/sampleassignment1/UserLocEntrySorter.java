package com.example.sampleassignment1;

import java.util.Comparator;

public class UserLocEntrySorter implements Comparator<UserLocEntry> {

    @Override
    public int compare(UserLocEntry userLocEntry, UserLocEntry t1) {
        return t1.epoch.compareTo(userLocEntry.epoch);
    }
}