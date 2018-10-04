package iris.example.sabita_sant.alarm.utils;

import iris.example.sabita_sant.alarm.R;

/**
 * Created by Sud on 7/27/18.
 */

public final class Constants {
    private Constants() {
    }
    public static final int QUOTES_CACHE_SIZE = 2;
    public static final int DAY_IN_MILIS = 24*3600*1000; // no of miliseconds

    //keys
    public static final String ALARM_ID_KEY = "id";
    public static final String QUOTES_WORKER = "quotesNotification";
    public static final String SUGGEST_ALARM_WORER = "suggestAlarm";

    public static final int[] REPEAT_CHECKBOXES = {R.id.repeat_s, R.id.repeat_m, R.id.repeat_t, R.id.repeat_w, R.id.repeat_th, R.id.repeat_f, R.id.repeat_sat};
}
