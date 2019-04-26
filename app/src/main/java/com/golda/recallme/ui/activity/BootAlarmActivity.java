package com.golda.recallme.ui.activity;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.golda.recallme.R;
import com.golda.recallme.alarm.AlarmClockBuilder;
import com.golda.recallme.api.RetrofitApiProvider;
import com.golda.recallme.models.alarm.AlarmModel;
import com.golda.recallme.models.weather.Weather;
import com.golda.recallme.models.weather.WeatherResponseModel;
import com.golda.recallme.weather.WeatherTempConverter;

import net.steamcrafted.materialiconlib.MaterialDrawableBuilder;
import net.steamcrafted.materialiconlib.MaterialIconView;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * Created by Evgeniy on 7.04.2019.
 */
public class BootAlarmActivity extends AppCompatActivity {

    @BindView(R.id.weather_icon)
    MaterialIconView weatherIcon;
    @BindView(R.id.wifi_icon)
    MaterialIconView wifiIcon;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_condition)
    TextView tvCondition;
    @BindView(R.id.tv_temp)
    TextView tvTemp;
    @BindView(R.id.rl_boot_alarm_off)
    RelativeLayout rvAlarmOff;

    private AlarmModel alarm;
    private MediaPlayer mediaPlayer;
    private Vibrator vibration;

    public static final String ONLY_ONCE = "Only once";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_boot_alarm);
        ButterKnife.bind(this);

        wifiIcon.setVisibility(View.INVISIBLE);
        weatherIcon.setVisibility(View.INVISIBLE);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String city = prefs.getString(getString(R.string.pref_city_key), getString(R.string.pref_city_default));

        int alarm_clock = getIntent().getIntExtra("ALARM_CLOCK", 0);

        alarm = new AlarmClockBuilder().build();
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        final int remind = alarm.remind;

        startPlayingRing();
        if (alarm.vibrate) {
            startVibrate();
        }

        if (alarm.weather) {
            loadWeather(city);
        }

        rvAlarmOff.setOnClickListener(v -> {
            finish();
        });
    }

    private void loadWeather(String city) {
        RetrofitApiProvider apiProvider = new RetrofitApiProvider();
        apiProvider.getWeather(city, new Callback<WeatherResponseModel>() {
            @Override
            public void onResponse(Call<WeatherResponseModel> call, Response<WeatherResponseModel> response) {
                if (response.isSuccessful() && response.body() != null) {
                    weatherIcon.setVisibility(View.VISIBLE);
                    populateWeather(response);
                }
            }

            @Override
            public void onFailure(Call<WeatherResponseModel> call, Throwable t) {
                wifiIcon.setVisibility(View.VISIBLE);
                wifiIcon.setIcon(MaterialDrawableBuilder.IconValue.WIFI_OFF);
            }
        });
    }

    private void populateWeather(Response<WeatherResponseModel> response) {
        Weather weather[] = response.body().getWeathers();
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

    private void stopVibrate() {
        vibration.cancel();
    }

    private void startVibrate() {
        vibration = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] pattern = new long[]{1000, 1000, 1000, 1000};
        vibration.vibrate(pattern, 2);
    }

    private void stopPlayRing() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
    }

    private void startPlayingRing() {
        RingtoneManager ringtoneManager = new RingtoneManager(this);
        ringtoneManager.setType(RingtoneManager.TYPE_ALARM);
        ringtoneManager.getCursor();

        Uri ringtoneUri = ringtoneManager.getRingtoneUri(alarm.ringPosition);
        AudioManager audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_ALARM, 7, AudioManager.ADJUST_SAME);
        mediaPlayer = new MediaPlayer();
        try {
            mediaPlayer.setDataSource(this, ringtoneUri);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_ALARM);
            mediaPlayer.prepare();
            mediaPlayer.start();
            mediaPlayer.setLooping(true);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        stopPlayRing();
        if (alarm.vibrate) {
            stopVibrate();
        }
    }
}
