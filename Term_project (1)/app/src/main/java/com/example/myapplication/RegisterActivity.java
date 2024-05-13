package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class RegisterActivity extends AppCompatActivity {

    private EditText emailEditText;
    private EditText passwordEditText;
    private Button loginButton;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                if (validateInput(email, password)) {

                    Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                    startActivity(intent);


                    finish();

                } else {
                    Toast.makeText(RegisterActivity.this, getString(R.string.validemailpass), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private boolean validateInput(String email, String password) {

        return true;
    }
}

