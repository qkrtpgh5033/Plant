package com.example.plant;

import android.os.Parcel;
import android.os.Parcelable;
import android.widget.TextView;

public class Data implements Parcelable {
//    private static final long serialVersionUID = 1L;
    String test;
    static TextView temp;
    static TextView humi;
    static TextView soil_humi;


    public Data(){

    }
    protected Data(Parcel in) {
    }

    public static final Creator<Data> CREATOR = new Creator<Data>() {
        @Override
        public Data createFromParcel(Parcel in) {
            return new Data(in);
        }

        @Override
        public Data[] newArray(int size) {
            return new Data[size];
        }
    };

    //    public Data(){
//
//    }
//
//
//
    public TextView getTemp() {
        return temp;
    }

    public void setTemp(TextView temp) {
        this.temp = temp;
    }

    public TextView getHumi() {
        return humi;
    }

    public void setHumi(TextView humi) {
        this.humi = humi;
    }

    public TextView getSoil_humi() {
        return soil_humi;
    }

    public void setSoil_humi(TextView soil_humi) {
        this.soil_humi = soil_humi;
    }



    static public void check()
    {
        temp.setText("test");
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {

    }
}
