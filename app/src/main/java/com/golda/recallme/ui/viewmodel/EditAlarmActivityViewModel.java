package com.golda.recallme.ui.viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

import com.golda.recallme.alarm.db.AlarmDBUtils;
import com.golda.recallme.models.alarm.AlarmModel;

public class EditAlarmActivityViewModel extends AndroidViewModel {

    private static AlarmModel alarmModel;

    public EditAlarmActivityViewModel(@NonNull Application application) {
        super(application);
    }

    public AlarmModel getAlarmModel(int id) {
        if (alarmModel == null) {
            alarmModel = AlarmDBUtils.getLiveAlarmModel(id);
        }
        return alarmModel;
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        alarmModel = null;
    }
}

