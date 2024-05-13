package com.example.myapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {


    String userName;
    String profileImageURL;

    FloatingActionButton fab;
    FloatingActionButton fab2;
     RecyclerView recyclerView;
     ArrayList<DataClass> dataList;
     PostAdapter adapter;
      DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("Images");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        fab = findViewById(R.id.fab);
        fab2 = findViewById(R.id.fab2);
        recyclerView = findViewById(R.id.post_recycler_view);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        dataList = new ArrayList<>();
        adapter = new PostAdapter(this, dataList, userName, profileImageURL);
        recyclerView.setAdapter(adapter);


        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("users").document(userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                userName = document.getString("username");
                                profileImageURL = document.getString("profileImageUrl");


                                adapter = new PostAdapter(MainActivity.this, dataList, userName, profileImageURL);
                                recyclerView.setAdapter(adapter);

                                // Fetch posts from Firebase and update RecyclerView
                                databaseReference = FirebaseDatabase.getInstance().getReference("Images");
                                databaseReference.addValueEventListener(new ValueEventListener() {
                                    @SuppressLint("NotifyDataSetChanged")
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        dataList.clear(); // Clear the list before adding new data
                                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                                            DataClass dataClass = dataSnapshot.getValue(DataClass.class);
                                            dataList.add(dataClass);
                                        }
                                        adapter.notifyDataSetChanged();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });
                            } else {
                                Log.d("MainActivity", "No such document");
                            }
                        } else {
                            Log.d("MainActivity", "get failed with ", task.getException());
                        }
                    }
                });




        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, MainActivity2.class);
                startActivity(intent);

            }
        });

        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, HealthHelperActivity.class);
                startActivity(intent);

            }
        });




            ImageButton btnOpenPost = findViewById(R.id.btn_post);
        btnOpenPost.setOnClickListener(v -> {

            Intent intent = new Intent(MainActivity.this, PostActivity.class);
            startActivity(intent);
        });

         ImageButton btnchat = findViewById(R.id.ChatButton);
        btnchat.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ChatActivity.class);
            startActivity(intent);
        });

        ImageButton searchButton = findViewById(R.id.searchButton);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });


        ImageButton btnOpenProfile = findViewById(R.id.btn_open_profile);
        btnOpenProfile.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("users").child(user.getUid());
            userRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User userProfile = snapshot.getValue(User.class);
                    if (userProfile != null) {
                        // it should shows once the user is logged in

                       String welcomeMessage = getString(R.string.welcome) + userProfile.getName() + "!";
                        Toast.makeText(MainActivity.this, welcomeMessage, Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(MainActivity.this, getString(R.string.userdatafail) + error.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });


        }



    }


}