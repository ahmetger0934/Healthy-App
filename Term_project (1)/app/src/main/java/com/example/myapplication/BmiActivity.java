package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.content.SharedPreferences;

public class BmiActivity extends AppCompatActivity {

    private static final String SHARED_PREFS_NAME = "MyPrefs";
    private static final String VARIABLE_KEY = "variable_key";
    private EditText height;
    private EditText weight;
    private TextView bmifinal;
    private Button calc;
    public float bmi = -1;
    private float w;
    private float h;

    private String text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bmi);
        bmifinal = findViewById(R.id.bmiText);
        height = findViewById(R.id.bmiL);
        weight = findViewById(R.id.bmiR);
        calc = findViewById(R.id.bmiButt);

        SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);

        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                text = weight.getText().toString();
                w = Float.parseFloat(text);
                text = height.getText().toString();
                h = Float.parseFloat(text);
                bmi = w/(h*h); // You should store the bmi in the profile so we can use it in health helper
                bmifinal.setText(String.valueOf(bmi));
                SharedPreferences.Editor editor = sharedPrefs.edit();
                editor.putFloat(VARIABLE_KEY, bmi);
                editor.apply();
            }
        });
    }
}