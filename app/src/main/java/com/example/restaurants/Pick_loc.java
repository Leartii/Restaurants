package com.example.restaurants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.example.restaurants.Details;
import com.example.restaurants.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class Pick_loc extends AppCompatActivity implements OnMapReadyCallback {
    private GoogleMap mMap;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private Button butoni;
    private Marker marker, curr_locmarker;
    private static final int REQUEST_CODE = 101;
    private FirebaseAuth mAuth;
    private DatabaseReference dbref;
    private String key;
    private LatLng prevLoc;

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseAuth auth = FirebaseAuth.getInstance();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild("Location")){
                    try{
                        Log.d("Inside  ", "hasLocation" );
                        prevLoc = new LatLng(Double.valueOf(dataSnapshot.child("Location").child("Latitude").getValue().toString()),
                                Double.valueOf(dataSnapshot.child("Location").child("Longitude").getValue().toString()));
                        Log.d("prevLoc", prevLoc.toString());
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                }
                else{
                    Log.d("has map", "no");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_loc_picker);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.mapi);
        mapFragment.getMapAsync((this));
        try{
            Bundle extras = getIntent().getExtras();
            key =  extras.getString("key");

        }catch (Exception e){
            e.printStackTrace();
        }


        try {
            Log.d("prevLoc ", prevLoc.toString());
        }catch (Exception e){
            e.printStackTrace();
        }

        butoni = (Button) findViewById(R.id.pick_button);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        mAuth = FirebaseAuth.getInstance();
        dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Location");

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                mMap.clear();
                LatLng lt = new LatLng(location.getLatitude(), location.getLongitude());

                if(prevLoc != null && prevLoc == lt) {
                    curr_locmarker = mMap.addMarker(new MarkerOptions().position(lt).title("Your Previous Location"));
                    curr_locmarker.setTag(0);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(lt));
                }
                else if(prevLoc != null && prevLoc != lt){
                    curr_locmarker = mMap.addMarker(new MarkerOptions().position(lt).title("Your Location"));
                    curr_locmarker.setTag(0);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(lt));
                    Location loc1= new Location("");
                    loc1.setLatitude(prevLoc.latitude);
                    loc1.setLongitude(prevLoc.longitude);

                    Location loc2 = new Location("");
                    loc2.setLongitude(lt.longitude);
                    loc2.setLatitude(lt.latitude);
                    mMap.addMarker(new MarkerOptions().position(prevLoc).title("Previous Location"));
                }
                else{
                    curr_locmarker = mMap.addMarker(new MarkerOptions().position(lt).title("Your Location"));
                    curr_locmarker.setTag(0);
                    mMap.moveCamera(CameraUpdateFactory.newLatLng(lt));
                }
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };

        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED //&& checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {
            Log.d("place", "check self premission");
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        } else {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 6000, 0, locationListener);
        }

        butoni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (marker != null) {
                    LatLng mlatlng = marker.getPosition();
                    Map mhash = new HashMap();
                    mhash.put("Latitude", mlatlng.latitude);
                    mhash.put("Longitude" , mlatlng.longitude);
                    dbref.setValue(mhash).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Unsuccessfull", Toast.LENGTH_LONG).show();
                            }
                            else{
                                if(key != null){
                                    Toast.makeText(getApplicationContext(), "Successfully stored", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), Home.class));

                                } else{
                                Toast.makeText(getApplicationContext(), "Successfully stored", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), Details.class));
                                }
                            }
                        }
                    });
                    Toast.makeText(getApplicationContext(), "latitude: " + mlatlng.latitude + ", longitude: " + mlatlng.longitude, Toast.LENGTH_LONG).show();
                }
                else if(curr_locmarker != null){
                    LatLng crrLatlng = curr_locmarker.getPosition();
                    Map mhash = new HashMap();
                    mhash.put("Latitude", crrLatlng.latitude);
                    mhash.put("Longitude", crrLatlng.longitude);
                    dbref.setValue(mhash).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Unsuccessfull", Toast.LENGTH_LONG).show();
                            }
                            else{
                                if(key != null){
                                    Toast.makeText(getApplicationContext(), "Successfully changed", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), Home.class));
                                }else{
                                    Toast.makeText(getApplicationContext(), "Successfully stored", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), Details.class));
                                }

                            }
                        }
                    });
                    Toast.makeText(getApplicationContext(), "latitude: " + crrLatlng.latitude + ", longitude: " + crrLatlng.longitude, Toast.LENGTH_LONG).show();

                }

                else {

                    Toast.makeText(getApplicationContext(), "Please pick the location of your restaurant", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        Log.d("place", "on map ready");
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                marker = mMap.addMarker(new MarkerOptions().position(latLng).title("This location"));
                marker.setTag(0);

            }
        });

    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 6000, 0, locationListener);
                }
        else {
                    Toast.makeText(getApplicationContext(), "We can't show you your location without your premission", Toast.LENGTH_LONG).show();
                }
        }



}
