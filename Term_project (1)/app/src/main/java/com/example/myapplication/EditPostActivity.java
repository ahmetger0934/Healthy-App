package com.example.myapplication;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class EditPostActivity extends AppCompatActivity {

    DatabaseReference databaseReference;
    String postId;
    EditText editTextCaption;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_post);

        databaseReference = FirebaseDatabase.getInstance().getReference("posts");

        editTextCaption = findViewById(R.id.editTextCaption);
        Button buttonSubmit = findViewById(R.id.buttonSubmit);

        postId = getIntent().getStringExtra("postId");

        loadPostDetails();

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateCaption();
            }
        });
    }

    private void loadPostDetails() {
        databaseReference.child(postId).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                DataClass post = dataSnapshot.getValue(DataClass.class);
                if (post != null) {
                    editTextCaption.setText(post.getCaption());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(EditPostActivity.this, getString(R.string.failed), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateCaption() {
        String newCaption = editTextCaption.getText().toString();
        databaseReference.child(postId).child("caption").setValue(newCaption)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(EditPostActivity.this, getString(R.string.capupdate), Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditPostActivity.this, getString(R.string.failedupdatecap), Toast.LENGTH_SHORT).show();
                    }
                });
    }

}

