package com.example.spiraltest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.spiraltest.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        OnClickButtonListener();
    }

    private void OnClickButtonListener(){
        Button team_1 = (Button)findViewById(R.id.team1);
        Button team_2 = (Button)findViewById(R.id.team2);
        Button team_3 = (Button)findViewById(R.id.team3);
        Button team_4 = (Button)findViewById(R.id.team4);
        Button team_5 = (Button)findViewById(R.id.team5);
        Button team_6 = (Button)findViewById(R.id.team6);
        Button team_7 = (Button)findViewById(R.id.team7);
        Button team_8 = (Button)findViewById(R.id.team8);
        Button team_9 = (Button)findViewById(R.id.team9);
        Button team_10 = (Button)findViewById(R.id.team10);
        Button team_11 = (Button)findViewById(R.id.team11);
        Button team_12 = (Button)findViewById(R.id.team12);
        Button team_13 = (Button)findViewById(R.id.team13);
        Button team_14 = (Button)findViewById(R.id.team14);
        Button team_15 = (Button)findViewById(R.id.team15);
        Button team_16 = (Button)findViewById(R.id.team16);
        Button team_17 = (Button)findViewById(R.id.team17);
        Button team_18 = (Button)findViewById(R.id.team18);
        Button team_19 = (Button)findViewById(R.id.team19);

        team_1.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("Team1");
                        startActivity(intent);
                    }
                }
        );
        team_2.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("Team2");
                        startActivity(intent);
                    }
                }
        );
        team_3.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("Team3");
                        startActivity(intent);
                    }
                }
        );
        team_4.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("Team4");
                        startActivity(intent);
                    }
                }
        );
        team_5.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("Team5");
                        startActivity(intent);
                    }
                }
        );
        team_6.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("Team6");
                        startActivity(intent);
                    }
                }
        );
        team_7.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("Team7");
                        startActivity(intent);
                    }
                }
        );
        team_8.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("Team8");
                        startActivity(intent);
                    }
                }
        );
        team_9.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("Team9");
                        startActivity(intent);
                    }
                }
        );
        team_10.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("Team10");
                        startActivity(intent);
                    }
                }
        );
        team_11.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("Team11");
                        startActivity(intent);
                    }
                }
        );
        team_12.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("Team12");
                        startActivity(intent);
                    }
                }
        );
        team_13.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("Team13");
                        startActivity(intent);
                    }
                }
        );
        team_14.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("Team14");
                        startActivity(intent);
                    }
                }
        );
        team_15.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("Team15");
                        startActivity(intent);
                    }
                }
        );
        team_16.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("Team16");
                        startActivity(intent);
                    }
                }
        );
        team_17.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("Team17");
                        startActivity(intent);
                    }
                }
        );
        team_18.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("Team18");
                        startActivity(intent);
                    }
                }
        );
        team_19.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent("Team19");
                        startActivity(intent);
                    }
                }
        );




    }
}
