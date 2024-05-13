package com.example.myapplication;

import static com.example.myapplication.SignUpActivity.TAG;

import android.annotation.SuppressLint;
import android.app.Activity;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class PostActivity extends AppCompatActivity {

     FloatingActionButton uploadButton;
     ImageView uploadImage;
    EditText caption;
    ProgressBar progressBar;
    private String username;
    private String profile_photo;
     Uri img_uri;
     DatabaseReference db_ref = FirebaseDatabase.getInstance().getReference("Images");
     StorageReference storage_ref = FirebaseStorage.getInstance().getReference();

    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        uploadButton = findViewById(R.id.uploadButton);
        caption = findViewById(R.id.uploadCaption);
        uploadImage = findViewById(R.id.uploadImage);

        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(currentUser.getUid());
            userRef.addValueEventListener(new ValueEventListener() {
                                              @Override
                                              public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                  User userProfile = snapshot.getValue(User.class);
                                                  if (userProfile != null) {
                                                      username = userProfile.getName();  // change this to match your actual field name
                                                      profile_photo = userProfile.getProfilePhotoUrl();
                                                  }
                                              }


                                                @Override
                                                public void onCancelled(@NonNull DatabaseError error) {
                                                    Toast.makeText(PostActivity.this, getString(R.string.somethingwrong), Toast.LENGTH_LONG).show();
                                                }

                                            }

            );

        }



        ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        if (result.getResultCode() == Activity.RESULT_OK){
                            Intent data = result.getData();
                            img_uri = data.getData();
                            uploadImage.setImageURI(img_uri);
                        } else {
                            Toast.makeText(PostActivity.this, getString(R.string.noimage), Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        uploadImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent photoPicker = new Intent();
                photoPicker.setAction(Intent.ACTION_GET_CONTENT);
                photoPicker.setType("image/*");
                activityResultLauncher.launch(photoPicker);
            }
        });

        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (img_uri != null){
                    uploadToFirebase(img_uri);
                } else  {
                    Toast.makeText(PostActivity.this, getString(R.string.selectimage), Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void uploadToFirebase(Uri uri){
        String caption = this.caption.getText().toString();
        final StorageReference imageReference = storage_ref.child(System.currentTimeMillis() + "." + getFileExtension(uri));


        int maxCaptionLength = 10;


        if (caption.length() > maxCaptionLength) {

            final String truncatedCaption = caption.substring(0, maxCaptionLength) + "...";


            this.caption.setText(truncatedCaption);


            this.caption.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    showDialogOrToast(truncatedCaption, caption);
                }
            });
        }

        String postId = db_ref.push().getKey();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();

        String username = FirebaseAuth.getInstance().getCurrentUser().getDisplayName();
        String finalCaption = caption;


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("users").document(userId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String profilePhotoUrl = document.getString("profilePhotoUrl");

                        imageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                imageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        DataClass dataClass = new DataClass(uri.toString(), finalCaption, username, profilePhotoUrl, postId, userId);
                                        String key = db_ref.push().getKey();
                                        db_ref.child(key).setValue(dataClass);
                                        Toast.makeText(PostActivity.this, getString(R.string.uploaded), Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(PostActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }
                                });
                            }
                        });

                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });

    }

    public void showDialogOrToast(String truncatedCaption, String fullCaption) {

        Toast.makeText(PostActivity.this, fullCaption, Toast.LENGTH_LONG).show();
    }
    public String getFileExtension(Uri fileUri){
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(fileUri));
    }
}