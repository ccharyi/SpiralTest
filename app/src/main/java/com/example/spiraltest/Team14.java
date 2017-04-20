package com.example.spiraltest;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.TextView;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Team14 extends AppCompatActivity implements OnClickListener {
    private Team14DrawingView drawView;
    private ImageButton currPaint, newBtn, saveBtn;
    private int handSelected;
    private float smallBrush, hLeft, hRight;
    //private Drawable spiralImg;
    private TextView tv;
    private Map<String, String> data;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team14);

        newBtn = (ImageButton)findViewById(R.id.new_btn);
        newBtn.setOnClickListener(this);

        saveBtn = (ImageButton)findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(this);

        smallBrush = 10;

        drawView = (Team14DrawingView)findViewById(R.id.drawing);
        //LinearLayout paintLayout = (LinearLayout)findViewById(R.id.hand_selected);
        //handSelected = (ImageButton)paintLayout.getChildAt(0);
        //handSelected.setImageDrawable(getResources().getDrawable(R.drawable.paint_pressed));
        handSelected = 1;

        tv = (TextView) findViewById(R.id.hausdorff);

        drawView.setBrushSize(smallBrush);
    }

    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.left_hand_radio:
                if (checked)
                    handSelected = 1;
                break;
            case R.id.right_hand_radio:
                if (checked)
                    handSelected = 2;
                break;
        }
    }

    @Override
    public void onClick(View view){
        if(view.getId()==R.id.new_btn){
            //new button
            AlertDialog.Builder newDialog = new AlertDialog.Builder(this);
            newDialog.setTitle("New drawing");
            newDialog.setMessage("Start new drawing (you will lose the current drawing)?");
            newDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    tv.setText("Score:");
                    drawView.destroyDrawingCache();
                    drawView.resetValues();
                    drawView.startNew();
                    dialog.dismiss();
                }
            });
            newDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            newDialog.show();
        } else if(view.getId()==R.id.save_btn){

            float h = -1;

            for (Team14Coordinate c1: drawView.getMasterSpiral()){
                h = Math.max(h, c1.hausdorffDist(drawView.getTouches()));
            }

            h = Math.max(h, drawView.getHausdorff());

            if(handSelected == 1) { //send results for left hand
                hLeft = h;
            } else { //send results for right hand
                hRight = h;
                Team14Database.getInstance().addSpiralTest(new Team14SpiralHelper(hLeft, hRight, new Date()));
            }


            tv.setText("Results saved successfully\nScore: " + Float.toString(h));

            //save drawing
            /*
            AlertDialog.Builder saveDialog = new AlertDialog.Builder(this);
            saveDialog.setTitle("Save drawing");
            saveDialog.setMessage("Save drawing to device Gallery?");
            saveDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    //save drawing
                    drawView.setDrawingCacheEnabled(true);
                    String imgSaved = MediaStore.Images.Media.insertImage(
                            getContentResolver(), drawView.getDrawingCache(),
                            UUID.randomUUID().toString()+".png", "drawing");
                    if(imgSaved!=null){
                        Toast savedToast = Toast.makeText(getApplicationContext(),
                                "Drawing saved to Gallery!", Toast.LENGTH_SHORT);
                        savedToast.show();
                    }
                    else{
                        Toast unsavedToast = Toast.makeText(getApplicationContext(),
                                "Oops! Image could not be saved.", Toast.LENGTH_SHORT);
                        unsavedToast.show();
                    }
                    drawView.destroyDrawingCache();
                }
            });
            saveDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            saveDialog.show();
            */
        }
    }
}
