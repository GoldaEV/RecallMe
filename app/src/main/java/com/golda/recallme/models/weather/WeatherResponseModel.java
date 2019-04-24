package com.golda.recallme.models.weather;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Evgeniy on 02.04.2019.
 */

public class WeatherResponseModel {

    @SerializedName("id")
    private String id;
    @SerializedName("name")
    private String name;
    @SerializedName("base")
    private String base;
    @SerializedName("weather")
    private Weather weathers[];
    @SerializedName("main")
    private Main main;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBase() {
        return base;
    }

    public void setBase(String base) {
        this.base = base;
    }

    public Weather[] getWeathers() {
        return weathers;
    }

    public void setWeathers(Weather[] weathers) {
        this.weathers = weathers;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }
}
