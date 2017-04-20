package com.example.spiraltest;

import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;


import static com.example.spiraltest.Team8SpiralScoreFragment.newInstance;
import static com.example.spiraltest.Team8SpiralTestFragment.newInstance;

public class Team8 extends FragmentActivity implements Team8SpiralTestFragment.OnFinishListener,
        Team8SpiralScoreFragment.FinishSpiralTestListener {
    private static final String[] TRIAL_ORDER = {"right hand", "left hand", "right hand", "left hand", "right hand", "left hand"};
    private static final int[] RIGHT_HAND_TRIALS = {0, 2, 4};
    private static final int[] LEFT_HAND_TRIALS = {1, 3, 5};
    protected static final String RESULT_KEY = "RESULT_KEY";
    protected static final String ROUND_KEY = "ROUND_KEY";
    //    private static final int PERMISSION_REQUEST_CODE = 1;
    private FragmentManager fragmentManager;
    private FragmentTransaction transaction;
    private int roundNumber;
    private static float[] scores;
    private double[] durations;
    private boolean hasBeenResumed;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team8);
        fragmentManager = getSupportFragmentManager();
        transaction = fragmentManager.beginTransaction();
        roundNumber = 0;
        scores = new float[6];
        durations = new double[6];
        hasBeenResumed = false;


        // place initial test in view automatically
        Team8SpiralTestFragment fragment = newInstance(TRIAL_ORDER[roundNumber], roundNumber+1);
        transaction.add(R.id.fragmentContainer, fragment).addToBackStack(null).commit();
    }

    @Override
    public void onBackPressed() {
        // disable back button
    }

    @Override
    public void onResume() {
        super.onResume();

        if (roundNumber < TRIAL_ORDER.length && hasBeenResumed) {
            // replace old fragment for new fragment
            Team8SpiralTestFragment fragment = newInstance(TRIAL_ORDER[roundNumber], roundNumber+1);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
        hasBeenResumed = true;
    }

    public void onFinish(float score, long duration) {
        durations[roundNumber] = duration;
        scores[roundNumber++] = score;

        if (roundNumber < TRIAL_ORDER.length) {
            // show next trial
            Team8SpiralTestFragment fragment = newInstance(TRIAL_ORDER[roundNumber], roundNumber+1);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        } else {
            // show score fragment
            Team8SpiralScoreFragment fragment = newInstance(TRIAL_ORDER, RIGHT_HAND_TRIALS, LEFT_HAND_TRIALS, scores);
            transaction = fragmentManager.beginTransaction();
            transaction.replace(R.id.fragmentContainer, fragment);
            transaction.addToBackStack(null);
            transaction.commit();
        }
    }

    public void goHome() {
        finish();
    }

}
