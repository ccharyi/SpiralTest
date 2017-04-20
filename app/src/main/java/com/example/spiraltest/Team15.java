package com.example.spiraltest;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Locale;

public class Team15 extends AppCompatActivity {
    public static final String RESULT_IMAGE_URI = "result_image_uri";
    private static final int REQUEST_EXTERNAL_STORAGE = 0x1;

    private Team15DrawingView dv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_team15);

        dv = (Team15DrawingView) findViewById(R.id.spiral_test_drawing_view);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_EXTERNAL_STORAGE){
            for(int i = 0; i < permissions.length; i++){
                if(grantResults[i] != PackageManager.PERMISSION_GRANTED)
                    return;
                Log.d(getClass().getSimpleName(), "Req'd " + permissions[i] + " with result: " + grantResults[i]);
            }

            saveBitmap();
            //saveMetric();
        }
    }

    public void onSaveDrawing(View v){
        if ((ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
                && (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_EXTERNAL_STORAGE);
        }else {
            saveBitmap();
            //saveMetric();
        }
    }

//    public void saveMetric() {
//        double result = dv.calculateScore(dv.getUserPoints(), dv.getFixedPoints());
//
//        Intent i = new Intent(SpiralTestActivity.this, SpiralTestResultActivity.class);
//        i.putExtra("SpiralMetric", result);
//        startActivity(i);
//    }

    public void saveBitmap() {

        File file=new File(Environment.getExternalStorageDirectory()+"/spiralTest");
        if(!file.isDirectory()){
            file.mkdir();
        }

        file = new File(Environment.getExternalStorageDirectory()+"/spiralTest",System.currentTimeMillis()+".jpg");
        Uri fileUri = null;

        try {
            FileOutputStream out = new FileOutputStream(file);
            dv.getDrawingCache().compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();

            ContentValues values = new ContentValues();
            values.put(MediaStore.Images.Media.TITLE, "Spiral Test");
            values.put(MediaStore.Images.Media.DESCRIPTION, "Spiral Test for MS");
            values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis ());
            values.put(MediaStore.Images.ImageColumns.BUCKET_ID, file.toString().toLowerCase(Locale.US).hashCode());
            values.put(MediaStore.Images.ImageColumns.BUCKET_DISPLAY_NAME, file.getName().toLowerCase(Locale.US));
            values.put("_data", file.getAbsolutePath());

            ContentResolver cr = getContentResolver();
            fileUri = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent i = new Intent(Team15.this, Team15ResultActivity.class);

        if(fileUri != null)
            i.putExtra(RESULT_IMAGE_URI, fileUri.toString());

        //startActivity(i);

        double result = dv.calculateScore(dv.getUserPoints(), dv.getFixedPoints());

        //Intent i = new Intent(SpiralTestActivity.this, SpiralTestResultActivity.class);
        i.putExtra("SpiralMetric", result);
        startActivity(i);
    }


}