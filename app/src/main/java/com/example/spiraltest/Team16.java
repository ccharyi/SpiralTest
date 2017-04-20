package com.example.spiraltest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class Team16 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team16);
    }
    public void onClick(View view) {
        Intent i = new Intent(this, Team16Spiral.class);

        startActivity(i);
    }
}