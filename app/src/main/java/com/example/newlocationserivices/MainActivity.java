package com.example.newlocationserivices;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;

public class MainActivity extends AppCompatActivity {
    private TextView latitude;
    private TextView longitude;
    private TextView altitude;
    private TextView speed;
    private TextView accuracy;
    private TextView sensortype;
    private TextView updatesonandoff;
    private ToggleButton switchesgpspowerbalanced;
    private ToggleButton locationonoff;
    private ToggleButton livelocation;
    private Button MapView;
    public FusedLocationProviderClient fusedLocationProviderClient;
    private static final int MY_PERMISSION_REQUEST_FINE_LOCATION = 101;
    private LocationRequest locationRequest;
    private LocationCallback locationCallback;
    private boolean updatesOn = false;
    public long lat;
    public long longi;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //intialize textview
        latitude = findViewById(R.id.tvlatitude);
        longitude = findViewById(R.id.tvlongitude);
        altitude = findViewById(R.id.tvaltitude);
        speed = findViewById(R.id.tvspeed);
        sensortype = findViewById(R.id.tvsensor);
        updatesonandoff = findViewById(R.id.tvlupdate);
        accuracy = findViewById(R.id.tvaccuracy);
        switchesgpspowerbalanced = findViewById(R.id.tvgps);
        locationonoff = findViewById(R.id.tvlocationonoff);
        MapView = findViewById(R.id.locationonmap);
        locationRequest = new LocationRequest();
        locationRequest.setInterval(7500); //that value is in seconds i-e 1 sec=1000 milli seconds
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);

       MapView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View view)
           {
               startActivity(new Intent(MainActivity.this,MapsActivity.class));
           }
       });


        switchesgpspowerbalanced.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchesgpspowerbalanced.isChecked()) {
                    //USING GPS
                    sensortype.setText("GPS");
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                } else {
                    //using power balances
                    sensortype.setText("CELL TOWER AND WIFI");
                    locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                }
            }
        });
        locationonoff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (locationonoff.isChecked()) {
                    //location on
                    updatesonandoff.setText("ON");
                    updatesOn = true;
                    startLocationUpdates();
                } else {
                    updatesonandoff.setText("OFF");
                    updatesOn = false;
                    stopLocationUpdates();
                }
            }
        });


        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        latitude.setText(String.valueOf(location.getLatitude()));
                        accuracy.setText(String.valueOf(location.getAccuracy()));
                        longitude.setText(String.valueOf(location.getLongitude()));
                        if (location.hasAltitude()) {
                            altitude.setText(String.valueOf(location.getAltitude()));
                        } else {
                            altitude.setText("No Altitude Available");
                        }
                        if (location.hasSpeed()) {
                            speed.setText(String.valueOf(location.getSpeed()) + "m/s");
                        } else {
                            speed.setText("No Speed Available");
                        }
                    }
                }

            });
        } else {
            //request permission
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
        }
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                super.onLocationResult(locationResult);
                for (Location location : locationResult.getLocations()) {
                    //update UI with location Data
                    if (location != null) {

                        latitude.setText(String.valueOf(location.getLatitude()));
                        accuracy.setText(String.valueOf(location.getAccuracy()));
                        longitude.setText(String.valueOf(location.getLongitude()));
                        if (location.hasAltitude()) {
                            altitude.setText(String.valueOf(location.getAltitude()));
                        } else {
                            altitude.setText("No Altitude Available");
                        }
                        if (location.hasSpeed()) {
                            speed.setText(String.valueOf(location.getSpeed()) + "m/s");
                        } else {
                            speed.setText("No Speed Available");
                        }
                    }

                }
            }
        };

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MY_PERMISSION_REQUEST_FINE_LOCATION:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //permission already granted so nothing to do
                } else {
                    Toast.makeText(getApplicationContext(), "THIS APP REQUIRES PERMISSION TO BE GRANTED", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (updatesOn)
            startLocationUpdates();
    }
    private int startLocationUpdates() {
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, null);
        } else {
            requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MY_PERMISSION_REQUEST_FINE_LOCATION);
        }
        return 0;
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    private void stopLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }


}


