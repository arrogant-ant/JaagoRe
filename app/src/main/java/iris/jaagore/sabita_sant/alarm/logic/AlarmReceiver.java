package iris.jaagore.sabita_sant.alarm.logic;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import iris.jaagore.sabita_sant.alarm.AlarmScreen;

public class AlarmReceiver
        extends BroadcastReceiver {
    static AlarmReceiver ins;



    public AlarmReceiver() {
        Log.e("test", "constructor receiver");
        ins = this;
    }



    @Override
    public void onReceive(Context context, Intent intent) {

        Intent i = new Intent();
        i.setClass(context, AlarmScreen.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
        Log.e("start", "activity");


    }
}
