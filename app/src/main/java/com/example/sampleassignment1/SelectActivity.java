package com.example.sampleassignment1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;

public class SelectActivity extends AppCompatActivity {
    MyLocationPlaceMap myLocationPlaceMap;
    ArrayList<MyLocationPlace> myLocations = new ArrayList<>();
    MyLocationPlace myLocation;
    Button selfButton;
    Button otherButton1;
    Button otherButton2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select);

        myLocationPlaceMap = new MyLocationPlaceMap(getApplicationContext(), SelectActivity.this);
        myLocationPlaceMap.requestPermissions();
        myLocationPlaceMap.getLatLngAddress(myLocations);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            String currentUser  = extras.getString("selectedUser");
            String otherUser1 = extras.getString("otherUser1");
            String otherUser2 = extras.getString("otherUser2");

            selfButton = findViewById(R.id.buttonWhereAmI);
            otherButton1 = findViewById(R.id.buttonWhereIsUser2);
            otherButton2 = findViewById(R.id.buttonWhereIsUser3);
            selfButton.setText("Where am I ("+ currentUser + ")?");
            otherButton1.setText("Where is " + otherUser1 + "?");
            otherButton2.setText("Where is " + otherUser2 + "?");
        }
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