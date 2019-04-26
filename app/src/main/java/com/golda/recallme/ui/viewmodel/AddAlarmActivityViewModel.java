package com.golda.recallme.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.ViewModel;
import android.content.Context;
import android.database.Cursor;
import android.media.RingtoneManager;
import android.support.annotation.NonNull;

import com.golda.recallme.R;
import com.golda.recallme.alarm.AlarmClockBuilder;
import com.golda.recallme.models.alarm.AlarmModel;

import java.util.Calendar;

public class AddAlarmActivityViewModel extends AndroidViewModel {

    private AlarmModel alarmClockNew;

    public AddAlarmActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public AlarmModel getNewAlarmClock() {
             alarmClockNew = new AlarmClockBuilder().build();
            alarmClockNew.setEnable(true);
            int[] currentTime = getCurrentTime();
            alarmClockNew.setHour(currentTime[0]);
            alarmClockNew.setMinute(currentTime[1]);
            alarmClockNew.setRepeat(getApplication().getString(R.string.repeatWeekDay));
            alarmClockNew.setSunday(false);
            alarmClockNew.setMonday(true);
            alarmClockNew.setTuesday(true);
            alarmClockNew.setWednesday(true);
            alarmClockNew.setThursday(true);
            alarmClockNew.setFriday(true);
            alarmClockNew.setSaturday(false);
            alarmClockNew.setRingPosition(0);
            alarmClockNew.setRing(firstRing(getApplication()));
            alarmClockNew.setVolume(10);
            alarmClockNew.setVibrate(false);
            alarmClockNew.setRemind(3);
            alarmClockNew.setWeather(false);

        return alarmClockNew;
    }

    private int[] getCurrentTime() {
        Calendar time = Calendar.getInstance();
        int hour = time.get(Calendar.HOUR_OF_DAY);
        int minute = time.get(Calendar.MINUTE);
        return new int[]{hour, minute};
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
}
