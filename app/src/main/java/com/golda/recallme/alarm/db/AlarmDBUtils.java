package com.golda.recallme.alarm.db;

import android.arch.lifecycle.LiveData;

import com.golda.recallme.App;
import com.golda.recallme.models.alarm.AlarmModel;

import java.util.List;

/**
 * Created by Evgeniy on 17.03.2019.
 */

public class AlarmDBUtils {

    public static LiveData<AlarmModel> getLiveAlarmModel(int id) {
        return App.getInstance().getDatabase().alarmModelDao().getByLiveId(id);
    }

    public static AlarmModel getAlarmModel(int id) {
        return App.getInstance().getDatabase().alarmModelDao().getById(id);
    }

    public static int insertLiveAlarmClock(AlarmModel alarm) {
        return (int) App.getInstance().getDatabase().alarmModelDao().insert(alarm);
    }


    public static void updateLiveAlarmClock(AlarmModel alarm) {
        new Thread(() -> App.getInstance().getDatabase().alarmModelDao().update(alarm)).start();
    }

    public static void deleteLiveAlarmClock(int id) {
        new Thread(() -> App.getInstance().getDatabase().alarmModelDao().delete(id)).start();
    }

    public static LiveData<List<AlarmModel>> queryLiveAlarmClock() {

        return App.getInstance().getDatabase().alarmModelDao().getAll();
    }

}
