package com.example.spiraltest;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import com.example.spiraltest.R;
import android.content.Intent;
import android.view.View;


public class Team5 extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team5);


        findViewById(R.id.spiralTakeTestBtn).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(v.getContext(), Team5SpiralTestMain.class);
                startActivity(intent);
            }
        });
    }
}