package com.golda.recallme.ui.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.golda.recallme.ui.activity.BootAlarmActivity;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

/**
 * Created by Evgeniy on 24.03.2019.
 */

public class AlarmClockReceiver extends BroadcastReceiver {

    public static final String ALARM_CLOCK = "alarm_clock";

    public AlarmClockReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent bootIntent = new Intent(context, BootAlarmActivity.class);
        bootIntent.addFlags(FLAG_ACTIVITY_NEW_TASK);
        bootIntent.putExtra(ALARM_CLOCK, intent.getIntExtra(ALARM_CLOCK,0));
        Log.d("TAG", "receiver");
        context .startActivity(bootIntent);
    }
}
