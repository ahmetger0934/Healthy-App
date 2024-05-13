package com.example.myapplication;
import static com.google.android.gms.vision.L.TAG;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.Spinner;
import java.util.ArrayList;
import android.widget.ArrayAdapter;
import java.util.Locale;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.widget.AdapterView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


/*
public class ProfileActivity extends AppCompatActivity {





    private EditText et_display_name, etEmail;
    private Button btnChangePassword, btnUpdateProfile;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        EditText etDisplayName = findViewById(R.id.et_display_name);
        EditText etEmail = findViewById(R.id.et_email);
        EditText etPassword = findViewById(R.id.et_password);
        Button btnUpdateProfile = findViewById(R.id.btn_update_profile);
        Button btnChangePassword = findViewById(R.id.btn_change_password);

        Button buttonLogout = findViewById(R.id.button_logout);




        mAuth = FirebaseAuth.getInstance();
        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            etDisplayName.setText(currentUser.getDisplayName());
            etEmail.setText(currentUser.getEmail());
        }

        btnUpdateProfile.setOnClickListener(v -> updateProfile());
        btnChangePassword.setOnClickListener(v -> changePassword(etPassword.getText().toString()));

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                // Redirect the user to the login page
                startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                finish();
            }
        });
    }

    private void updateProfile() {
        // Update user's display name in app

        String displayName = et_display_name.getText().toString();
        if (TextUtils.isEmpty(displayName)) {
            et_display_name.setError("Display name is required");
            return;
        }
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();
        currentUser.updateProfile(profileUpdates).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(ProfileActivity.this, "Display name updated successfully", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(ProfileActivity.this, "Failed to update display name", Toast.LENGTH_SHORT).show();

            }
        });

        // Update user's email in Firebase
        String email = etEmail.getText().toString();
        if (!TextUtils.isEmpty(email)) {
            currentUser.updateEmail(email).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(ProfileActivity.this, "Email updated successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to update email", Toast.LENGTH_SHORT).show();
                }
            });
        }

    }



    private void changePassword(String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.updatePassword(newPassword)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Toast.makeText(ProfileActivity.this, "Password changed successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Password change failed", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }


}



 */






import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ProfileActivity extends AppCompatActivity {
    private static final int PICK_IMAGE_REQUEST = 1;

    DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser currentUser;
    private FirebaseFirestore db;
    private FirebaseStorage storage;
    private StorageReference storage_ref;
    private ImageView profile_photo;
    private ImageButton img_btn;

    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);


        img_btn = findViewById(R.id.imageButton);
        img_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recreate();
            }
        });

        db = FirebaseFirestore.getInstance();

        mAuth = FirebaseAuth.getInstance();

        currentUser = mAuth.getCurrentUser();

        storage = FirebaseStorage.getInstance();

           storage_ref = storage.getReference();

           profile_photo = findViewById(R.id.iv_profile_photo);

        mDatabase = FirebaseDatabase.getInstance().getReference();

        ImageButton uploadpost = findViewById(R.id.btn_post);

        ImageButton backHome = findViewById(R.id.btn_home);
          ImageButton search = findViewById(R.id.searchButton);




        TextInputEditText display_name_edit = findViewById(R.id.et_display_name);
        TextInputEditText email_edit = findViewById(R.id.et_email);
        TextInputEditText password_edit = findViewById(R.id.et_password);
        MaterialButton update_profile_btn = findViewById(R.id.btn_update_profile);
        MaterialButton change_psword_btn = findViewById(R.id.btn_change_password);
        @SuppressLint("WrongViewCast") MaterialButton uploadPhotoButton = findViewById(R.id.btn_upload_photo);
        MaterialButton logoutButton = findViewById(R.id.button_logout);

        currentUser = mAuth.getCurrentUser();

        if (currentUser != null) {
            display_name_edit.setText(currentUser.getDisplayName());
            email_edit.setText(currentUser.getEmail());


            profile_photo.setImageResource(R.drawable.profile_photo);




            DocumentReference userDoc = db.collection("users").document(currentUser.getUid());

        userDoc.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.w(TAG, "Listen failed.", e);
                return;
            }

            if (documentSnapshot != null && documentSnapshot.exists()) {
                String imageUrl = documentSnapshot.getString("profileImageUrl");
                if (imageUrl != null) {

                    if (!isFinishing()) {
                        Glide.with(ProfileActivity.this)
                                .load(imageUrl)
                                .into(profile_photo);
                    }

                } else {

                    profile_photo.setImageResource(R.drawable.profile_photo);
                }
            } else {
                Log.d(TAG, "Current data: null");
            }
        });
    }

        profile_photo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               choosePicture();

            }
        }

        );

        File photoFile = new File(getFilesDir(), "profile_photo.jpg");
        if (photoFile.exists()) {
            Uri photoUri = Uri.fromFile(photoFile);
            profile_photo.setImageURI(photoUri);
        }

        if (currentUser == null) {
            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        } else {
            display_name_edit.setText(currentUser.getDisplayName());
            email_edit.setText(currentUser.getEmail());
        }

        update_profile_btn.setOnClickListener(v -> {
            String displayName = display_name_edit.getText().toString();
            String email = email_edit.getText().toString();

            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build();

            currentUser.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            currentUser.updateEmail(email)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {

                                            Map<String, Object> userMap = new HashMap<>();
                                            userMap.put("email", email);
                                            userMap.put("username", displayName);


                                            db.collection("users").document(currentUser.getUid())

                                                    .set(userMap, SetOptions.merge()).addOnSuccessListener(aVoid -> {
                                                        Log.d(TAG, "DocumentSnapshot successfully written!");
                                                        DocumentReference userDoc = db.collection("users").document(currentUser.getUid());
                                                        userDoc.get().addOnCompleteListener(task2 -> {
                                                                    if (task2.isSuccessful()) {
                                                                DocumentSnapshot document = task2.getResult();
                                                                if (document.exists()) {
                                                                    String imageUrl = document.getString("profileImageUrl");
                                                                    if (imageUrl != null) {
                                                                        Glide.with(ProfileActivity.this)
                                                                                .load(imageUrl)
                                                                                .into(profile_photo);
                                                                    }

                                                                    else
                                                                    {

                                                                        profile_photo.setImageResource(R.drawable.profile_photo);
                                                                    }
                                                                }
                                                            }
                                                                    else {

                                                                        Log.d(TAG, "No such document");

                                                                    }
                                                        });


                                                        Toast.makeText(ProfileActivity.this, getString(R.string.profileupdatesucc),
                                                                Toast.LENGTH_SHORT).show();
                                                    }).addOnFailureListener(e -> {

                                                        Log.w(TAG, "Error writing document", e);
                                                    });
                                        } else
                                        {
                                            Toast.makeText(ProfileActivity.this, getString(R.string.emailupdatefail),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        } else {
                            Toast.makeText(ProfileActivity.this, getString(R.string.profileupdatefail),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        change_psword_btn.setOnClickListener(v -> {
            String newPassword = password_edit.getText().toString();

            mDatabase.child("users").child(currentUser.getUid()).child("password").setValue(newPassword)
                    .addOnSuccessListener(aVoid -> {
                        Log.d(TAG, "Password successfully updated in Realtime Database!");
                        Toast.makeText(ProfileActivity.this, getString(R.string.chng_passwrd), Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Failed to update password in Realtime Database.", e);
                        Toast.makeText(ProfileActivity.this, "Failed to Update Password in Realtime Database.", Toast.LENGTH_SHORT).show();
                    });
        });


        uploadPhotoButton.setOnClickListener(v -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
        });

        logoutButton.setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();

            profile_photo.setImageResource(R.drawable.profile_photo);

            startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
            finish();
        });

        uploadpost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, PostActivity.class));
            }
        });

        backHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, MainActivity.class));
            }
        });

        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProfileActivity.this, SearchActivity.class));
            }
        });


        spinner = findViewById(R.id.langspin);
        ArrayList<String> languages = new ArrayList<>();
        languages.add("English");
        languages.add("Türkçe");
        ArrayAdapter<String> languageAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                languages
        );
        spinner.setAdapter(languageAdapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        setLocale("en");
                        break;
                    case 1:
                        setLocale("tr");
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private void setLocale(String languageCode) {
        Locale locale = new Locale(languageCode);
        Locale.setDefault(locale);

        Resources resources = getResources();
        Configuration configuration = resources.getConfiguration();
        configuration.setLocale(locale);
        resources.updateConfiguration(configuration, resources.getDisplayMetrics());
    }

    private void choosePicture() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK) {
            if (data != null && data.getData() != null) {
                Uri filePath = data.getData();
                try {
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                    profile_photo.setImageBitmap(bitmap);


                    uploadImage(filePath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {

                profile_photo.setImageResource(R.drawable.profile_photo);
            }
        }

    }

    private void uploadImage(Uri filePath) {
        if (filePath != null) {
            StorageReference fileRef = storage_ref.child("profileImages/" + currentUser.getUid() + ".jpg");
            UploadTask uploadTask = fileRef.putFile(filePath);

            uploadTask.addOnFailureListener(exception -> {
                Toast.makeText(ProfileActivity.this, getString(R.string.uploadfail) + exception.getMessage(), Toast.LENGTH_SHORT).show();
            }).addOnSuccessListener(taskSnapshot -> {
                Toast.makeText(ProfileActivity.this, getString(R.string.uploadsucc), Toast.LENGTH_SHORT).show();


                fileRef.getDownloadUrl().addOnSuccessListener(uri -> {
                    DocumentReference userDoc = db.collection("users").document(currentUser.getUid());
                    userDoc.update("profileImageUrl", uri.toString());
                }).addOnFailureListener(e -> {
                    Toast.makeText(ProfileActivity.this, getString(R.string.urlfail) + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            });
        }
    }
}