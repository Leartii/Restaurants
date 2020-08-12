package com.example.restaurants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.example.restaurants.Data.MenuRecyclerAdapter;
import com.example.restaurants.Model.Menu_Items;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RestaurantDetails extends AppCompatActivity {

    private RecyclerView recyclerView;
    private CircleImageView img;
    private TextView titleV, tablesV, seatsV, start_closeV, availableV;
    private RecyclerView.Adapter adapter;
    private FirebaseAuth mAuth;
    private DatabaseReference dbref;
    private List<Menu_Items> menu_items;
    private Context context;
    private String Id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        setup();

        try {
            Bundle extras = getIntent().getExtras();
            Id = extras.getString("ID");
        }catch (Exception e){
            e.printStackTrace();
        }

        DatabaseReference db = FirebaseDatabase.getInstance().getReference().child("Users").child(Id);
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                titleV.setText(dataSnapshot.child("name").getValue().toString());
                tablesV.setText("Tables: "+ dataSnapshot.child("Details").child("number_of_tables").getValue().toString());
                seatsV.setText("Chairs: "+dataSnapshot.child("Details").child("chairs_per_table").getValue().toString());
                start_closeV.setText("Open: "+dataSnapshot.child("Details").child("openning_time").getValue().toString()+"-"
                        +dataSnapshot.child("Details").child("closing_time").getValue().toString());
                availableV.setText(dataSnapshot.child("free_tables").getValue().toString()+"/"
                        +dataSnapshot.child("Details").child("number_of_tables").getValue().toString()+" free tables");
                Picasso.with(getApplicationContext()).load(dataSnapshot.child("imageLink").getValue().toString()).placeholder(android.R.drawable.ic_btn_speak_now).into(img);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        context = this;
        menu_items = new ArrayList<>();
        recyclerView = (RecyclerView) findViewById(R.id.dets_recycler);
        mAuth = FirebaseAuth.getInstance();
        dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(Id).child("Menu");
        recyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL,false));
        adapter = new MenuRecyclerAdapter(context, menu_items);
        recyclerView.setAdapter(adapter);
        dbref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                menu_items.clear();
                for(DataSnapshot data : dataSnapshot.getChildren()){

                    Menu_Items menu = new Menu_Items();
                    menu.setParent_key(data.getKey());
                    menu.setImage(data.child("img").getValue().toString());
                    menu.setName(data.child("name").getValue().toString());
                    menu.setPrice(data.child("price").getValue().toString());

                    menu_items.add(menu);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setup() {
        titleV = (TextView) findViewById(R.id.dets_title);
        tablesV = (TextView) findViewById(R.id.dets_tables);
        seatsV = (TextView) findViewById(R.id.dets_seats);
        start_closeV = (TextView) findViewById(R.id.dets_start_close);
        availableV = (TextView) findViewById(R.id.dets_available);
        img = (CircleImageView) findViewById(R.id.det_circ);
    }
}

