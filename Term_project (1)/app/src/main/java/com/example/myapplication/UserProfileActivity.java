package com.example.myapplication;

import static com.google.android.gms.vision.L.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.List;


public class UserProfileActivity extends AppCompatActivity {

     FirebaseAuth mAuth;
     FirebaseFirestore db;


     TextView user_name;
     TextView bio_text;

     ImageView profile_img;
     Button followButton;
     Button messageButton;

     String userId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);


        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();


        TextView username_text = findViewById(R.id.username);
        TextView bioTextView = findViewById(R.id.bio);

        ImageView profile_img = findViewById(R.id.profile_picture);
        followButton = findViewById(R.id.follow_button);
        messageButton = findViewById(R.id.message_button);

        ProgressBar loadingIndicator = findViewById(R.id.loading_indicator);


        userId = getIntent().getStringExtra("userId");

        loadingIndicator.setVisibility(View.VISIBLE);

        DocumentReference docRef = db.collection("users").document(userId);
        docRef.get().addOnCompleteListener(task -> {

            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {

                    username_text.setText(document.getString("username"));   // Assuming "username" is the field in your Firestore document
                    // bioTextView.setText(document.getString("bio"));


                    String imageUrl = document.getString("profileImageUrl");

                    if(imageUrl != null && !imageUrl.isEmpty()){
                        StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);

                        imageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                Glide.with(UserProfileActivity.this)
                                        .load(uri)
                                        .into(profile_img);
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {

                                Toast.makeText(UserProfileActivity.this, getString(R.string.imagefail) + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    } else {

                        profile_img.setImageResource(R.drawable.profile_photo);
                    }


                    checkIfFollowing();
                    followButton.setOnClickListener(v -> {

                        followUser();

                    });



                    messageButton.setOnClickListener(v -> {
                        openMessagingActivity();
                    });



                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    if (currentUser != null) {
                        String currentUserId = currentUser.getUid();
                        if (currentUserId.equals(userId)) {
                            followButton.setVisibility(View.GONE);
                            messageButton.setVisibility(View.GONE);
                        }
                    }
                }
                else {
                    Log.d(TAG, "No such document");
                }
            }
            else {
                Log.d(TAG, "get failed with ", task.getException());
            }
            loadingIndicator.setVisibility(View.GONE);

        });

    }


    @SuppressLint("SetTextI18n")
    public void checkIfFollowing() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();

            followButton.setEnabled(false);
            followButton.setText(getString(R.string.loading));

            db.collection("users").document(currentUserId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> following = (List<String>) document.get("following");


                        db.collection("users").document(userId).get().addOnCompleteListener(task2 -> {
                            if (task2.isSuccessful()) {
                                DocumentSnapshot document2 = task2.getResult();
                                if (document2.exists()) {
                                    String username = document2.getString("username");
                                    if (following != null && following.contains(username)) {
                                        followButton.setText(getString(R.string.unfollow));
                                    } else {
                                        followButton.setText(getString(R.string.follow));
                                    }

                                    followButton.setEnabled(true);
                                } else {
                                    Log.d(TAG, "@string/No such document for the user whose profile is being viewed");
                                }
                            } else {
                                Log.d(TAG, "@string/Failed to fetch user data", task2.getException());
                            }
                        });
                    } else {
                        Log.d(TAG, "@string/No such document for the current user");
                    }
                } else {
                    Log.d(TAG, "@string/Failed to fetch user data", task.getException());
                }
            });
        }
    }


    @SuppressLint("SetTextI18n")
    public void followUser() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();
            if (currentUserId.equals(userId)) {

                Toast.makeText(this, getString(R.string.selffollow), Toast.LENGTH_SHORT).show();
            } else {

                db.collection("users").document(userId).get().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            String targetUsername = document.getString("username");
                            if (targetUsername == null) {
                                Toast.makeText(this, getString(R.string.fetchuserfail), Toast.LENGTH_SHORT).show();
                                return;
                            }

                            db.collection("users").document(currentUserId).get().addOnCompleteListener(task2 -> {
                                if (task2.isSuccessful()) {
                                    DocumentSnapshot document2 = task2.getResult();
                                    if (document2.exists()) {
                                        List<String> following = (List<String>) document2.get("following");
                                        if (following != null && following.contains(targetUsername)) {

                                            db.collection("users").document(currentUserId).update("following", FieldValue.arrayRemove(targetUsername))
                                                    .addOnSuccessListener(aVoid -> {
                                                        followButton.setText(getString(R.string.follow));
                                                        Toast.makeText(this, getString(R.string.unfollownow), Toast.LENGTH_SHORT).show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(this, getString(R.string.followupdatefail), Toast.LENGTH_SHORT).show();
                                                    });
                                        } else {

                                            db.collection("users").document(currentUserId).update("following", FieldValue.arrayUnion(targetUsername))
                                                    .addOnSuccessListener(aVoid -> {
                                                        followButton.setText(getString(R.string.unfollow));
                                                        Toast.makeText(this, getString(R.string.follownow), Toast.LENGTH_SHORT).show();
                                                    })
                                                    .addOnFailureListener(e -> {
                                                        Toast.makeText(this, getString(R.string.followupdatefail), Toast.LENGTH_SHORT).show();
                                                    });
                                        }
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(this, getString(R.string.usernotexist), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, getString(R.string.fetchdatafail), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    }


    public void openMessagingActivity() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            String currentUserId = currentUser.getUid();


            db.collection("users").document(currentUserId).get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        List<String> currentUserFollowing = (List<String>) document.get("following");
                        Log.d(TAG, "Current user's following list: " + currentUserFollowing.toString());
                        db.collection("users").document(userId).get().addOnCompleteListener(task2 -> {
                               if (task2.isSuccessful()) {
                                DocumentSnapshot document2 = task2.getResult();
                                if (document2.exists()) {
                                    List<String> otherUserFollowing = (List<String>) document2.get("following");
                                    if (otherUserFollowing != null) {
                                        Log.d(TAG, "Other user's following list: " + otherUserFollowing.toString());
                                    } else {
                                        Log.d(TAG, "Other user's following list is null");
                                    }



                                    if (currentUserFollowing != null && otherUserFollowing != null) {
                                        // They follow each other. Open the chat activity.
                                        Intent intent = new Intent(UserProfileActivity.this, ChatActivity.class);
                                        intent.putExtra("recipientUserId", userId);
                                        startActivity(intent);


                                    }

                                    else {

                                        Toast.makeText(this, "Both users must follow each other to chat.", Toast.LENGTH_SHORT).show();

                                    }
                                }

                                else
                                {
                                    Log.d(TAG, "No such document for the other user");
                                }

                               }
                               else
                               {
                                Log.d(TAG, "Failed to fetch other user's data", task2.getException());
                            }
                        });

                    }
                    else
                    {
                        Log.d(TAG, "No such document for the current user");
                    }
                }
                else
                {
                    Log.d(TAG, "Failed to fetch current user's data", task.getException());
                }
            });
        }

    }


}