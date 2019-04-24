package com.golda.recallme.api;


import com.golda.recallme.models.weather.WeatherResponseModel;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by Evgeniy on 02.04.2019.
 */

public interface OpenWeatherService {

    @GET("weather")
    Call<WeatherResponseModel> getWeather(@Query("q") String city, @Query("APPID") String apiKey);
}
