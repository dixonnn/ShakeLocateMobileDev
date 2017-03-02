package com.example.cdixon8.shakelocatemobiledev;

import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Bundle coords;
    private double lat;
    private double lng;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        coords = getIntent().getExtras();
        if (coords != null) {
            lat = coords.getDouble("lat");
            lng = coords.getDouble("long");
        } else {
            Log.e("MapActivity","Error getting coordinates");
        }

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Create a marker using the bundled coordinates from the last activity
        LatLng currentLoc = new LatLng(lat, lng);
        mMap.addMarker(new MarkerOptions().position(currentLoc).title("Marker at your current location."));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLoc, 15));
    }
}
