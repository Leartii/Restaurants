package com.example.restaurants;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class Add_Menu extends AppCompatActivity {
    private CircleImageView imgV;
    private EditText nameV, priceV;
    private Button add;
    private String name, price;
    private Uri uriResult = null;
    private String key;
    private FirebaseAuth mAuth;
    private DatabaseReference dbref;
    private StorageReference firebaseStorage;
    private ProgressDialog progressDialog;
    private static final int GALLERY_CODE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_add__menu);
        setupUI();
        progressDialog = new ProgressDialog(this);

        try {
            Bundle extras = getIntent().getExtras();
            key = extras.getString("key");
        }
        catch (Exception e){
            e.printStackTrace();
        }

        Log.d("GetUID()", FirebaseAuth.getInstance().getUid());

        if(key != null){
            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child("Users")
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Menu").child(key);

            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                         nameV.setText(dataSnapshot.child("name").getValue().toString());
                         priceV.setText(dataSnapshot.child("price").getValue().toString());
                    Picasso.with(getApplicationContext()).load(dataSnapshot.child("img").getValue().toString()).placeholder(R.drawable.theuser).into(imgV);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
        }
        mAuth = FirebaseAuth.getInstance();
        dbref = FirebaseDatabase.getInstance().getReference().child("Users").child(mAuth.getCurrentUser().getUid()).child("Menu");
        firebaseStorage = FirebaseStorage.getInstance().getReference().child("Menu_Pictures").child(mAuth.getCurrentUser().getUid()).child("Photos");
        imgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent();
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                galleryIntent.setType("image/*");
                startActivityForResult(galleryIntent, GALLERY_CODE);
            }
        });

        add.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                name = nameV.getText().toString();
                price = priceV.getText().toString();
                progressDialog.setMessage("Adding Item");
                progressDialog.show();

                if(!name.isEmpty() && !price.isEmpty() && uriResult!=null){

                   firebaseStorage.child(uriResult.getLastPathSegment()).putFile(uriResult).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                       @Override
                       public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                           StorageReference imagePath = firebaseStorage.child(uriResult.getLastPathSegment());
                           imagePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                               @Override
                               public void onSuccess(Uri uri) {
                                    Map mhash = new HashMap();
                                    mhash.put("name", name);
                                    mhash.put("price", price);
                                    mhash.put("img", uri.toString());
                                  if(key == null){
                                    dbref.push().setValue(mhash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void aVoid) {
                                            Toast.makeText(getApplicationContext(), " Successfully inserted", Toast.LENGTH_LONG).show();
                                        }
                                    });
                                    progressDialog.dismiss();
                                    finish();
                                  }else {
                                      dbref.child(key).setValue(mhash).addOnSuccessListener(new OnSuccessListener<Void>() {
                                          @Override
                                          public void onSuccess(Void aVoid) {
                                              Toast.makeText(getApplicationContext(), " Successfully inserted", Toast.LENGTH_LONG).show();
                                          }
                                      });
                                      progressDialog.dismiss();
                                      finish();
                                  }

                               }
                           });
                           progressDialog.dismiss();
                       }
                   });
                   progressDialog.dismiss();
                }
                else if(!name.isEmpty() && !price.isEmpty() && uriResult == null && key != null){

                    dbref.child(key).child("name").setValue(name);
                    dbref.child(key).child("price").setValue(price);
                    progressDialog.dismiss();


                }
                else{
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(), "Some field is null, please add a picture of the item", Toast.LENGTH_LONG).show();
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
                uriResult = result.getUri();

                imgV.setImageURI(uriResult);
            }
            else if(resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE){
                Exception error = result.getError();

            }

        }
    }

    private void setupUI(){
        imgV =(CircleImageView) findViewById(R.id.item_img);
        nameV = (EditText) findViewById(R.id.item_nm);
        priceV = (EditText) findViewById(R.id.itemprc);
        add = (Button) findViewById(R.id.item_submit);
    }
}
