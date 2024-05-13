package com.example.myapplication;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

     FirebaseAuth mAuth;


     FirebaseUser currentUser;
     FirebaseFirestore db;
     EditText email_edit;
     EditText name_edit;
     EditText password_edit;
     Button signUpButton;
    DatabaseReference mDatabase;
     static final String TAG = "SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);







        name_edit = findViewById(R.id.et_name);
        email_edit = findViewById(R.id.et_email);
        password_edit = findViewById(R.id.et_password);
        signUpButton = findViewById(R.id.btn_sign_up);




        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();
        db = FirebaseFirestore.getInstance();
        currentUser = mAuth.getCurrentUser();




        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = email_edit.getText().toString().trim();
                String password = password_edit.getText().toString().trim();
                String name = name_edit.getText().toString().trim();

                if (isInputValid(email, password)) {
                    long id = 0;
                    createUser(email, password,name,id);
                }
            }
        });

    }

    public boolean isInputValid(String email, String password) {
        if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            Toast.makeText(SignUpActivity.this, getString(R.string.enteremail), Toast.LENGTH_SHORT).show();
            return false;
        }


        if (password.isEmpty()) {
            Toast.makeText(SignUpActivity.this, getString(R.string.enterpassword), Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }


    public void createUser(String email, String password, String name,long id) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignUpActivity.this, getString(R.string.profilecreated), Toast.LENGTH_SHORT).show();

                            String userId = mAuth.getCurrentUser().getUid();
                            User user = new User(email,name, password,id);

                            mDatabase.child("users").child(userId).setValue(user);

                            // Set User details in Firestore
                            Map<String, Object> userMap = new HashMap<>();
                            userMap.put("username", name);
                            userMap.put("email", email);

                            db.collection("users").document(userId)
                                    .set(userMap)
                                    .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                                    .addOnFailureListener(e -> Log.w(TAG, "Error writing document", e));

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(name)
                                    .build();

                            mAuth.getCurrentUser().updateProfile(profileUpdates)
                                    .addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(SignUpActivity.this, getString(R.string.profileupdatesucc),
                                                    Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(SignUpActivity.this, getString(R.string.profileupdatefail),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    });


                            Intent intent = new Intent(SignUpActivity.this, RegisterActivity.class);
                            startActivity(intent);

                            finish();
                        } else {
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignUpActivity.this, getString(R.string.signupfail), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }
}