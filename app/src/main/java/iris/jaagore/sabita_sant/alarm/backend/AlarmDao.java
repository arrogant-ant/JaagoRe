package iris.jaagore.sabita_sant.alarm.backend;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

/**
 * Created by Sud on 6/2/18.
 */
@Dao
public interface AlarmDao {
    @Insert
    void addAlarm(Alarm alarm);

    @Delete
    void deleteAlarm(Alarm alarm);

    @Update
    void updateAlarm(Alarm alarm);

    @Query("SELECT * FROM  Alarm WHERE id=:id LIMIT 1")
    Alarm getAlarm(int id);
}
