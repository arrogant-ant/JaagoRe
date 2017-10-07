package com.jaagore.sabita_sant.alarm;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class AddAlarm
        extends AppCompatActivity {
    static long ALARM_TIME;
    static StringBuilder AlarmText;
    private static AddAlarm inst;
    static boolean repeat = false;
    static int snooze;
    Calendar calendar = Calendar.getInstance();
    PendingIntent pendingIntent;
    Switch repeat_sw, status_sw;
    private Spinner snooze_sp;
    private TextView title, recent_tx;
    private TimePicker timePicker;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private long recent_time;
    private boolean recent_repeat;
    private int recent_snooze;
    Button submit_bt;
    AlarmManager alarmManager;

    public static AddAlarm instance() {
        return inst;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_alarm);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        title= (TextView) findViewById(R.id.title);
        alarmTime(-330*60000);
        this.repeat_sw = ((Switch) findViewById(R.id.repeat));
        status_sw= (Switch) findViewById(R.id.status_sw);
        sharedPreferences=getSharedPreferences("time", MODE_PRIVATE);
        setAlarmManger();
        //snooze spinner
        snooze=1;
        snooze_sp= (Spinner) findViewById(R.id.snooze);
        ArrayAdapter<CharSequence> adapter=ArrayAdapter.createFromResource(AddAlarm.this,R.array.snooze_time,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        snooze_sp.setAdapter(adapter);
        snooze_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        snooze = 1;
                        break;
                    case 1:
                        snooze = 2;
                        break;
                    case 2:
                        snooze = 5;
                        break;
                    case 3:
                        snooze = 10;
                        break;
                    case 4:
                        snooze = 15;
                        break;

                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                snooze = 1;
            }

        });

        status_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    alarmManager.cancel(pendingIntent);
                    pendingIntent.cancel();
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean("status", false);
                    NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(NOTIFICATION_SERVICE);
                    notificationManager.cancelAll();
                }
            }
        });

        Typeface heading=Typeface.createFromAsset(getAssets(),"fonts/Raleway-SemiBold.ttf");
        title.setTypeface(heading);
        submit_bt= (Button) findViewById(R.id.button);
        submit_bt.setTypeface(heading);

    }

    //initialize recent alarm
    private void recentAlarm() {
        recent_time=sharedPreferences.getLong("time", 0);
        recent_repeat=sharedPreferences.getBoolean("repeat", false);
        recent_snooze=sharedPreferences.getInt("snooze", 1);

        recent_tx= (TextView) findViewById(R.id.recentAlarm);
        if(recent_time!=0) {
            alarmTime(recent_time);
            recent_tx.setText(AlarmText);
            status_sw.setChecked(sharedPreferences.getBoolean("status", false));
            repeat_sw.setChecked(recent_repeat);
        }
    }


    //converts milisecond to human readable time
    public static void alarmTime(long timeInMilis) {
        timeInMilis/=60000;
        timeInMilis+=330;
        String format;
        int min=(int)timeInMilis%60;
        int hour=(int)(timeInMilis/60)%24;
        if (hour == 0)
            format = "A.M.";
        else if (hour == 12)
            format = "P.M.";
        else if (hour > 12) {
            format = "P.M.";
            hour -= 12;
        } else
            format = "A.M.";
        if (min < 10)
            AlarmText = new StringBuilder("").append(hour).append(":0").append(min).append(" ").append(format);
        else
            AlarmText = new StringBuilder("").append(hour).append(":").append(min).append(" ").append(format);



    }


    public void onStart() {
        super.onStart();
        inst = this;
        recentAlarm();
    }

    public void setAlarm(View paramView) {

        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour().intValue());
        calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute().intValue());
        ALARM_TIME = this.calendar.getTimeInMillis();
        if(ALARM_TIME < Calendar.getInstance().getTimeInMillis())
            ALARM_TIME+=24*60*60000;
        repeat = this.repeat_sw.isChecked();
        alarmTime(calendar.getTimeInMillis());
        editor=sharedPreferences.edit();
        editor.putLong("time", ALARM_TIME);
        editor.putBoolean("repeat", repeat);
        editor.putInt("snooze", snooze);
        editor.putBoolean("status", true);
        editor.commit();
        recentAlarm();
        setAlarmManger();
        alarmManager.set(AlarmManager.RTC_WAKEUP, ALARM_TIME, pendingIntent);

        setNotification(AddAlarm.this, calendar.getTimeInMillis());
    }

    private void setAlarmManger() {
        Intent receiver_intent = new Intent(AddAlarm.this, AlarmReceiver.class);
        receiver_intent.putExtra("snooze",snooze);
        pendingIntent = PendingIntent.getBroadcast(AddAlarm.this, 0, receiver_intent, PendingIntent.FLAG_CANCEL_CURRENT);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
    }


    public void info(View view) {
        startActivity(new Intent(AddAlarm.this, Info.class));
    }


    public static void setNotification(Context ctx, long timeInMillis)
    {
        NotificationManager notificationManager = (NotificationManager) ctx.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        alarmTime(timeInMillis);
        PendingIntent alarm=PendingIntent.getActivity(ctx, 0, new Intent(ctx,AddAlarm.class), PendingIntent.FLAG_UPDATE_CURRENT);
        Notification.Builder builder =new Notification.Builder(ctx)
                .setAutoCancel(false)
                .setContentTitle("Alarm")
                .setSmallIcon(R.drawable.icon)
                .setContentText("Next Alarm Pending at "+AlarmText)
                .setPriority(Notification.PRIORITY_MAX)
                .setOngoing(true)
                .setContentIntent(alarm);

        NotificationManager manager=(NotificationManager)ctx.getSystemService(NOTIFICATION_SERVICE);
        manager.notify(2,builder.build());
    }
}

