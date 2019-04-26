package com.golda.recallme.ui.activity;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.Intent;
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
import com.golda.recallme.alarm.AlarmManagerHelper;
import com.golda.recallme.alarm.db.AlarmDBUtils;
import com.golda.recallme.models.alarm.AlarmModel;
import com.golda.recallme.ui.viewmodel.EditAlarmActivityViewModel;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Evgeniy on 22.01.2019.
 */
public class EditAlarmActivity extends AppCompatActivity implements View.OnClickListener {

    EditAlarmActivityViewModel viewModel;

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

    public static final String ALARM_CLOCK = "EXTRA_EDIT_ALARM";
    private static AlarmModel alarmModel;

    public static Intent newIntent(Context context, int id) {
        Intent intent = new Intent(context, EditAlarmActivity.class);
        intent.putExtra(ALARM_CLOCK, id);
        return intent;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);
        ButterKnife.bind(this);

        viewModel = ViewModelProviders.of(this).get(EditAlarmActivityViewModel.class);

        setSupportActionBar(toolbar);
        ActionBar bar = getSupportActionBar();
        assert bar != null;
        bar.setDisplayHomeAsUpEnabled(true);
        bar.setDisplayShowTitleEnabled(false);
        toolbarTitle.setText(R.string.editAlarmActivityTitle);

        tvHours = findViewById(R.id.alarm_time_hours);
        tvMin = findViewById(R.id.alarm_time_min);

        int id = getIntent().getIntExtra(ALARM_CLOCK, 0);
        LiveData<AlarmModel> editableModel = viewModel.getAlarmModel(id);
        editableModel.observe(this, model -> {
            alarmModel = model;
            initUI();});
    }



    private void initUI() {

        tvRingtones.setText(alarmModel.ring);
        switchVibration.setChecked(alarmModel.vibrate);
        cvRepeat.setOnClickListener(this);
        cvRing.setOnClickListener(this);
        cvRemind.setOnClickListener(this);
        switchWeather.setChecked(alarmModel.weather);

        int hour = alarmModel.hour;
        int minute = alarmModel.minute;

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

        switchVibration.setOnCheckedChangeListener((buttonView, isChecked) -> alarmModel.setVibrate(isChecked));

        switchWeather.setOnCheckedChangeListener((buttonView, isChecked) -> alarmModel.setWeather(isChecked));

        tvRingtones.setText(alarmModel.ring);
        tvRepeat.setText(alarmModel.repeat);
        tvRemind.setText(getRemindString(alarmModel.remind));
    }

    @OnClick(R.id.alarm_cv_time)
    public void OnTimeClick() {
        DialogFragment dialogFragment = new TimePickerFragment();
        dialogFragment.show(getSupportFragmentManager(), "timePicker");
    }

    @OnClick(R.id.floating_action_btn2)
    public void OnFAB2Click() {
        AlarmDBUtils.updateLiveAlarmClock(alarmModel);
        if (alarmModel.enable) {
            AlarmManagerHelper.startAlarmClock(EditAlarmActivity.this, alarmModel.id);
        }
        finish();
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

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.alarm_cv_repeat:
                Intent intent = new Intent(RepeatActivity.newIntent(this, alarmModel.id));
                startActivity(intent);
                break;
            case R.id.alarm_cv_ring:
                startActivity(RingActivity.newIntent(this, alarmModel.id));
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
            alarmModel.setMinute(minute);
            alarmModel.setHour(hourOfDay);
        }
    }
}
