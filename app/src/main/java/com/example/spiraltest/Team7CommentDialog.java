package com.example.spiraltest;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;


public class Team7CommentDialog {
    AlertDialog.Builder alertDialogBuilder;
    String comment;


    public Team7CommentDialog(Context context){
        LayoutInflater li = LayoutInflater.from(context);
        View promptsView = li.inflate(R.layout.activity_team7_prompt,null);

        alertDialogBuilder = new AlertDialog.Builder(context);

        alertDialogBuilder.setView(promptsView);

        final EditText userInput = (EditText) promptsView.findViewById(R.id.prompt_text);

        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("Done", new DialogInterface.OnClickListener() {
                    String entered;
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        comment = userInput.getText().toString();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                comment = "None";
            }
        });


    }

    public AlertDialog create(){
        return alertDialogBuilder.create();

    }

    public String getTextComment(){return comment;}

}