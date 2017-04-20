package com.example.spiraltest;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.UUID;


public class Team5SpiralTestMain extends Activity{


    private static final int LIB_ACCOUNT_NAME_REQUEST_CODE = 1001;
    private static final int LIB_AUTHORIZATION_REQUEST_CODE = 1002;
    private static final int LIB_PERMISSION_REQUEST_CODE = 1003;
    private static final int LIB_PLAY_SERVICES_REQUEST_CODE = 1004;


    static Team5SpiralTestMain activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team5_spiral_main);
        activity = this;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 0);
        }
    }

    public void sentToSheets(float score){


    }

    public void takeScreenshot()
    {
        Team5DrawingView view = (Team5DrawingView) findViewById(R.id.drawing_view);
        MediaStore.Images.Media.insertImage(
                getContentResolver(), view.getDrawingCache(),
                UUID.randomUUID().toString()+".png", "drawing");
        view.destroyDrawingCache();
        view.startNew();
    }

    public void takeScreenshot1() {
        Date now = new Date();
        android.text.format.DateFormat.format("yyyy-MM-dd_hh:mm:ss", now);
        try {
            // image naming and path  to include sd card  appending name you choose for file
            String mPath = Environment.getExternalStorageDirectory().toString() + "/" + now + ".jpg";

            // create bitmap screen capture
            View v1 = getWindow().getDecorView().getRootView();
            v1.setDrawingCacheEnabled(true);
            Bitmap bitmap = Bitmap.createBitmap(v1.getDrawingCache());
            v1.setDrawingCacheEnabled(false);

            File imageFile = new File(mPath);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();

            MediaScannerConnection.scanFile(this,
                    new String[]{imageFile.toString()}, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });
        } catch (Throwable e) {
            // Several error may come out with file handling or OOM
            e.printStackTrace();
        }
        Team5DrawingView view = (Team5DrawingView) findViewById(R.id.drawing_view);
        view.startNew();


    }
}