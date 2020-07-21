package com.example.restaurants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.restaurants.Data.RestaurantsRecyclerAdapter;
import com.example.restaurants.Model.Restaurants;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerView.Adapter adapter;
    private List<Restaurants> restaurantsList;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener stateListener;
    private String curr_user;
    private LocationListener locationListener;
    private LocationManager locationManager;
    private DatabaseReference dbref;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(!task.isSuccessful()){
                    Toast.makeText(getApplicationContext(), "anonymous sign in was unsuccessfull", Toast.LENGTH_LONG).show();
                }
                else {
                    Toast.makeText(getApplicationContext(), "anonymous sign in was successfull", Toast.LENGTH_LONG).show();
                }
            }
        });

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        restaurantsList = new ArrayList<>();
        dbref = FirebaseDatabase.getInstance().getReference().child("Users");

        recyclerView = (RecyclerView) findViewById(R.id.mainRecycler);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        context = this;
        adapter = new RestaurantsRecyclerAdapter(context, restaurantsList);
        recyclerView.setAdapter(adapter);



        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(final Location location) {
                dbref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for(DataSnapshot data : dataSnapshot.getChildren()){
                          try{  Location loc = new Location("");
                            loc.setLongitude(Double.valueOf(data.child("Location").child("Longitude").getValue().toString()));
                            loc.setLatitude(Double.valueOf(data.child("Location").child("Latitude").getValue().toString()));
                            if(location.distanceTo(loc) < 1000){
                                Restaurants res = new Restaurants();
                                res.setDistance(String.valueOf(location.distanceTo(loc)));
                                res.setFreeseats(data.child("free_tables").getValue().toString());
                                res.setImageLink(data.child("imageLink").getValue().toString());
                                res.setName(data.child("name").getValue().toString());
                                res.setLatitude(loc.getLatitude());
                                res.setLongitude(loc.getLongitude());

                                restaurantsList.add(res);
                            }
                            Log.d("INside; ","Yes" );
                            if(restaurantsList.isEmpty()){
                                Log.d("Is empty", "Yes");
                            }else {
                                Log.d("Is empty", "No");
                            }
                            adapter.notifyDataSetChanged();
                        }catch (Exception e){
                              e.printStackTrace();
                          }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });



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
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 10, locationListener);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 3000, 10, locationListener);
        }
        else {
            Toast.makeText(getApplicationContext(), "We can't show restaurants around your location without your premission", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        startActivity(new Intent(MainActivity.this, Log_In.class));
        return true;
    }



}
