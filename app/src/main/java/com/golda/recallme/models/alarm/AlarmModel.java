package com.golda.recallme.models.alarm;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import com.golda.recallme.alarm.AlarmClockBuilder;

import java.io.Serializable;

/**
 * Created by Evgeniy on 17.03.2019.
 */
@Entity
public class AlarmModel implements Serializable {

    @PrimaryKey(autoGenerate = true)
    public int id;
    public boolean enable;
    public int hour, minute;
    public String repeat;
    public boolean sunday, monday, tuesday, wednesday, thursday, friday, saturday;
    public int ringPosition;
    public String ring;
    public int volume;
    public boolean vibrate;
    public int remind;
    public boolean weather;

    public AlarmModel(boolean enable, int hour, int minute, String repeat, boolean sunday, boolean monday, boolean tuesday, boolean wednesday, boolean thursday, boolean friday, boolean saturday, int ringPosition, String ring, int volume, boolean vibrate, int remind, boolean weather) {
        this.enable = enable;
        this.hour = hour;
        this.minute = minute;
        this.repeat = repeat;
        this.sunday = sunday;
        this.monday = monday;
        this.tuesday = tuesday;
        this.wednesday = wednesday;
        this.thursday = thursday;
        this.friday = friday;
        this.saturday = saturday;
        this.ringPosition = ringPosition;
        this.ring = ring;
        this.volume = volume;
        this.vibrate = vibrate;
        this.remind = remind;
        this.weather = weather;
    }

    public AlarmModel(AlarmClockBuilder builder) {
        this.enable = builder.enable;
        this.hour = builder.hour;
        this.minute = builder.minute;
        this.repeat = builder.repeat;
        this.sunday = builder.sunday;
        this.monday = builder.monday;
        this.tuesday = builder.tuesday;
        this.wednesday = builder.wednesday;
        this.thursday = builder.thursday;
        this.friday = builder.friday;
        this.saturday = builder.saturday;
        this.ringPosition = builder.ringPosition;
        this.ring = builder.ring;
        this.volume = builder.volume;
        this.vibrate = builder.vibrate;
        this.remind = builder.remind;
        this.weather = builder.weather;
    }

    public void setEnable(boolean enable) {
        this.enable = enable;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public void setRepeat(String repeat) {
        this.repeat = repeat;
    }

    public void setSunday(boolean sunday) {
        this.sunday = sunday;
    }

    public void setMonday(boolean monday) {
        this.monday = monday;
    }

    public void setTuesday(boolean tuesday) {
        this.tuesday = tuesday;
    }

    public void setWednesday(boolean wednesday) {
        this.wednesday = wednesday;
    }

    public void setThursday(boolean thursday) {
        this.thursday = thursday;
    }

    public void setFriday(boolean friday) {
        this.friday = friday;
    }

    public void setSaturday(boolean saturday) {
        this.saturday = saturday;
    }

    public void setRingPosition(int ringPosition) {
        this.ringPosition = ringPosition;
    }

    public void setRing(String ring) {
        this.ring = ring;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void setVibrate(boolean vibrate) {
        this.vibrate = vibrate;
    }

    public void setRemind(int remind) {
        this.remind = remind;
    }

    public void setWeather(boolean weather) {
        this.weather = weather;
    }
}
