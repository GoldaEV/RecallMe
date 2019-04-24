package com.golda.recallme.alarm.room;


import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import com.golda.recallme.models.alarm.AlarmModel;


/**
 * Created by Evgeniy on 17.03.2019.
 */


@Database(entities = {AlarmModel.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {
    public abstract AlarmModelDao alarmModelDao();
}
