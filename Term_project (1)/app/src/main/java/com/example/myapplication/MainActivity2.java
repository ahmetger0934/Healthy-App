package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity2 extends AppCompatActivity {
    private RecyclerView RecyV;
    private String[] activityNames ={"Meatballs","Soup","Pizza","Hotdog","Hamburger","Salad"};//To add a new activity destination to buttons add the activity name here
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        RecyV = findViewById(R.id.RecyV);

        ArrayList<RecViewContent> contents = new ArrayList<>();
        contents.add(new RecViewContent(getString(R.string.meatballs),"https://downshiftology.com/wp-content/uploads/2023/02/Meatballs-main-500x500.jpg"));//This is how we add new content to the menu, to add the images just copy a images path from google, be careful it only works with paths that end with .jpg
        contents.add(new RecViewContent(getString(R.string.soup), "https://www.thespruceeats.com/thmb/fLepfqodpZrQztMOSn86CSfovTY=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/malaysian-potato-carrot-and-tomato-soup-3030415-hero-01-7d51917d0cd14e17a13495f4c9940100.jpg"));
        contents.add(new RecViewContent(getString(R.string.pizza), "https://www.unileverfoodsolutions.com.tr/dam/global-ufs/mcos/TURKEY/calcmenu/recipes/TR-recipes/general/ac%C4%B1l%C4%B1-pizza-(tulum-peyniri-mantar-ve-roka-ile)/main-header.jpg"));
        contents.add(new RecViewContent(getString(R.string.hotdog), "https://blog.blueapron.com/wp-content/uploads/2022/07/loaded-hot-dogs.jpg"));
        contents.add(new RecViewContent(getString(R.string.hamburger), "https://www.unileverfoodsolutions.com.tr/konsept-uygulamalarimiz/sokak-lezzetleri/hamburger-malzemesi-secerken/jcr:content/parsys/content/textimage_copy_copy_628821058/image.transform/jpeg-optimized/image.1552661787858.jpg"));
        contents.add(new RecViewContent(getString(R.string.salad),"https://images.immediate.co.uk/production/volatile/sites/30/2014/05/Epic-summer-salad-hub-2646e6e.jpg" ));
        RecViewAdapter adapter = new RecViewAdapter(this,activityNames);
        adapter.setContents(contents);

        RecyV.setAdapter(adapter);
        RecyV.setLayoutManager(new LinearLayoutManager(this));

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create new item here
                contents.add(new RecViewContent(getString(R.string.bread),"https://www.allrecipes.com/thmb/CjzJwg2pACUzGODdxJL1BJDRx9Y=/1500x0/filters:no_upscale():max_bytes(150000):strip_icc()/6788-amish-white-bread-DDMFS-4x3-6faa1e552bdb4f6eabdd7791e59b3c84.jpg"));
            }
        });
    }

}
