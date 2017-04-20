package com.example.spiraltest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;


public class Team4 extends AppCompatActivity {

    private Team4DrawingView drawView;
    private Button newBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team4);
        drawView = (Team4DrawingView)findViewById(R.id.drawing);

        newBtn = (Button)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                drawView.startNew();
            }
        });



    }



}