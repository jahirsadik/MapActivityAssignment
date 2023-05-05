package com.example.sampleassignment1;

import static com.example.sampleassignment1.DatabaseHelper.fDatabase;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.SupportStreetViewPanoramaFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.maps.android.PolyUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;
import java.util.TreeSet;

public class RouteActivity extends AppCompatActivity implements OnMapReadyCallback, OnStreetViewPanoramaReadyCallback {

    String fromUser;
    String toUser;

    private GoogleMap mMap;
    SupportMapFragment mapFragment;
    SupportStreetViewPanoramaFragment streetViewPanoramaFragment;
    StreetViewPanorama mStreetViewPanorama;
    boolean showMap = true;
    MyLocationPlaceMap myLocationPlaceMap;
    private final TreeSet<UserLocEntry> fromlocations = new TreeSet<>(new UserLocEntrySorter());
    private final TreeSet<UserLocEntry> toLocations = new TreeSet<>(new UserLocEntrySorter());
    private LatLng clickedLatLng;

    TextView distanceTv;
    TextView drivingTimeTv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fromUser = extras.getString("fromUser");
            toUser = extras.getString("toUser");
        }

        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.routeMap);
        mapFragment.getMapAsync(this);

        streetViewPanoramaFragment =
                (SupportStreetViewPanoramaFragment)
                        getSupportFragmentManager().findFragmentById(R.id.routeStreetView);
        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);

        mapFragment.getView().bringToFront();

        myLocationPlaceMap = new MyLocationPlaceMap(getApplicationContext(), RouteActivity.this);

        distanceTv = findViewById(R.id.textViewDistance);
        drivingTimeTv = findViewById(R.id.textViewDrivingTime);

        fDatabase.child(fromUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DatabaseHelper.updateLocations(fromlocations, snapshot);
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

        fDatabase.child(toUser).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                DatabaseHelper.updateLocations(toLocations, snapshot);
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
                LatLng from;
                LatLng to;

                if (!fromlocations.isEmpty() && !toLocations.isEmpty()) {
                    from = new LatLng(fromlocations.first().latitude, fromlocations.first().longitude);
                    to = new LatLng(toLocations.first().latitude, toLocations.first().longitude);
                    drawRoute(from, to);
                    LatLngBounds latLngBounds = new LatLngBounds.Builder().include(from).include(to).build();
                    mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 0));
                }

                if (!fromlocations.isEmpty()) {
                    for (UserLocEntry loc : fromlocations) {
                        mMap.addMarker(
                                new MarkerOptions()
                                        .title(fromUser)
                                        .snippet("Latitude: " + loc.latitude + ", Longitude: " + loc.longitude +
                                                "\nAddress: " + loc.address)
                                        .position(new LatLng(loc.latitude, loc.longitude))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE))
                        );
                    }
                }
                if (!toLocations.isEmpty()) {
                    for (UserLocEntry loc : toLocations) {
                        mMap.addMarker(
                                new MarkerOptions()
                                        .title(toUser)
                                        .snippet("Latitude: " + loc.latitude + ", Longitude: " + loc.longitude +
                                                "\nAddress: " + loc.address)
                                        .position(new LatLng(loc.latitude, loc.longitude))
                                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                        );
                    }
                }
            }
        });

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(@NonNull Marker marker) {
                showMap = true;
                streetViewPanoramaFragment.getView().bringToFront();
                clickedLatLng = marker.getPosition();
                mStreetViewPanorama.setPosition(clickedLatLng);
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
        mStreetViewPanorama.setPosition(clickedLatLng);
    }

    public void drawRoute(LatLng origin, LatLng destination) {
        String url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + origin.latitude + "," + origin.longitude + "&destination="
                + destination.latitude + "," + destination.longitude
                + "&mode=driving&key=AIzaSyCh2PIXXDw8PwJxvWACBtEUEYpHBVu9g90";

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // Parse the JSON response and draw the route on the map
                        PolylineOptions polylineOptions = new PolylineOptions();
                        polylineOptions.color(Color.RED);
                        polylineOptions.width(5);

                        Log.d("request", url);
                        Log.d("response", response.toString());

                        JSONArray routes = null;
                        try {
                            routes = response.getJSONArray("routes");
                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }
                        for (int i = 0; i < routes.length(); i++) {
                            try {
                                JSONObject route = routes.getJSONObject(i);
                                JSONObject overviewPolyline = route.getJSONObject("overview_polyline");
                                String points = overviewPolyline.getString("points");
                                List<LatLng> path = PolyUtil.decode(points);
                                polylineOptions.addAll(path);
                            } catch (JSONException e) {
                                Log.e("routing", "JSON parsing error has occured");
                            }
                        }
                        mMap.addPolyline(polylineOptions);
                        try {
                            String distance = routes.getJSONObject(routes.length() - 1).getJSONArray("legs")
                                    .getJSONObject(0)
                                    .getJSONObject("distance")
                                    .getString("text");

                            String duration = routes.getJSONObject(routes.length() - 1).getJSONArray("legs")
                                    .getJSONObject(0)
                                    .getJSONObject("duration")
                                    .getString("text");

                            distanceTv.setText("Distance: " + distance);
                            drivingTimeTv.setText("Driving time: " + duration);

                        } catch (Exception e) {
                            Log.e("routing", "Distance/Duration parsing error has occured");
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Handle the error
                    }
                });
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(jsonObjectRequest);
    }

    public void showRouteMapStreetView(View view) {
        if (showMap) {
            showMap = false;
            mapFragment.getMapAsync(this);
            mapFragment.getView().bringToFront();
        }
    }
}