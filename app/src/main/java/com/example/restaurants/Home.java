package com.example.restaurants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Home extends AppCompatActivity {

    private EditText numTabs;
    private ImageButton inc, dec;
    private Button commit;
    private FirebaseAuth mAuth;
    private DatabaseReference dbref;
    private int max;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        setupUI();
        mAuth = FirebaseAuth.getInstance();
        dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid());

        dbref.child("Details").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
               try {
                   max = Integer.parseInt(dataSnapshot.child("number_of_tables").getValue().toString());
               } catch (Exception e){
                   e.printStackTrace();
               }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        inc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int num = 0;
                    num = Integer.parseInt(numTabs.getText().toString());
                    if (num >= 0 && num < max) {
                    num++;
                    numTabs.setText(String.valueOf(num));
                    } else {
                    Toast.makeText(getApplicationContext(), "Number of free Tables exceeds total number of tables", Toast.LENGTH_LONG).show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        });
        dec.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              try {
                  int num = 0;
                  num = Integer.parseInt(numTabs.getText().toString());
                  if (num > 0 && num <= max) {
                      num--;
                      numTabs.setText(String.valueOf(num));
                  } else {
                      Toast.makeText(getApplicationContext(), "Number of free Tables can't be negative", Toast.LENGTH_LONG).show();
                  }
              }
              catch (Exception e){
                  e.printStackTrace();
              }
            }
        });

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dbref.child("free_tables").setValue(numTabs.getText().toString()).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Commited Successfully", Toast.LENGTH_SHORT).show();
                    }

                });
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.change_d:{
                Intent intent = new Intent(getApplicationContext(), Details.class);
                intent.putExtra("key", "key");
                startActivity(intent);
            }break;

            case R.id.change_loc:{
                Intent intent = new Intent(getApplicationContext(), Pick_loc.class);
                intent.putExtra("key", "key");
                startActivity(intent);
            }break;

            case R.id.change_menu: {
                Intent intent = new Intent(getApplicationContext(), Menu.class);
                intent.putExtra("key", "key");
                startActivity(intent);
            }break;

            case R.id.logout:{
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(), Log_In.class));
                finish();
            }
        }

        return true;
    }

    private void setupUI() {
        numTabs = (EditText) findViewById(R.id.numb_of_tables);
        inc = (ImageButton) findViewById(R.id.increment);
        dec = (ImageButton) findViewById(R.id.decrement);
        commit = (Button) findViewById(R.id.commitB);

        numTabs.setSelected(false);
        numTabs.setFocusable(false);
    }
}
