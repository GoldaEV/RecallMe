package com.golda.recallme.alarm;

import com.golda.recallme.models.alarm.AlarmModel;

/**
 * Created by Evgeniy on 17.03.2019.
 */
public class AlarmClockLab extends AlarmModel {

    private static AlarmClockLab clockLab = null;

    protected AlarmClockLab(AlarmClockBuilder builder) {
        super(builder);
    }

    public static AlarmClockLab getClockLab(AlarmClockBuilder builder) {
        if (clockLab == null) {
            synchronized (AlarmClockLab.class) {
                if (clockLab == null) {
                    clockLab = new AlarmClockLab(builder);
                }
            }
        }
        return clockLab;
    }

    public void setId(int id) {
        this.id = id;
    }
}
