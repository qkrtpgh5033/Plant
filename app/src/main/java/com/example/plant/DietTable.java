package com.example.plant;

public class DietTable {
    String date;
    String lunch;
    String dinner;

    public DietTable(String date, String lunch, String dinner)
    {
        this.date= date;
        this.lunch = lunch;
        this.dinner = dinner;
    }


    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLunch() {
        return lunch;
    }

    public void setLunch(String lunch) {
        this.lunch = lunch;
    }

    public String getDinner() {
        return dinner;
    }

    public void setDinner(String dinner) {
        this.dinner = dinner;
    }



}
