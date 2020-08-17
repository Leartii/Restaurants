package com.example.restaurants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.restaurants.R;
import com.example.restaurants.Res_home;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class Details extends AppCompatActivity {
    private EditText Eopen, Eclose, Etable, Echairs;
    private String open,close,table,chairs;
    private Button submitB;
    private FirebaseAuth mAuth;
    private DatabaseReference dbref;
    private String key;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_details);
        setupUI();
        try {
            Bundle extras = getIntent().getExtras();
            key = extras.getString("key");
        }catch (Exception e){
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();

        final int Hour = calendar.get(Calendar.HOUR_OF_DAY);
        final int Minute = calendar.get(Calendar.MINUTE);

        Eopen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(Details.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                       if(minute == 0){
                        Eopen.setText(hourOfDay+":"+minute+"0");
                       }
                       else if(hourOfDay == 0){
                           Eopen.setText("0"+hourOfDay+":"+minute);
                       }
                       else if(hourOfDay == 0 && minute == 0){
                           Eopen.setText("0"+hourOfDay+":"+minute+"0");
                       }
                       else{
                           Eopen.setText(hourOfDay+":"+minute);
                       }
                    }
                }, Hour, Minute, true);
                timePickerDialog.show();
            }
        });

        Eclose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TimePickerDialog timePickerDialog = new TimePickerDialog(Details.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        if(minute == 0){
                            Eclose.setText(hourOfDay+":"+minute+"0");
                        }
                        else if(hourOfDay == 0){
                            Eclose.setText("0"+hourOfDay+":"+minute);
                        }
                        else if(hourOfDay == 0 && minute == 0){
                            Eclose.setText("0"+hourOfDay+":"+minute+"0");
                        }
                        else{
                            Eclose.setText(hourOfDay+":"+minute);
                        }
                    }
                }, Hour, Minute, true);
                timePickerDialog.show();
            }
        });

        mAuth = FirebaseAuth.getInstance();
        dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Details");

        if(key != null){
            submitB.setText("Done");
            dbref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Eopen.setText(dataSnapshot.child("openning_time").getValue().toString());
                    Eclose.setText(dataSnapshot.child("closing_time").getValue().toString());
                    Echairs.setText(dataSnapshot.child("chairs_per_table").getValue().toString());
                    Etable.setText(dataSnapshot.child("number_of_tables").getValue().toString());
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }

        submitB.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open = Eopen.getText().toString();
                close = Eclose.getText().toString();
                table = Etable.getText().toString();
                chairs = Echairs.getText().toString();

                if(!(open.isEmpty() && close.isEmpty() && table.isEmpty() && chairs.isEmpty())){
                    Map mhash = new HashMap();
                    mhash.put("openning_time", open);
                    mhash.put("closing_time", close);
                    mhash.put("number_of_tables", table);
                    mhash.put("chairs_per_table", chairs);
                    dbref.setValue(mhash).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                if (key != null) {
                                    Toast.makeText(getApplicationContext(), "Information updated successfully", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), Home.class));
                                } else {
                                    Toast.makeText(getApplicationContext(), "Submitted successfully", Toast.LENGTH_LONG).show();
                                    startActivity(new Intent(getApplicationContext(), Menu.class));
                                }
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Failed to submit", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
                else{
                    Toast.makeText(getApplicationContext(), "One of the input fields is empty",Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void setupUI() {
        Eopen = (EditText) findViewById(R.id.open_time);
        Eclose = (EditText) findViewById(R.id.close_time);
        Etable = (EditText) findViewById(R.id.number_of_tables);
        Echairs = (EditText) findViewById(R.id.number_of_chairs);
        submitB = (Button) findViewById(R.id.details_button);
    }
}
