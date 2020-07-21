package com.example.restaurants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

public class Sign_Up extends AppCompatActivity {

    private ImageView usrimg;
    private EditText usernameE, emailE, passwordE;
    private String  username, email, password;
    private Button sButton;
    private StorageReference mFirebaseStorage;
    private FirebaseAuth firebaseAuth;
    private static final int GALLERY_CODE = 1;
    private ProgressDialog progressDialog;
    Uri resultUri = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_sign__up);
        setupUi();

        firebaseAuth = FirebaseAuth.getInstance();

        progressDialog = new ProgressDialog(this);
        mFirebaseStorage = FirebaseStorage.getInstance().getReference().child("users_profile_pics");

        usrimg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);

            }
        });

        sButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username  = usernameE.getText().toString();
                email = emailE.getText().toString();
                password = passwordE.getText().toString();
                progressDialog.setMessage("Creating Account");
                progressDialog.show();

                if(!(username.isEmpty() && email.isEmpty() && password.isEmpty())){

                    firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(Sign_Up.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()){
                                progressDialog.dismiss();
                                Log.d("values " , email+", "+password+ ", "+username);
                                Toast.makeText(getApplicationContext(), "Some error during sign up",Toast.LENGTH_LONG).show();
                            }
                            else{
                                Log.d("before the image insert", "okay" );
                                mFirebaseStorage.child(resultUri.getLastPathSegment()).putFile(resultUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        StorageReference imagePath = mFirebaseStorage.child(resultUri.getLastPathSegment());
                                        imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                            @Override
                                            public void onSuccess(Uri uri) {
                                                Log.d("Success " , " Yes");
                                                String user_id = firebaseAuth.getCurrentUser().getUid();
                                                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(user_id);
                                                Map newUsr =new HashMap();
                                                newUsr.put("name", username);
                                                newUsr.put("email", email);
                                                newUsr.put("imageLink", uri.toString());
                                                dbref.setValue(newUsr);
                                                progressDialog.dismiss();
                                                Toast.makeText(getApplicationContext(), "Successfully created!",Toast.LENGTH_LONG).show();
                                                startActivity(new Intent(getApplicationContext(), ResUsr.class));
                                                finish();

                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });


                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "One of the fields is empty" , Toast.LENGTH_LONG).show();
                }

            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == GALLERY_CODE && resultCode == RESULT_OK){

            if(data.getData() != null){
                Uri mImageUri = data.getData();
                CropImage.activity(mImageUri)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setAspectRatio(1, 1)
                        .start(this);
            }
            else {
                //  Toast.makeText(getApplicationContext(), "Error data is null", Toast.LENGTH_LONG).show();
                Log.d("Value is null", "Yes it is");
            }
        }

        if(requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE){
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if(resultCode == RESULT_OK){
                resultUri = result.getUri();

                usrimg.setImageURI(resultUri);
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();

            }

        }
    }

    private void setupUi() {
        usrimg = (ImageView) findViewById(R.id.user);
        usernameE = (EditText) findViewById(R.id.Susername);
        emailE = (EditText) findViewById(R.id.Semail);
        passwordE = (EditText) findViewById(R.id.passwordS);
        sButton = (Button) findViewById(R.id.btn_singup);
    }

}
