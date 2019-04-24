package com.golda.recallme.alarm.room;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.golda.recallme.models.alarm.AlarmModel;

import java.util.List;

/**
 * Created by Evgeniy on 17.03.2019.
 */

@Dao
public interface AlarmModelDao {

    @Query("SELECT * FROM alarmmodel")
    LiveData<List<AlarmModel>> getAll();

    @Query("SELECT * FROM alarmmodel WHERE id = :id")
    AlarmModel getById(long id);

    @Insert
    long insert(AlarmModel alarmRoomModel);

    @Update
    void update(AlarmModel alarmRoomModel);

    @Query("DELETE FROM AlarmModel WHERE id = :id")
    void delete(int id);
}
