package com.golda.recallme.ui.activity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;

import com.golda.recallme.R;
import com.golda.recallme.alarm.AlarmClockBuilder;
import com.golda.recallme.alarm.AlarmManagerHelper;
import com.golda.recallme.alarm.db.AlarmDBUtils;
import com.golda.recallme.models.alarm.AlarmModel;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Evgeniy on 21.03.2019.
 */
public class AddAlarmActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.toolbar_title)
    TextView toolbarTitle;
    @BindView(R.id.alarm_cv_repeat)
    CardView cvRepeat;
    @BindView(R.id.repeat_content)
    TextView tvRepeat;
    @BindView(R.id.alarm_cv_ring)
    CardView cvRing;
    @BindView(R.id.ringtones_content)
    TextView tvRingtones;
    @BindView(R.id.alarm_cv_remind)
    CardView cvRemind;
    @BindView(R.id.remind_content)
    TextView tvRemind;
    @BindView(R.id.switch_vibration)
    SwitchCompat switchVibration;
    @BindView(R.id.switch_weather)
    SwitchCompat switchWeather;

    public static TextView tvHours;
    public static TextView tvMin;

    private static AlarmModel alarmClockLab;

    public static Intent newIntent(Context context) {
        return new Intent(context, AddAlarmActivity.class);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);

        tvHours = findViewById(R.id.alarm_time_hours);
        tvMin = findViewById(R.id.alarm_time_min);

        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowTitleEnabled(false);
        toolbarTitle.setText(R.string.addAlarmActivityTitle);

        initUI();
    }

    private void initUI() {
        alarmClockLab = new AlarmClockBuilder().build();
        alarmClockLab.setEnable(true);
        int[] currentTime = getCurrentTime();
        alarmClockLab.setHour(currentTime[0]);
        alarmClockLab.setMinute(currentTime[1]);
        alarmClockLab.setRepeat(getString(R.string.repeatWeekDay));
        alarmClockLab.setSunday(false);
        alarmClockLab.setMonday(true);
        alarmClockLab.setTuesday(true);
        alarmClockLab.setWednesday(true);
        alarmClockLab.setThursday(true);
        alarmClockLab.setFriday(true);
        alarmClockLab.setSaturday(false);
        alarmClockLab.setRingPosition(0);
        alarmClockLab.setRing(firstRing(this));
        alarmClockLab.setVolume(10);
        alarmClockLab.setVibrate(false);
        alarmClockLab.setRemind(3);
        alarmClockLab.setWeather(false);

        tvRingtones.setText(alarmClockLab.ring);
        switchVibration.setChecked(alarmClockLab.vibrate);
        cvRepeat.setOnClickListener(this);
        cvRing.setOnClickListener(this);
        cvRemind.setOnClickListener(this);
        switchWeather.setChecked(alarmClockLab.weather);

        int hour = alarmClockLab.hour;
        int minute = alarmClockLab.minute;

        String h = String.valueOf(hour);
        String m = String.valueOf(minute);

        if (minute < 10) {
            m = "0" + minute;
        }
        if (hour < 10) {
            h = "0" + hour;
        }
        tvHours.setText(h);
        tvMin.setText(m);

        switchVibration.setOnCheckedChangeListener((buttonView, isChecked) -> alarmClockLab.setVibrate(isChecked));

        switchWeather.setOnCheckedChangeListener((buttonView, isChecked) -> alarmClockLab.setWeather(isChecked));

    }

    @OnClick(R.id.alarm_cv_time)
    public void OnTimeClick() {
        DialogFragment dialogFragment = new TimePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @OnClick(R.id.floating_action_btn2)
    public void OnFAB2Click() {
        int alarmId = AlarmDBUtils.insertLiveAlarmClock(alarmClockLab);
        AlarmModel alarmModel = AlarmDBUtils.getLiveAlarmModel(alarmId);
        if (alarmModel.enable) {
            AlarmManagerHelper.startAlarmClock(AddAlarmActivity.this, alarmModel.id);
        }
        finish();
    }

    @Override
    protected void onResume() {
        super.onResume();
        tvRingtones.setText(alarmClockLab.ring);
        tvRepeat.setText(alarmClockLab.repeat);
        tvRemind.setText(getRemindString(alarmClockLab.remind));
    }

    private String getRemindString(int remind) {
        String remindString = "";
        if (remind == 3) {
            remindString = getString(R.string.remindThreeMinutes);
        } else if (remind == 5) {
            remindString = getString(R.string.remindFiveMinutes);
        } else if (remind == 10) {
            remindString = getString(R.string.remindTenMinutes);
        } else if (remind == 20) {
            remindString = getString(R.string.remindTwentyMinutes);
        } else if (remind == 30) {
            remindString = getString(R.string.remindHalfHour);
        }
        return remindString;
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
        cursor.close();
        return ringName;
    }

    private int[] getCurrentTime() {
        Calendar time = Calendar.getInstance();
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        return new int[]{hour, minute};
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alarm_cv_repeat:
                startActivity(new Intent(RepeatActivity.newIntent(this)));
                break;
            case R.id.alarm_cv_ring:
                startActivity(RingActivity.newIntent(this));
                break;
            case R.id.alarm_cv_remind:
                startActivity(new Intent(this, RemindActivity.class));
                break;
            default:
                break;
        }
    }

    public static class TimePickerFragment extends DialogFragment implements TimePickerDialog.OnTimeSetListener {

        public TimePickerFragment() {
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));
        }

        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            String min = String.valueOf(minute);
            String hour = String.valueOf(hourOfDay);

            tvHours.setText(hour);
            tvMin.setText(min);
            alarmClockLab.setMinute(minute);
            alarmClockLab.setHour(hourOfDay);


        }
    }
}
