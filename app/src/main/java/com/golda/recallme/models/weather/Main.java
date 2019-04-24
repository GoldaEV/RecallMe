package com.golda.recallme.models.weather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Evgeniy on 02.04.2019.
 */

public class Main {

    @SerializedName("temp")
    private String temp;

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
