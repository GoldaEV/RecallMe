package com.golda.recallme.ui.activity;

import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.golda.recallme.R;
import com.golda.recallme.alarm.AlarmClockBuilder;
import com.golda.recallme.alarm.db.AlarmDBUtils;
import com.golda.recallme.api.RetrofitApiProvider;
import com.golda.recallme.models.alarm.AlarmModel;
import com.golda.recallme.models.weather.Weather;
import com.golda.recallme.models.weather.WeatherResponseModel;
import com.golda.recallme.service.AlarmClockService;
import com.golda.recallme.ui.adapter.AlarmAdapter;
import com.golda.recallme.weather.WeatherTempConverter;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hotchemi.android.rate.AppRate;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements AlarmAdapter.AlarmAdapterInterface {

    @BindView(R.id.clayout)
    CoordinatorLayout cLayout;
    @BindView(R.id.add_alarmlist)
    RecyclerView recyclerView;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.image_weather_icon)
    MaterialIconView weatherIcon;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_condition)
    TextView tvCondition;
    @BindView(R.id.tv_temp)
    TextView tvTemp;
    @BindView(R.id.tv_weather_title)
    TextView tvTitle;

    private List<AlarmModel> alarmList;
    private AlarmAdapter alarmAdapter;

    public static final String WEEK_DAY = "Week day";
    private static final String BOOT = "boot";
    private static final String FLAG = "flag";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        weatherIcon.setVisibility(View.INVISIBLE);
        alarmList = new ArrayList<>();

        AppRate.with(this)
                .setInstallDays(3)
                .setLaunchTimes(3)
                .setRemindInterval(2)
                .setShowLaterButton(true)
                .setDebug(false)
                .setOnClickButtonListener(which -> Log.d(MainActivity.class.getName(), Integer.toString(which)))
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String city = prefs.getString(getString(R.string.pref_city_key), getString(R.string.pref_city_default));
        loadWeather(city);

        init();

        alarmAdapter = new AlarmAdapter(this, alarmList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(alarmAdapter);
        startService(new Intent(this, AlarmClockService.class));
    }

    private void setData(List<AlarmModel> list) {
        alarmAdapter.setData(list);
    }

    @OnClick(R.id.floating_action_btn)
    public void OnFABClick() {
        startActivity(AddAlarmActivity.newIntent(MainActivity.this));
    }

    private void loadWeather(String city) {
        RetrofitApiProvider apiProvider = new RetrofitApiProvider();
        apiProvider.getWeather(city, new Callback<WeatherResponseModel>() {
            @Override
            public void onResponse(Call<WeatherResponseModel> call, Response<WeatherResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Snackbar.make(cLayout, R.string.weather_update, Snackbar.LENGTH_SHORT)
                            .setActionTextColor(Color.RED)
                            .show();
                    weatherIcon.setVisibility(View.VISIBLE);
                    populateWeather(response);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponseModel> call, Throwable t) {
                Snackbar.make(cLayout, R.string.failure_network, Snackbar.LENGTH_SHORT)
                        .setActionTextColor(Color.RED)
                        .show();
                tvTitle.setText(R.string.failure_network);
                weatherIcon.setVisibility(View.VISIBLE);
                weatherIcon.setIcon(MaterialDrawableBuilder.IconValue.WIFI_OFF);
            }
        });
    }

    private void populateWeather(Response<WeatherResponseModel> response) {
        Weather weather[] = response.body().getWeathers();
        tvTitle.setText(R.string.current_weather);
        tvLocation.setText(response.body().getName());
        tvCondition.setText(weather[0].getMain());

        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        String tFormat = preferences.getString(getString(R.string.pref_temp_format_key), getString(R.string.pref_default_temp_format));

        if (tFormat.equals(getString(R.string.pref_temp_celsius))) {
            tvTemp.setText(WeatherTempConverter.convertToCelsius(response.body().getMain().getTemp()).intValue() + getString(R.string.cel));
        } else {
            tvTemp.setText(WeatherTempConverter.convertToFahrenheit(response.body().getMain().getTemp()).intValue() + getString(R.string.fahr));
        }

        switch (weather[0].getIcon()) {
            case "01d":
                weatherIcon.setIcon(MaterialDrawableBuilder.IconValue.WEATHER_SUNNY);
                break;
            case "01n":
                weatherIcon.setIcon(MaterialDrawableBuilder.IconValue.WEATHER_NIGHT);
                break;
            case "02d":
            case "02n":
                weatherIcon.setIcon(MaterialDrawableBuilder.IconValue.WEATHER_PARTLYCLOUDY);
                break;
            case "03d":
            case "03n":
                weatherIcon.setIcon(MaterialDrawableBuilder.IconValue.WEATHER_CLOUDY);
                break;
            case "04d":
            case "04n":
                weatherIcon.setIcon(MaterialDrawableBuilder.IconValue.WEATHER_CLOUDY);
                break;
            case "09d":
            case "09n":
                weatherIcon.setIcon(MaterialDrawableBuilder.IconValue.WEATHER_RAINY);
                break;
            case "10d":
            case "10n":
                weatherIcon.setIcon(MaterialDrawableBuilder.IconValue.WEATHER_RAINY);
                break;
            case "11d":
            case "11n":
                weatherIcon.setIcon(MaterialDrawableBuilder.IconValue.WEATHER_LIGHTNING);
                break;
            case "13d":
            case "13n":
                weatherIcon.setIcon(MaterialDrawableBuilder.IconValue.WEATHER_SNOWY);
                break;
            case "50d":
            case "50n":
                weatherIcon.setIcon(MaterialDrawableBuilder.IconValue.WEATHER_FOG);
                break;
        }
    }

    @OnClick(R.id.ib_refresh)
    public void OnRefresh() {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String city = prefs.getString(getString(R.string.pref_city_key), getString(R.string.pref_city_default));
        loadWeather(city);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
                .ring(firstRing(this))
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

    @Override
    public void onItemClick(AlarmModel model) {
        startActivity(new Intent(EditAlarmActivity.newIntent(this, model.id)));
    }

    @Override
    public void onLongClick(int id) {
        startActivity(new Intent(DeleteActivity.newIntent(MainActivity.this, id)));
    }

    private void init() {
        SharedPreferences sharedPreferences = getSharedPreferences(BOOT, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        boolean flag = sharedPreferences.getBoolean(FLAG, false);

        if (!flag) {
            initDB();
            editor.putBoolean(FLAG, true);
            editor.apply();
        }

        AlarmDBUtils.queryLiveAlarmClock().observe(this, new Observer<List<AlarmModel>>() {
            @Override
            public void onChanged(@Nullable List<AlarmModel> alarmModels) {
                alarmList = alarmModels;
                setData(alarmList);
            }
        });
    }
}
