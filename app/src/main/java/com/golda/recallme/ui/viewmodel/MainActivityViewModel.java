package com.golda.recallme.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.golda.recallme.R;
import com.golda.recallme.alarm.AlarmClockBuilder;
import com.golda.recallme.alarm.db.AlarmDBUtils;
import com.golda.recallme.api.RetrofitApiProvider;
import com.golda.recallme.models.alarm.AlarmModel;
import com.golda.recallme.models.weather.WeatherResponseModel;
import com.golda.recallme.models.weather.WeatherState;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class MainActivityViewModel extends AndroidViewModel {

    public static final String WEEK_DAY = "Week day";
    private static final String BOOT = "boot";
    private static final String FLAG = "flag";

    LiveData<List<AlarmModel>> alarmList;
    MutableLiveData<WeatherResponseModel> weather;
    MutableLiveData<WeatherState> weatherState;

    public MainActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public LiveData<List<AlarmModel>> getAlarmList() {
        if (alarmList == null) {
            alarmList = AlarmDBUtils.queryLiveAlarmClock();
            init();
        }
        return alarmList;
    }


    private void init() {
        SharedPreferences sharedPreferences = getApplication().getApplicationContext().getSharedPreferences(BOOT, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean flag = sharedPreferences.getBoolean(FLAG, false);

        if (!flag) {
            initDB();
            editor.putBoolean(FLAG, true);
            editor.apply();
        }

    }

    private void initDB() {
        AlarmClockBuilder clockBuilder = new AlarmClockBuilder();
        AlarmModel alarmM = clockBuilder.enable(false)
                .hour(7)
                .minute(0)
                .repeat(WEEK_DAY)
                .sunday(false)
                .monday(true)
                .tuesday(true)
                .wednesday(true)
                .thursday(true)
                .friday(true)
                .saturday(false)
                .ringPosition(0)
                .ring(firstRing(getApplication()))
                .volume(10)
                .vibrate(true)
                .remind(3)
                .weather(true)
                .build();

        AlarmDBUtils.insertLiveAlarmClock(alarmM);
    }

    private String firstRing(Context context) {
        RingtoneManager ringtoneManager = new RingtoneManager(context);
        ringtoneManager.setType(RingtoneManager.TYPE_ALARM);
        Cursor cursor = ringtoneManager.getCursor();
        String ringName = null;

        while (cursor.moveToNext()) {
            ringName = cursor.getString(RingtoneManager.TITLE_COLUMN_INDEX);
            if (ringName != null) {
                break;
            }
        }
        return ringName;
    }

    public void updateWeather() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplication().getApplicationContext());
        String city = prefs.getString(getApplication().getString(R.string.pref_city_key), getApplication().getString(R.string.pref_city_default));
        updateWeather(city);
    }

    public LiveData<WeatherResponseModel> getWeather() {
        if (weather == null) {
            weather = new MutableLiveData<>();
            updateWeather();
        }
        return weather;
    }

    public LiveData<WeatherState> getWeatherState() {
        if (weatherState == null) {
            weatherState = new MutableLiveData<>();
            updateWeather();
        }
        return weatherState;
    }

    private void updateWeather(String city) {
        RetrofitApiProvider apiProvider = new RetrofitApiProvider();


        apiProvider.getWeather(city, new Callback<WeatherResponseModel>() {
            @Override
            public void onResponse(Call<WeatherResponseModel> call, Response<WeatherResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    weatherState.setValue(WeatherState.UPDATED);
                    weather.setValue(response.body());
                }
            }

            @Override
            public void onFailure(Call<WeatherResponseModel> call, Throwable t) {
                weatherState.setValue(WeatherState.ERROR);
            }
        });
    }


}
