package com.example.spiraltest;

import android.os.Parcel;
import android.os.Parcelable;


import java.util.Date;

/**
 * Created by AndrewLee on 2/17/17.
 */

public class Team15ResultsMain implements Parcelable {
    int value;
    String date;

    public Team15ResultsMain(int value,String date){
        this.value = value;
        this.date = date;
    }

    public Team15ResultsMain(Parcel data){
        date = data.readString();
        value = data.readInt();
    }

    public void setValue(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags){
        dest.writeString(date);
        dest.writeInt(value);
    }

    public static final Parcelable.Creator<Team15ResultsMain> CREATOR = new Parcelable.Creator<Team15ResultsMain>() {
        public Team15ResultsMain createFromParcel(Parcel data) {
            return new Team15ResultsMain(data);
        }

        public Team15ResultsMain[] newArray(int size) {
            return new Team15ResultsMain[size];
        }
    };

}
