package com.golda.recallme.ui.activity;

import android.app.AlarmManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.golda.recallme.R;
import com.golda.recallme.alarm.AlarmClockBuilder;
import com.golda.recallme.alarm.AlarmClockLab;
import com.golda.recallme.alarm.db.AlarmDBUtils;
import com.golda.recallme.models.alarm.AlarmModel;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Evgeniy on 27.03.2019.
 */
public class RepeatActivity extends AppCompatActivity implements View.OnClickListener {

    @BindView(R.id.repeat_once)
    TextView tvOnce;
    @BindView(R.id.repeat_weekday)
    TextView tvWeekDay;
    @BindView(R.id.repeat_everyday)
    TextView tvEveryDay;
    @BindView(R.id.repeat_weekend)
    TextView tvWeekend;
    @BindView(R.id.repeat_choice)
    TextView tvChoice;

    private AlarmModel alarmClockLab;
    public static final String ALARM_CLOCK = "EXTRA_REPEAT";

    public static Intent newIntent(Context context, int id) {
        Intent intent = new Intent(context, RepeatActivity.class);
        intent.putExtra(ALARM_CLOCK, id);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_repeat);
        ButterKnife.bind(this);

        int id = getIntent().getIntExtra(ALARM_CLOCK, 0);
        alarmClockLab = AlarmDBUtils.getAlarmModel(id);

        tvOnce.setOnClickListener(this);
        tvWeekDay.setOnClickListener(this);
        tvEveryDay.setOnClickListener(this);
        tvWeekend.setOnClickListener(this);
        tvChoice.setOnClickListener(this);

        String repeat = alarmClockLab.repeat;
        if (repeat.equals(tvOnce.getText().toString())) {
            tvOnce.setTextColor(ContextCompat.getColor(this, R.color.colorRed_500));
        } else if (repeat.equals(tvWeekDay.getText().toString())) {
            tvWeekDay.setTextColor(ContextCompat.getColor(this, R.color.colorRed_500));
        } else if (repeat.equals(tvEveryDay.getText().toString())) {
            tvEveryDay.setTextColor(ContextCompat.getColor(this, R.color.colorRed_500));
        } else if (repeat.equals(tvWeekend.getText().toString())) {
            tvWeekend.setTextColor(ContextCompat.getColor(this, R.color.colorRed_500));
        } else if (repeat.equals(tvChoice.getText().toString())) {
            tvChoice.setTextColor(ContextCompat.getColor(this, R.color.colorRed_500));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.repeat_once:
                alarmClockLab.setRepeat(tvOnce.getText().toString());
                alarmClockLab.setSunday(false);
                alarmClockLab.setMonday(false);
                alarmClockLab.setTuesday(false);
                alarmClockLab.setWednesday(false);
                alarmClockLab.setThursday(false);
                alarmClockLab.setFriday(false);
                alarmClockLab.setSaturday(false);
                break;

            case R.id.repeat_weekday:
                alarmClockLab.setRepeat(tvWeekDay.getText().toString());
                alarmClockLab.setSunday(false);
                alarmClockLab.setMonday(true);
                alarmClockLab.setTuesday(true);
                alarmClockLab.setWednesday(true);
                alarmClockLab.setThursday(true);
                alarmClockLab.setFriday(true);
                alarmClockLab.setSaturday(false);
                break;

            case R.id.repeat_everyday:
                alarmClockLab.setRepeat(tvEveryDay.getText().toString());
                alarmClockLab.setSunday(true);
                alarmClockLab.setMonday(true);
                alarmClockLab.setTuesday(true);
                alarmClockLab.setWednesday(true);
                alarmClockLab.setThursday(true);
                alarmClockLab.setFriday(true);
                alarmClockLab.setSaturday(true);
                break;

            case R.id.repeat_weekend:
                alarmClockLab.setRepeat(tvWeekend.getText().toString());
                alarmClockLab.setSunday(true);
                alarmClockLab.setMonday(false);
                alarmClockLab.setTuesday(false);
                alarmClockLab.setWednesday(false);
                alarmClockLab.setThursday(false);
                alarmClockLab.setFriday(false);
                alarmClockLab.setSaturday(true);
                break;

            case R.id.repeat_choice:
                alarmClockLab.setRepeat(tvChoice.getText().toString());
                startActivity(RepeatChoiceActivity.newIntent(this));
                break;

            default:
                break;
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AlarmDBUtils.updateLiveAlarmClock(alarmClockLab);
    }
}
