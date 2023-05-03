package com.example.sampleassignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import java.util.ArrayList;

public class SelectActivity extends AppCompatActivity {
    MyLocationPlaceMap myLocationPlaceMap;
    ArrayList<MyLocationPlace> myLocations = new ArrayList<>();
    MyLocationPlace myLocation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        myLocationPlaceMap = new MyLocationPlaceMap(getApplicationContext(), SelectActivity.this);
        myLocationPlaceMap.requestPermissions();
        myLocationPlaceMap.getLatLngAddress(myLocations);
    }

    public void whereAmI (View view) {
        myLocationPlaceMap.getLatLngAddress(myLocations);

        if (myLocations.size() > 0) {
            myLocation = myLocations.get(0);
            myLocations.clear();

            Intent intent = new Intent(this, MapsActivity.class);
            intent.putExtra("lat", myLocation.getLatitude());
            intent.putExtra("lng", myLocation.getLongitude());
            intent.putExtra("addr", myLocation.getAddress());
            startActivity(intent);
        }
    }

}