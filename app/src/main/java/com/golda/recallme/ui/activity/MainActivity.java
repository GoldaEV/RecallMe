package com.golda.recallme.ui.activity;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.golda.recallme.models.alarm.AlarmModel;
import com.golda.recallme.models.weather.Weather;
import com.golda.recallme.models.weather.WeatherResponseModel;
import com.golda.recallme.models.weather.WeatherState;
import com.golda.recallme.service.AlarmClockService;
import com.golda.recallme.ui.adapter.AlarmAdapter;
import com.golda.recallme.ui.viewmodel.MainActivityViewModel;
import com.golda.recallme.weather.WeatherTempConverter;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import hotchemi.android.rate.AppRate;

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

    private AlarmAdapter alarmAdapter;
    private MainActivityViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);
        viewModel = ViewModelProviders.of(this).get(MainActivityViewModel.class);

        ButterKnife.bind(this);
        setSupportActionBar(toolbar);
        weatherIcon.setVisibility(View.INVISIBLE);


        AppRate.with(this)
                .setInstallDays(3)
                .setLaunchTimes(3)
                .setRemindInterval(2)
                .setShowLaterButton(true)
                .setDebug(false)
                .setOnClickButtonListener(which -> Log.d(MainActivity.class.getName(), Integer.toString(which)))
                .monitor();

        AppRate.showRateDialogIfMeetsConditions(this);

        LiveData<WeatherState> weatherState = viewModel.getWeatherState();
        weatherState.observe(this, weatherStateObserver);

        LiveData<WeatherResponseModel> weather = viewModel.getWeather();
        weather.observe(this, weatherObserver);
        viewModel.updateWeather();


        LiveData<List<AlarmModel>> alarmList = viewModel.getAlarmList();

        alarmList.observe(this, alarmModels -> setData(alarmModels));

        alarmAdapter = new AlarmAdapter(this);

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

    @OnClick(R.id.ib_refresh)
    public void OnRefresh() {
        viewModel.updateWeather();
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

    @Override
    public void onItemClick(AlarmModel model) {
        startActivity(new Intent(EditAlarmActivity.newIntent(this, model.id)));
    }

    @Override
    public void onLongClick(int id) {
        startActivity(new Intent(DeleteActivity.newIntent(MainActivity.this, id)));
    }

    private Observer<WeatherResponseModel> weatherObserver = new Observer<WeatherResponseModel>() {
        @Override
        public void onChanged(@Nullable WeatherResponseModel weatherResponseModel) {

            Weather weather[] = weatherResponseModel.getWeathers();
            tvTitle.setText(R.string.current_weather);
            tvLocation.setText(weatherResponseModel.getName());
            tvCondition.setText(weather[0].getMain());

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
            String tFormat = preferences.getString(getString(R.string.pref_temp_format_key), getString(R.string.pref_default_temp_format));

            if (tFormat.equals(getString(R.string.pref_temp_celsius))) {
                tvTemp.setText(WeatherTempConverter.convertToCelsius(weatherResponseModel.getMain().getTemp()).intValue() + getString(R.string.cel));
            } else {
                tvTemp.setText(WeatherTempConverter.convertToFahrenheit(weatherResponseModel.getMain().getTemp()).intValue() + getString(R.string.fahr));
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
    };

    private Observer<WeatherState> weatherStateObserver = new Observer<WeatherState>() {
        @Override
        public void onChanged(@Nullable WeatherState weatherState) {
            if (weatherState != null) {
                switch (weatherState) {
                    case UPDATED:
                        Snackbar.make(cLayout, R.string.weather_update, Snackbar.LENGTH_SHORT)
                                .setActionTextColor(Color.RED)
                                .show();
                        weatherIcon.setVisibility(View.VISIBLE);
                        break;
                    case ERROR:
                        Snackbar.make(cLayout, R.string.failure_network, Snackbar.LENGTH_SHORT)
                                .setActionTextColor(Color.RED)
                                .show();
                        tvTitle.setText(R.string.failure_network);
                        weatherIcon.setVisibility(View.VISIBLE);
                        weatherIcon.setIcon(MaterialDrawableBuilder.IconValue.WIFI_OFF);
                        break;
                }
            }
        }
    };

}
