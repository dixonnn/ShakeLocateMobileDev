package com.example.cdixon8.shakelocatemobiledev;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.SensorManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.wearable.CapabilityApi;
import com.squareup.seismic.ShakeDetector;

public class MainActivity extends AppCompatActivity
        implements ShakeDetector.Listener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    public String latText;
    public String longText;
    private int REQUEST_LOCATION = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Button to display pin on map only visible after coordinates gathered
        Button display = (Button) findViewById(R.id.display);
        display.setVisibility(View.INVISIBLE);

        // prep google play services
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }

        // prep shake function
        SensorManager sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        ShakeDetector sd = new ShakeDetector(this);
        sd.start(sensorManager);

    }

    public void hearShake() {
        Toast.makeText(this, "Shook!", Toast.LENGTH_SHORT).show();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(Bundle conHint) {

        // If "Yes" hasn't been given by the user, request permissions
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
            mGoogleApiClient.disconnect();
        } else {
            // Permission has already been granted, we're good to go
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

            if (mLastLocation != null) {
                TextView latRes = (TextView) findViewById(R.id.latRes);
                TextView longRes = (TextView) findViewById(R.id.longRes);
                Button display = (Button) findViewById(R.id.display);

                display.setOnClickListener(new View.OnClickListener() {
                    public void onClick(View v) {
                        Intent i = new Intent(MainActivity.this, MapActivity.class);
                        i.putExtra("lat", mLastLocation.getLatitude());
                        i.putExtra("long", mLastLocation.getLongitude());
                        startActivity(i);
                    }
                });

                latText = String.valueOf(mLastLocation.getLatitude());
                longText = String.valueOf(mLastLocation.getLongitude());

                latRes.setText(latText);
                longRes.setText(longText);
                display.setVisibility(View.VISIBLE);

            } else {
                Toast.makeText(this, "No location detected.", Toast.LENGTH_SHORT).show();
            }

            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        Log.i("MainActivity", "Connection failed: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }

    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if(grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                mGoogleApiClient.connect();
            } else {
                // Permission was denied
            }
        }
    }

    @Override
    public void onConnectionSuspended(int cause) {
        Log.i("MainActivity", "Connection suspended");
    }

}