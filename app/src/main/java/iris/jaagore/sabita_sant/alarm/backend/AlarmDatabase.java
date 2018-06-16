package iris.jaagore.sabita_sant.alarm.backend;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

/**
 * Created by Sud on 6/2/18.
 */
@Database(entities = {Alarm.class},version = 1)
public abstract class AlarmDatabase extends RoomDatabase {
    public abstract AlarmDao alarmDao();


    //for singleton
    private static AlarmDatabase databaseInstance;
    private AlarmDatabase() {

    }
    public static AlarmDatabase getInstance(Context context)
    {
        if(databaseInstance==null)
            databaseInstance= Room.databaseBuilder(context,
                    AlarmDatabase.class, "database-JaagoRe").build();
        return databaseInstance;
    }
}
