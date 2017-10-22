package iris.jaagore.sabita_sant.alarm;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
    long ALARM_TIME;
    static String alarmText;
    static boolean repeat = false;
    static int snooze;
    Calendar calendar = Calendar.getInstance();
    Switch repeat_sw, status_sw;
    private Spinner snooze_sp;
    private TextView title;
    private TimePicker timePicker;

    Button submit_bt;
    Alarm alarm;
    AlarmHelper alarmHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_add_alarm);
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(new ConnectivityReceiver(),intentFilter);
        timePicker = (TimePicker) findViewById(R.id.timePicker);
        title = (TextView) findViewById(R.id.quote_title);
        alarm = new Alarm(AddAlarm.this);
        alarmHelper = new AlarmHelper(AddAlarm.this);
        alarmText = alarm.getAlarmTime();
        this.repeat_sw = ((Switch) findViewById(R.id.repeat));
        status_sw = (Switch) findViewById(R.id.status_sw);
        setupSnoozeSpinner();

        status_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (!isChecked) {
                    alarmHelper.stopAlarm();
                } else
                    alarmHelper.setPrevious();
            }
        });
        repeat_sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean isChecked) {
                if (isChecked) {
                    alarmHelper.setRepeat(true);
                } else
                    alarmHelper.setRepeat(false);

            }
        });

        Typeface heading = Typeface.createFromAsset(getAssets(), "fonts/Raleway-SemiBold.ttf");
        title.setTypeface(heading);
        submit_bt = (Button) findViewById(R.id.button);
        submit_bt.setTypeface(heading);

    }

    private void setupSnoozeSpinner() {
        snooze = 1;
        snooze_sp = (Spinner) findViewById(R.id.snooze);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(AddAlarm.this, R.array.snooze_time, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        snooze_sp.setAdapter(adapter);
        snooze_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case 0:
                        snooze = 2;
                        break;
                    case 1:
                        snooze = 5;
                        break;
                    case 2:
                        snooze = 10;
                        break;
                    case 3:
                        snooze = 15;
                        break;


                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                snooze = 1;
            }

        });
    }

    //initialize recent alarm
    private void recentAlarm() {
        int pos = 0;

        long recent_time = alarm.getNextAlarm();
        boolean recent_repeat = alarm.isRepeat();
        int recent_snooze = alarm.getSnoozeTime();

        TextView recent_tx = (TextView) findViewById(R.id.recentAlarm);

        switch (recent_snooze) {
            case 2:
                pos = 0;
                break;
            case 5:
                pos = 1;
                break;
            case 10:
                pos = 2;
                break;
            case 15:
                pos = 3;
                break;


        }
        if (recent_time != 0) {
            alarmText = alarm.getAlarmTime();
            recent_tx.setText(alarmText);
            status_sw.setChecked(alarm.isActive());
            repeat_sw.setChecked(recent_repeat);
            snooze_sp.setSelection(pos);
        }
    }


    public void onResume() {
        super.onResume();
        recentAlarm();
        Log.d("AddAlarm", "resumed");
    }

    public void setAlarm(View paramView) {

        calendar.set(Calendar.HOUR_OF_DAY, timePicker.getCurrentHour().intValue());
        calendar.set(Calendar.MINUTE, timePicker.getCurrentMinute().intValue());
        ALARM_TIME = this.calendar.getTimeInMillis();
        if (ALARM_TIME < Calendar.getInstance().getTimeInMillis())
            ALARM_TIME += 24 * 60 * 60000;
        repeat = this.repeat_sw.isChecked();
        alarmText = alarm.getAlarmTime();
        alarmHelper.setAlarm(AddAlarm.this, ALARM_TIME, repeat, snooze, true);
        recentAlarm();
    }

    public void info(View view) {
        startActivity(new Intent(AddAlarm.this, Info.class));
    }



}

