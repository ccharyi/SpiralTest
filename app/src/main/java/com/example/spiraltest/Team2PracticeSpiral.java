package com.example.spiraltest;

import android.app.Activity;
import android.content.Intent;

import android.os.Bundle;
import android.view.View;

public class Team2PracticeSpiral extends Activity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team2_practice_spiral);
    }

    public void toTest(View v)
    {
        Intent intent = new Intent(Team2PracticeSpiral.this, Team2DrawingActivity.class);
        intent.putExtra("hand", "left");
        startActivity(intent);
    }
}
