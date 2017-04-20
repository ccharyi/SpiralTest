package com.example.spiraltest;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Fullscreen, and holds a custom view for tracing
 */

public class Team3TracerActivity extends AppCompatActivity {

    private final int requestCode = 436;
    private boolean right;
    private boolean runOtherHand = true;
    private int trialAmount;
    private int tempTrialAmount;
    private TextView message;
    private TextView trialMessage;
    private Team3TracerView tv;

    @Override
    public void onCreate (final Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_team3_tracer);
        right = getIntent().getExtras().getBoolean("right_hand");
        trialAmount = getIntent().getExtras().getInt("amountTrials");
        tempTrialAmount = trialAmount;
        message = (TextView) findViewById(R.id.textView6);
        trialMessage = (TextView) findViewById(R.id.textView7);
        setHandMessage();
        setTrialMessage();
        tv = (Team3TracerView) findViewById(R.id.tracer_view_right);
        setTraceMode();
        tv.setOnFinishListener(new Runnable() {
            @Override
            public void run() {
                saveTracerView();
                tv.setEnabled(true);
                if(runOtherHand){
                    if(tempTrialAmount > 1){
                        tempTrialAmount--;
                        setTraceMode();
                        setTrialMessage();
                    }else{
                        runOtherHand = false;
                        right = !right;
                        setHandMessage();
                        tempTrialAmount = trialAmount;
                        setTraceMode();
                        setTrialMessage();
                    }
                }else{
                    setHandMessage();
                    if(tempTrialAmount > 1){
                        tempTrialAmount--;
                        setTraceMode();
                        setTrialMessage();
                    }else{
                        trialMessage.setText("Finished");
                        finish();
                    }
                }
            }
        });
        findViewById(R.id.temp_test_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (ContextCompat.checkSelfPermission(Team3TracerActivity.this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Team3TracerActivity.this,
                            new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            requestCode);
                } else {
                    saveTracerView();
                }
            }
        });
    }
    private void setTrialMessage(){
        String message = "Trial: "+String.valueOf((trialAmount+1)-tempTrialAmount)+"/"+String.valueOf(trialAmount);
        trialMessage.setText(message);
    }
    private void setHandMessage(){
        if(right){
            message.setText("Right Hand");
        }else{
            message.setText("Left Hand");
        }
    }
    private void setTraceMode(){
        if(right){
            tv.setTraceMode(0);
        }else{
            tv.setTraceMode(1);
        }

    }
    @Override
    public void onRequestPermissionsResult (int requestCode,
                                            @NonNull String permissions[], @NonNull int[] grantResults) {
        if (requestCode == this.requestCode) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveTracerView();
            }
        }
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus) {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY |
                        View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        );
    }

    private void saveTracerView() {

        if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
            // External media not mounted, so nothing to write
            return;
        }

        Bitmap bm = Bitmap.createBitmap(tv.getWidth(), tv.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(bm);
        tv.onSave(c);

        String dateTime = getDateTime();
        File dir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "SpiralTrial_"+dateTime);
        if (!dir.mkdirs()) {
            // error when creating directory
            return;
        }
        File imageFile = new File(dir, getString(R.string.team3_spiral_trial_name));
        File userCSV = new File(dir, getString(R.string.team3_user_spiral_data_name));
        File predrawnCSV = new File(dir, getString(R.string.team3_predraw_spiral_data_name));
        File properties = new File(dir, getString(R.string.team3_user_metadata_name));

        try {
            FileOutputStream out = new FileOutputStream(imageFile);
            bm.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

            tv.getUserTrace().writeToFile(userCSV);
            if (tv.getPredrawn().getNTraces() > 0) {
                tv.getPredrawn().writeToFile(predrawnCSV);
            }
            writeProperties(properties);

            Toast.makeText(this, "Saved " + dateTime, Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Log.e(this.getClass().toString(), e.getMessage());
        }
    }

    private void writeProperties (File f) throws FileNotFoundException {
        // TODO actually get user info

        FileOutputStream fileOutputStream = new FileOutputStream(f);
        PrintStream printStream = new PrintStream(fileOutputStream);
        printStream.println("mode="+tv.getTraceMode()); // TODO translate to human readable
        printStream.println("ntraces="+tv.getUserTrace().getNTraces());
    }

    private String getDateTime () {
        DateFormat dateFormat = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss", Locale.US);
        return dateFormat.format(new Date(System.currentTimeMillis()));
    }
}