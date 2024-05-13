package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.SharedPreferences;

public class HealthHelperActivity extends AppCompatActivity{// This extends bmiactivity so we can access bmi var.

    private float bmi = -1;
    private Button button;
    private Button report;
    private TextView bmitext;
    private static final String SHARED_PREFS_NAME = "MyPrefs";
    private static final String VARIABLE_KEY = "variable_key";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_health_helper);
        bmitext = findViewById(R.id.bmitext);
        button = findViewById(R.id.bmi);
        report = findViewById(R.id.report);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBmi();
            }
        });
        report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences sharedPrefs = getSharedPreferences(SHARED_PREFS_NAME, MODE_PRIVATE);
                bmi = sharedPrefs.getFloat(VARIABLE_KEY, 0.0f);
                if(bmi < 0){
                    bmitext.setText(getString(R.string.impossweight)); //These are placeholders at worst case senerio they work at least
                } else if(bmi < 18.5){
                    bmitext.setText(getString(R.string.underweight));
                } else if(bmi < 24.9){
                    bmitext.setText(getString(R.string.normalweight));
                } else if(bmi < 29.9) {
                    bmitext.setText(getString(R.string.overweight));
                } else if(bmi < 34.9) {
                    bmitext.setText(getString(R.string.obeseweight));
                } else if(bmi < 40) {
                    bmitext.setText(getString(R.string.selectimage));
                }
            }
        });
    }
    public void openBmi(){
        Intent intent = new Intent(this, BmiActivity.class);
        startActivity(intent);
    }
}