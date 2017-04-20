package com.example.spiraltest;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.example.spiraltest.R;

public class Team3 extends AppCompatActivity implements View.OnClickListener{

    Button right;
    Button left;
    Button settings;
    private int amountTrials = 1;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.team3_main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team3);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setTitle("Tracing Test");

        left = (Button)findViewById(R.id.left_hand);
        right = (Button)findViewById(R.id.right_hand);

        left.setOnClickListener(this);
        right.setOnClickListener(this);

        SharedPreferences spf = PreferenceManager.getDefaultSharedPreferences(this);
        String storeTrials = spf.getString(getString(R.string.team3_key_trials), "1");
        amountTrials = Integer.parseInt(storeTrials);
    }

    private void settingsButtonClick() {
        Intent intent = new Intent(this, Team3Settings.class);
        startActivity(intent);
    }

    private void continueButtonClick(boolean right){
        Intent intent = new Intent(Team3.this, Team3TracerActivity.class);
        intent.putExtra("right_hand", right);
        intent.putExtra("amountTrials", amountTrials);
        startActivity(intent);
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.settings:
                settingsButtonClick();
                return true;
            case R.id.results:
                resultButtonClick();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void resultButtonClick() {
        Intent intent = new Intent("ms_app.results");
        startActivity(intent);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.left_hand:
                continueButtonClick(false);
                break;
            case R.id.right_hand:
                continueButtonClick(true);
                break;
        }
    }
}
