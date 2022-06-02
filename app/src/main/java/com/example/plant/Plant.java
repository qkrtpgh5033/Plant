package com.example.plant;

public class Plant {

    private long humi;
    private long soil_humi;
    private long temp;

    public Plant()
    {

    }
    public Plant(long humi, long soil_humi, long temp) {

        this.humi = humi;
        this.soil_humi = soil_humi;
        this.temp = temp;

    }

    public long getHumi() {
        return humi;
    }

    public void setHumi(long humi) {
        this.humi = humi;
    }

    public long getSoil_humi() {
        return soil_humi;
    }

    public void setSoil_humi(long soil_humi) {
        this.soil_humi = soil_humi;
    }

    public long getTemp() {
        return temp;
    }

    public void setTemp(long temp) {
        this.temp = temp;
    }




}
