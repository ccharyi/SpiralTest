package com.example.spiraltest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.spiraltest.Team7Results;
import com.example.spiraltest.Team7CommentDialog;


import java.util.ArrayList;


public class Team7 extends AppCompatActivity {
    private final int LEFT = 1;
    private final int RIGHT = 2;

    private final String DATA = "DATA";
    private Team7CommentDialog comment;
    Button leftButton;
    Button rightButton;
    Button doneButton;
    Button feedbackButton;

    private long leftScore = 0;
    private long rightScore = 0;

    private int testCount = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team7);

        leftButton = (Button) findViewById(R.id.trace_instr_left);
        rightButton = (Button) findViewById(R.id.trace_instr_right);
        doneButton = (Button) findViewById(R.id.trace_inst_done);
        feedbackButton = (Button) findViewById(R.id.trace_feedback);

    }

    public void leftTest(View v){
        testCount++;
        leftButton.setVisibility(View.GONE);
        startActivityForResult(new Intent(this,Team7Trace.class),LEFT);
    }

    public void rightTest(View v){
        testCount++;
        rightButton.setVisibility(View.GONE);
        startActivityForResult(new Intent(this,Team7Trace.class),RIGHT);
    }

    public void doneTest(View v){
        Intent intent = new Intent(this, Team7Results.class);
        intent.putExtra(getString(R.string.team7_LEFT),""+leftScore);
        intent.putExtra(getString(R.string.team7_RIGHT),""+rightScore);
//        Toast.makeText(this,comment.getTextComment(), Toast.LENGTH_SHORT).show();
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (testCount == 2){
            doneButton.setVisibility(View.VISIBLE);
            feedbackButton.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1){
            if(resultCode == RESULT_OK) {
                leftScore = data.getLongExtra(DATA, 0) / 3;
                ArrayList<Object> list = ((ArrayList<Object>) data.getSerializableExtra(Team7Trace.DATA_LIST));
                list.addAll((ArrayList<Object>) data.getSerializableExtra(Team7Trace.METRIC_LIST));
            }
        }
        else if(requestCode == 2){
            if(resultCode == RESULT_OK) {
                rightScore = data.getLongExtra(DATA, 0);
                ArrayList<Object> list = ((ArrayList<Object>) data.getSerializableExtra(Team7Trace.DATA_LIST));
                list.addAll((ArrayList<Object>) data.getSerializableExtra(Team7Trace.METRIC_LIST));
            }
        }
        else {
        }
    }

    public void traceFeedback(View view) {
        comment= new Team7CommentDialog(this);
        comment.create().show();
    }
}