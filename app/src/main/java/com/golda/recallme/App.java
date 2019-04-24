package com.golda.recallme;

import android.app.Application;
import android.arch.persistence.room.Room;

import com.golda.recallme.alarm.room.AppDatabase;

/**
 * Created by Evgeniy on 18.03.2019.
 */

public class App extends Application {
    public static App instance;

    private AppDatabase database;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        database = Room.databaseBuilder(this, AppDatabase.class, "alarmbase").allowMainThreadQueries().build();
    }

    public static App getInstance() {
        return instance;
    }

    public AppDatabase getDatabase() {
        return database;
    }
}
