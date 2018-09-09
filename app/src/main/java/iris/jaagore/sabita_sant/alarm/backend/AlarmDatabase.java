package iris.jaagore.sabita_sant.alarm.backend;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

/**
 * Created by Sud on 6/2/18.
 */
@Database(entities = {Alarm.class,Quote.class},version = 4)
@TypeConverters(Converter.class)
public abstract class AlarmDatabase extends RoomDatabase {
    public abstract AlarmDao alarmDao();
    public abstract  QuoteDao quoteDao();


    //for singleton
    private static AlarmDatabase databaseInstance;

    public static AlarmDatabase getInstance(Context context)
    {
        if(databaseInstance==null)
            databaseInstance= Room.databaseBuilder(context, AlarmDatabase.class, "database-JaagoRe")
                    .allowMainThreadQueries()
                    .fallbackToDestructiveMigration()
                    .build();
        return databaseInstance;
    }
}
