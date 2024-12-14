package com.employee_attendance_management.eam;


import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import androidx.core.view.GravityCompat;

import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import de.hdodenhof.circleimageview.CircleImageView;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    CircleImageView civMenu;
    TextView tvCheckInOut;
    private DatabaseHelper databaseHelper;

    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST = 1;


//    private static final double OFFICE_LAT = 12.971598; // Example: Replace with your office latitude
    private double OFFICE_LAT = 25.5948824; // Example: Replace with your office latitude
    //    private static final double OFFICE_LNG = 77.594566; // Example: Replace with your office longitude
    private double OFFICE_LNG = 85.1497289; // Example: Replace with your office longitude
    private static final float RADIUS_IN_METERS = 1000; // 1 km radius
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    double userLatitude;
    double userLongitude;
    String userName;

    DrawerLayout drawerLayout;
    NavigationView navigationView ;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        SharedPreferences userDetails = this.getSharedPreferences("CNB", MODE_PRIVATE);
        userName = userDetails.getString("userName", "NONAME");
        OFFICE_LAT = Double.parseDouble(userDetails.getString("officeLatitude", "25.5948824"));
        OFFICE_LNG = Double.parseDouble(userDetails.getString("officeLongitude", "85.1497289"));

        databaseHelper = new DatabaseHelper(this);

        tvCheckInOut = findViewById(R.id.tvCheckInOut);

        String currentDate = new SimpleDateFormat("d MMMM yyyy", Locale.getDefault()).format(new Date());

        if (databaseHelper.isAttendanceCompleted(userName, currentDate)) {

           tvCheckInOut.setText("Check In");

        } else if (databaseHelper.isCheckedIn(userName, currentDate)) {


            tvCheckInOut.setText("Check Out");



        }
//        else {
//
//            if (result != -1) {
//                Toast.makeText(MainActivity.this, "Checked In Successfully", Toast.LENGTH_SHORT).show();
//
//            } else {
//                Toast.makeText(MainActivity.this, "Check-in Failed", Toast.LENGTH_SHORT).show();
//
//
//            }
//        }
         drawerLayout = findViewById(R.id.drawer_layout);
         navigationView = findViewById(R.id.nav_view);
         civMenu = findViewById(R.id.civMenu);

        civMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

         navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
             @Override
             public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                 int itemId = item.getItemId();
                 if (itemId == R.id.nav_attendance_record) {
                     Intent intent = new Intent(MainActivity.this, officeSettingActivity.class);
                     startActivity(intent);
                 } else if (itemId == R.id.nav_settings) {
                     Intent intent = new Intent(MainActivity.this, employeeRecordsActivity.class);
                     startActivity(intent);
                 }
                 drawerLayout.closeDrawer(GravityCompat.START);
                 return true;
             }
         });




        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        mMap = googleMap;


        LatLng officeLocation = new LatLng(OFFICE_LAT, OFFICE_LNG);
        mMap.addMarker(new MarkerOptions().position(officeLocation).title("Office Location").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)));
        mMap.addCircle(new CircleOptions()
                .center(officeLocation)
                .radius(RADIUS_IN_METERS)
                .strokeColor(0x5500ff00)
                .fillColor(0x2200ff00));

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(officeLocation, 15));

        checkLocationPermission();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request Location Permissions
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_PERMISSION_REQUEST);

        } else {
//            fetchUserLocation();
            fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        double userLat = location.getLatitude();
                        double userLng = location.getLongitude();
                        LatLng userLocation = new LatLng(userLat, userLng);
                        Log.e("USER LOCATION", (String.valueOf(userLng)) + "," + (String.valueOf(userLat)));

                        // Add a marker for the user's location
                        mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));


                        // Add a marker for the user's location with a blue icon
                        mMap.addMarker(new MarkerOptions()
                                .position(userLocation)
                                .title("Your Location")
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));


                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

                        LatLng officeLocation = new LatLng(OFFICE_LAT, OFFICE_LNG);
                        List<PatternItem> pattern = Arrays.asList(
                                new Dot(), // Single dot
                                new Gap(10) // Gap between dots
                        );


                        Polyline polyline = mMap.addPolyline(new PolylineOptions()
                                .add(userLocation, officeLocation)
                                .color(0xFF0000FF) // Blue color
                                .width(5f) // Line width
                                .pattern(pattern));

                        // Calculate distance
                        float[] results = new float[1];
                        Location.distanceBetween(userLat, userLng, OFFICE_LAT, OFFICE_LNG, results);
                        float distanceInMeters = results[0];


                        tvCheckInOut.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                if (distanceInMeters <= RADIUS_IN_METERS) {

                                    userLatitude = userLat;
                                    userLongitude = userLng;
                                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                                        startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                                    }

//                                    Intent intent = new Intent(MainActivity.this, attendanceFormActivity.class);
//                                    startActivity(intent);
                                } else {
                                    // Out of range
                                    Toast.makeText(MainActivity.this, "You are not in the office", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });


                    } else {
                        Toast.makeText(MainActivity.this, "Unable to fetch location. Try again.", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            // Pass the imageBitmap to the next activity
            Intent intent = new Intent(MainActivity.this, attendanceFormActivity.class);
            intent.putExtra("imageBitmap", imageBitmap);
            intent.putExtra("userLongitude", userLongitude);
            intent.putExtra("userLatitude", userLatitude);
            intent.putExtra("userName", userName);
            startActivity(intent);
        }
    }

    private void fetchUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    double userLat = location.getLatitude();
                    double userLng = location.getLongitude();
                    LatLng userLocation = new LatLng(userLat, userLng);

                    // Add a marker for the user's location
                    mMap.addMarker(new MarkerOptions().position(userLocation).title("Your Location"));
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(userLocation, 15));

                    // Calculate distance
                    float[] results = new float[1];
                    Location.distanceBetween(userLat, userLng, OFFICE_LAT, OFFICE_LNG, results);
                    float distanceInMeters = results[0];

                    if (distanceInMeters <= RADIUS_IN_METERS) {
                        // Within 1 km, navigate to next activity
//                        Intent intent = new Intent(MainActivity.this, NextActivity.class);
//                        startActivity(intent);
                    } else {
                        // Out of range
                        Toast.makeText(MainActivity.this, "You are not in the office", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Please turn on GPS location.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchUserLocation();
            } else {
                Toast.makeText(this, "Location permission denied.", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

