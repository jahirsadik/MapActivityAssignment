package com.example.sampleassignment1;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.TreeSet;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, OnStreetViewPanoramaReadyCallback {

    private DatabaseReference mDatabase;
    String username;


    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    SupportStreetViewPanoramaFragment streetViewPanoramaFragment;
    StreetViewPanorama mStreetViewPanorama;

    Double latitude;
    Double longitude;
    LatLng latLngRed, latLngBlue;
    String address;
    TextView textViewAddress;
    TextView textViewLatitude;
    TextView textViewLongitude;
    Button btnMapStreetView;
    PlacesClient placesClient;
    Marker redMarker, blueMarker;
    boolean showMap = true;
    MyLocationPlaceMap myLocationPlaceMap;
    ArrayList<MyLocationPlace> myLocations = new ArrayList<>();
    MyLocationPlace myLocation;
    private final TreeSet<UserLocEntry> locations = new TreeSet<>(new UserLocEntrySorter());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
//        binding = ActivityMapsBinding.inflate(getLayoutInflater());
//        setContentView(binding.getRoot());
        setTitle("My Location Details");
        btnMapStreetView = findViewById(R.id.buttonMapStreetView);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            latitude = extras.getDouble("lat");
            longitude = extras.getDouble("lng");
            latLngRed = new LatLng(latitude, longitude);
            address = extras.getString("addr");

            textViewAddress = findViewById(R.id.textViewStreetAddress);
            textViewAddress.setText("Address: " + address);
            textViewLatitude = findViewById(R.id.textViewLatitude);
            textViewLatitude.setText("Latitude: " + latitude);
            textViewLongitude = findViewById(R.id.textViewLongitude);
            textViewLongitude.setText("Longitude: " + longitude);
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(R.id.streetView);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        mapFragment.getView().bringToFront();

        myLocationPlaceMap = new MyLocationPlaceMap(getApplicationContext(), MapsActivity.this);


        mDatabase = FirebaseDatabase.getInstance(DatabaseHelper.DATABASE_URL).getReference();
        if (extras != null) {
            username = extras.getString("username");
            LocalDateTime time = LocalDateTime.now();
            UserLocEntry userLocEntry = new UserLocEntry(address, time, latitude, longitude);

            Log.d("localdatetime", LocalDateTime.now().toString());
            String latStr = Integer.toString((int)(latitude * 1000));
            String longStr = Integer.toString((int)(longitude * 1000));
            String latlong = latStr + longStr;
            mDatabase.child(username).child(latlong).setValue(userLocEntry);
        }

        mDatabase.child(username).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                locations.clear();
                UserLocEntry temp = new UserLocEntry(
                        snapshot.child("address").getValue().toString(),
                        Instant.ofEpochSecond(Long.parseLong(snapshot.child("epoch").getValue().toString())).atOffset(ZoneOffset.UTC).toLocalDateTime(),
                        Double.parseDouble(snapshot.child("latitude").getValue().toString()),
                        Double.parseDouble(snapshot.child("longitude").getValue().toString())
                );
                locations.add(temp);
                for (UserLocEntry loc: locations) {
                    Log.d("aisi", loc.toString());
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @SuppressLint("PotentialBehaviorOverride")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.clear();

        mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                boolean isFirst = true;
                if (!locations.isEmpty()) {
                    for (UserLocEntry loc : locations) {
                        mMap.addMarker(
                                new MarkerOptions()
                                        .title("Show Surroundings")
                                        .snippet("Latitude: " + loc.latitude + ", Longitude: " + loc.longitude +
                                                "\nAddress: " + loc.address)
                                        .position(new LatLng(loc.latitude, loc.longitude))
                        );
                        if (isFirst) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(loc.latitude, loc.longitude), 14));
                            isFirst = false;
                        }
                    }
                }
                redMarker = mMap.addMarker(new MarkerOptions()
                        .title("Show Surroundings")
                        .snippet("Latitude: " + latitude + ", Longitude: " + longitude +
                                "\nAddress: " + address)
                        .position(latLngRed)
                );
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngRed, 18));
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(@NonNull Marker marker) {
                if (marker.equals(redMarker)) {
                    streetViewPanoramaFragment.getView().bringToFront();
                    btnMapStreetView.setText("Show map");
                    showMap = false;
                    return true;
                } else {
                    return false;
                }

            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                if (!marker.getId().equals(redMarker.getId())) {
                    streetViewPanoramaFragment.getView().bringToFront();
                    latLngBlue = marker.getPosition();
                    mStreetViewPanorama.setPosition(latLngBlue);
                }
            }
        });

        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                return null;
            }
            @Override
            public View getInfoContents(Marker marker) {
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_window, null);
                TextView title = (TextView) infoWindow.findViewById(R.id.textViewTitle);
                TextView snippet = (TextView) infoWindow.findViewById(R.id.textViewSnippet);
                ImageView image = (ImageView) infoWindow.findViewById(R.id.imageView);

                if (marker.getTitle() != null && marker.getSnippet() != null) {
                    title.setText(marker.getTitle());
                    snippet.setText(marker.getSnippet());
                } else {
                    title.setText("No info available");
                    snippet.setText("No location available");
                }
                image.setImageDrawable(getResources()
                        .getDrawable(R.drawable.blue_marker, getTheme()));
                return infoWindow;
            }
        });
    }

    @Override
    public void onStreetViewPanoramaReady(@NonNull StreetViewPanorama streetViewPanorama) {
        mStreetViewPanorama = streetViewPanorama;
        mStreetViewPanorama.setPosition(latLngRed);
    }

    public void showNearby(View view) {
        mapFragment.getView().bringToFront();
        myLocationPlaceMap.getNearbyPlaces(mMap, "YOUR_API_KEY");
    }

    public void showMapStreetView(View view) {
        showMap = !showMap;
        if (showMap) {
            mapFragment.getMapAsync(this);
            mapFragment.getView().bringToFront();
            btnMapStreetView.setText("Show Street View");
        } else {
            streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
            streetViewPanoramaFragment.getView().bringToFront();
            btnMapStreetView.setText("Show Map");
        }
    }

}