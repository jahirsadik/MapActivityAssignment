package com.example.sampleassignment1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SelectActivity extends AppCompatActivity {
    MyLocationPlaceMap myLocationPlaceMap;

    private String currentUser;
    private String otherUser1;
    private String otherUser2;
    private SharedPreferences sharedPref;

    ArrayList<MyLocationPlace> myLocations = new ArrayList<>();
    MyLocationPlace myLocation;
    Button selfButton;
    Button otherButton1;
    Button otherButton2;
    private static final String USER_1 = "user1";
    private static final String USER_2 = "user2";
    private static final String USER_3 = "user3";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        setContentView(R.layout.activity_select);

        myLocationPlaceMap = new MyLocationPlaceMap(getApplicationContext(), SelectActivity.this);
        myLocationPlaceMap.requestPermissions();
        myLocationPlaceMap.getLatLngAddress(myLocations);

        Bundle extras = getIntent().getExtras();

        if (extras != null) {
            currentUser  = extras.getString("selectedUser");
            otherUser1 = extras.getString("otherUser1");
            otherUser2 = extras.getString("otherUser2");

            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString(USER_1, currentUser);
            editor.putString(USER_2, otherUser1);
            editor.putString(USER_3, otherUser2);
            editor.apply();
        }

        if (savedInstanceState != null) {
            currentUser  = savedInstanceState.getString(USER_1);
            otherUser1 = savedInstanceState.getString(USER_2);
            otherUser2 = savedInstanceState.getString(USER_3);
        } else {
            currentUser = sharedPref.getString(USER_1, currentUser);
            otherUser1 = sharedPref.getString(USER_2, otherUser1);
            otherUser2 = sharedPref.getString(USER_3, otherUser2);
        }

        selfButton = findViewById(R.id.buttonWhereAmI);
        otherButton1 = findViewById(R.id.buttonWhereIsUser2);
        otherButton2 = findViewById(R.id.buttonWhereIsUser3);
        selfButton.setText("Where am I ("+ currentUser + ")?");
        otherButton1.setText("Where is " + otherUser1 + "?");
        otherButton2.setText("Where is " + otherUser2 + "?");
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
            intent.putExtra("username", currentUser);
            startActivity(intent);
        }
    }

    public void whereIsUser2(View view) {
        Intent intent = new Intent(this, RouteActivity.class);
        intent.putExtra("fromUser", currentUser);
        intent.putExtra("toUser", otherUser1);
        startActivity(intent);
    }

    public void whereIsUser3(View view) {
        Intent intent = new Intent(this, RouteActivity.class);
        intent.putExtra("fromUser", currentUser);
        intent.putExtra("toUser", otherUser2);
        startActivity(intent);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString(USER_1, currentUser);
        outState.putString(USER_2, otherUser1);
        outState.putString(USER_3, otherUser2);
        super.onSaveInstanceState(outState);
    }
}