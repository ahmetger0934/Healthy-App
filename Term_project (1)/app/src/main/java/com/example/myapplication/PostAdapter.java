package com.example.myapplication;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;



import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.MyViewHolder> {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
     ArrayList<DataClass> dataList;
     Context context;

    private DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("posts");
    private String username;
    private String profileImageURL;
    public PostAdapter(Context context, ArrayList<DataClass> dataList, String username, String profileImageURL) {

        this.context = context;
        this.dataList = dataList;
        this.username = username;
        this.profileImageURL = profileImageURL;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.recycler_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        DataClass dataClass = dataList.get(position);

        db.collection("users").document(dataClass.getUserId()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null && document.exists()) {
                        String profilePhotoUrl = document.getString("profileImageUrl");
                        Glide.with(context).load(profilePhotoUrl).into(holder.profilePicture);
                    }
                } else {
                    Log.d("PostAdapter", "@string/Failed to get document: ", task.getException());
                }
            }
        });
        Glide.with(context).load(dataList.get(position).getImageURL()).into(holder.recyclerImage);
        holder.recyclerCaption.setText(dataList.get(position).getCaption());
        holder.userName.setText(dataList.get(position).getUsername());
        Glide.with(context).load(profileImageURL).into(holder.profilePicture);


        holder.likesImage.setOnClickListener(v -> {
            String postId = dataList.get(position).getPostId();
            increaseLikeCount(postId);
        });

        holder.commentsImage.setOnClickListener(v -> {
            String postId = dataList.get(position).getPostId();

        });

        holder.sharesImage.setOnClickListener(v -> {
            String postId = dataList.get(position).getPostId();
            sharePost(postId);
        });




        holder.postMenuButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if (currentUser != null && currentUser.getUid().equals(dataClass.getUserId())) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("Post options")
                            .setItems(new CharSequence[]{"Edit", "Delete"}, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0: // Edit

                                            Intent intent = new Intent(context, EditPostActivity.class);
                                            intent.putExtra("postId", dataClass.getPostId());
                                            context.startActivity(intent);
                                            break;
                                        case 1: // Delete

                                            databaseReference.child(dataClass.getPostId()).removeValue()
                                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                        @Override
                                                        public void onSuccess(Void aVoid) {

                                                            Toast.makeText(context, context.getString(R.string.postdlt), Toast.LENGTH_SHORT).show();


                                                            int position = dataList.indexOf(dataClass);
                                                            dataList.remove(position);
                                                            notifyItemRemoved(position);
                                                            notifyItemRangeChanged(position, dataList.size());

                                                        }
                                                    })
                                                    .addOnFailureListener(new OnFailureListener() {
                                                        @Override
                                                        public void onFailure(@NonNull Exception e) {

                                                            Toast.makeText(context, "Failed to delete post", Toast.LENGTH_SHORT).show();
                                                        }
                                                    });

                                            break;
                                    }
                                }
                            })
                            .setNegativeButton("Cancel", null)
                            .show();
                } else {
                    Toast.makeText(context, "You can only edit or delete your own posts.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }
    private void increaseLikeCount(String postId) {
        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Images").child(postId);

        postRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    int currentLikes = snapshot.child("likes").getValue(Integer.class);
                    if (currentLikes != 0) {
                        int newLikes = currentLikes + 1;
                        postRef.child("likes").setValue(newLikes);
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.d("PostAdapter", "increaseLikeCount:onCancelled", error.toException());
            }
        });
    }

    private void sharePost(String postId) {

        DatabaseReference postRef = FirebaseDatabase.getInstance().getReference("Images").child(postId);
        postRef.child("shareCount").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                long shareCount = snapshot.getValue(Long.class);
                postRef.child("shareCount").setValue(shareCount + 1);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Handle database error
            }
        });
    }


    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView recyclerImage;
        TextView recyclerCaption;
        ImageView profilePicture;
        TextView userName;
        ImageView likesImage;
        ImageView commentsImage;
        ImageView sharesImage;
        ImageButton postMenuButton;






        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            recyclerImage = itemView.findViewById(R.id.recyclerImage);
            recyclerCaption = itemView.findViewById(R.id.recyclerCaption);
            profilePicture = itemView.findViewById(R.id.profilePicture);
            userName = itemView.findViewById(R.id.userName);
            likesImage = itemView.findViewById(R.id.likesImage);
            commentsImage = itemView.findViewById(R.id.commentsImage);
            sharesImage = itemView.findViewById(R.id.sharesImage);
            postMenuButton = itemView.findViewById(R.id.postMenuButton);

        }
    }
}
