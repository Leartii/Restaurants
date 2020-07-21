package com.example.restaurants;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Log_In extends AppCompatActivity {
    private EditText emailE, passwordE;
    private String email, password;
    private TextView signup;
    private Button lButton;
    private FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_log__in);
        setupUI();
        mAuth = FirebaseAuth.getInstance();
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Sign_Up.class));
            }
        });

        lButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailE.getText().toString();
                password = passwordE.getText().toString();

                if(email.isEmpty()){
                    emailE.setError("Enter email address");
                    emailE.requestFocus();
                }
                else if(password.isEmpty()){
                    passwordE.setError("Enter password");
                    passwordE.requestFocus();
                }
                else if(email.isEmpty() && password.isEmpty()){
                    Toast.makeText(Log_In.this, "Fields are empty", Toast.LENGTH_LONG).show();
                }

                else if(!(email.isEmpty() && password.isEmpty())){
                    mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(Log_In.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(Log_In.this, "Sign up error, please try again", Toast.LENGTH_LONG).show();
                            }
                            else{
                                Toast.makeText(Log_In.this, "Signed up successfully", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(getApplicationContext(), Home.class));
                                finish();
                            }
                        }
                    });
                }

                else{
                    Toast.makeText(Log_In.this, "Error occurred", Toast.LENGTH_LONG).show();
                }

            }
        });


    }

    private void setupUI() {
        emailE = (EditText) findViewById(R.id.username);
        passwordE = (EditText) findViewById(R.id.password);
        lButton = (Button) findViewById(R.id.btn_login);
        signup = (TextView) findViewById(R.id.sign_uptext);
    }
}
