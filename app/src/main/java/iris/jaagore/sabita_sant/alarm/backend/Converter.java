package iris.jaagore.sabita_sant.alarm.backend;

import android.arch.persistence.room.TypeConverter;

import iris.jaagore.sabita_sant.alarm.utils.AlarmType;

/**
 * Created by Sud on 8/17/18.
 */

public class Converter {
    @TypeConverter
    public static String fromBool(boolean[] value){
        StringBuilder resp = new StringBuilder(7);
        for (boolean val:value) {
            if(val)
            {
                resp.append("T");
                continue;
            }
            resp.append("F");


        }
        return String.valueOf(resp);
    }

    @TypeConverter
    public static boolean[] fromString(String value){
        char val;
        int len = value.length();
        boolean[] resp = new boolean[len];
        for(int i=0;i<len;i++){
            val = value.charAt(i);
            if(val == 'T')
                resp[i] = true;
            else
                resp[i] = false;

        }
        return resp;
    }

    @TypeConverter
    public static AlarmType fromInt(int value){
        switch (value){
            case 0:
                return AlarmType.SIMPLE;
            case 1:
                return AlarmType.ARIHEMATIC;
            case 2:
                return AlarmType.PHASE;
            default:
                return AlarmType.SIMPLE;
        }
    }

    @TypeConverter
    public static int fromAlarmType(AlarmType value){
        switch (value){
            case SIMPLE:
                return 0;
            case ARIHEMATIC:
                return 0;
            case PHASE:
                return 0;
            default:
                return 0;
        }
    }
}
