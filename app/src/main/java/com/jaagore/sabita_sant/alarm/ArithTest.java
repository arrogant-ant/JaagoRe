package com.jaagore.sabita_sant.alarm;

import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.security.Permission;
import java.util.Calendar;
import java.util.jar.Manifest;

public class ArithTest
        extends AppCompatActivity {
    TextView alert;
    Calendar cal = Calendar.getInstance();
    long dismiss;
    int i;
    TextView op1;
    TextView op2;
    TextView operand;
    EditText res;
    String result;
    int snooze_time;
    long time;
    int val1;
    int val2;
    MediaPlayer mediaPlayer;
    private long timeCountInMilliSeconds = 90 * 1000;
    private CountDownTimer countDownTimer;
    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private ProgressBar progressBarCircle;
    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.WAKE_LOCK)== PackageManager.PERMISSION_GRANTED)
            Log.e("start","permitted");
        setContentView(R.layout.activity_arith_test);
        final Window win= getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        Log.e("start", "created");
        op1 = (TextView) findViewById(R.id.operand1);
        op2 = (TextView) findViewById(R.id.operand2);
        operand = (TextView) findViewById(R.id.operator);
        res = (EditText) findViewById(R.id.res);
        alert = (TextView) findViewById(R.id.alert_text);
        progressBarCircle = (ProgressBar) findViewById(R.id.progressBarCircle);
        mediaPlayer = setPlayer(mediaPlayer);
        this.time = AddAlarm.ALARM_TIME;
        //   this.snooze_time = AddAlarm.snooze;
        snooze_time=getIntent().getExtras().getInt("snooze");
        Log.e("start", "created2");
        i = 0;
        Typeface heading=Typeface.createFromAsset(getAssets(),"fonts/Raleway-SemiBold.ttf");
        alert.setTypeface(heading);
        set();
        Log.e("start", "created end");
    }

    // setup player
    private MediaPlayer setPlayer(MediaPlayer player) {
        int m=(int)(Math.random() * 10 % 3);
        Log.e("mediaPlayer", "m= "+m);
        switch (m) {
            case 0:
                player = MediaPlayer.create(ArithTest.this, R.raw.tone1);
                break;
            case 1:
                player = MediaPlayer.create(ArithTest.this, R.raw.tone2);
                break;
            case 2:
                player = MediaPlayer.create(ArithTest.this, R.raw.tone3);
                break;
            default:
                player = MediaPlayer.create(ArithTest.this, R.raw.tone1);

        }
        player.start();
        player.setVolume(1, 1);
        player.setLooping(true);
        return player;
    }

    private void set() {

        if (timerStatus == TimerStatus.STARTED)
            stopCountDownTimer();

        setProgressBarValues();
        startCountDownTimer();
        timerStatus = TimerStatus.STARTED;
        i++;
        char op[] = {'+', '-', '*', '/'};
        int r = (int) (Math.random() * 10 % 4);
        operand.setText(String.valueOf(op[r]));
        val1 = (int) ((Math.random() * 100) % 23);
        op1.setText(String.valueOf(val1));
        val2 = (int) (((Math.random() * 100) % val1 )+ 1);
        op2.setText(String.valueOf(val2));


        switch (r) {
            case 0:
                result = String.valueOf((val1 + val2));
                break;
            case 1:
                result = String.valueOf((val1 - val2));
                break;
            case 2:
                result = String.valueOf((val1 * val2));
                break;
            case 3:
                result = String.valueOf((val1 / val2));
                break;

        }
        Log.e("start", "set end");


    }

    //checks the entered result
    public void check(View paramView) {
        Log.e("start", "check created");
        if (this.res.getText().toString().equals(this.result)) {
            mediaPlayer.stop();
            stopCountDownTimer();
            timerStatus = TimerStatus.STOPPED;
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(NOTIFICATION_SERVICE);
            notificationManager.cancelAll();

            this.time = (this.time % 86400000L / 60000L);
            Log.e("start", "check mid");
            if (AddAlarm.repeat)
                repeatAlarm();
            else
            {
                SharedPreferences preferences=getSharedPreferences("time",MODE_PRIVATE);
                SharedPreferences.Editor editor=preferences.edit();
                editor.putBoolean("status",false);
                editor.commit();
            }
            finish();

        } else {
            if (this.i < 4) {
                set();
                this.alert.setText("Sorry!! " + (4 - this.i) + " more try left");
                return;
            } else {
                startCountDownTimer();
                snooze();

            }
        }
    }

    private void repeatAlarm() {
        Toast.makeText(ArithTest.this, "alarm will  be repeated", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ArithTest.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ArithTest.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        int alarm_time = (int) time % 8640000;
        int present_time = (int) Calendar.getInstance().getTimeInMillis() % 8640000;
        if (alarm_time >= present_time) {
            alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() + alarm_time - present_time, pendingIntent);

        } else {
            alarmManager.set(AlarmManager.RTC_WAKEUP, Calendar.getInstance().getTimeInMillis() - (present_time - alarm_time) + 8640000, pendingIntent);
        }
        finish();
    }

    private void snooze() {
        mediaPlayer.stop();
        long time=Calendar.getInstance().getTimeInMillis() + 60000 * snooze_time;
        AddAlarm.setNotification(ArithTest.this, time);
        Toast.makeText(this, "ALARM SNOOZED FOR " + this.snooze_time + " MINS", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(ArithTest.this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(ArithTest.this, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP,time, pendingIntent);

        finish();
    }

    /**
     * method to start count down timer
     */
    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 50) {
            @Override
            public void onTick(long millisUntilFinished) {

                progressBarCircle.setProgress((int) (millisUntilFinished/1000));

            }

            @Override
            public void onFinish() {

                Toast.makeText(ArithTest.this,"Time up",Toast.LENGTH_SHORT).show();
                if (timerStatus == TimerStatus.STARTED) {
                    snooze();
                    timerStatus = TimerStatus.STOPPED;
                }
            }

        }.start();
        countDownTimer.start();
    }


    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        timerStatus = TimerStatus.STOPPED;
        countDownTimer.cancel();
        countDownTimer = null;

    }

    /**
     * method to set circular progress bar values
     */
    private void setProgressBarValues() {

        progressBarCircle.setMax((int) timeCountInMilliSeconds / 1000);
        progressBarCircle.setProgress((int) timeCountInMilliSeconds / 1000);

    }


    @Override
    protected void onStop() {
        super.onStop();
        if (timerStatus == TimerStatus.STARTED)
            snooze();
    }
}



/* Location:           C:\Users\Sabita_Sant\Desktop\Alarm\dex2jar-0.0.9.15\classes_dex2jar.jar

 * Qualified Name:     com.jaagore.sabita_sant.alarm.ArithTest

 * JD-Core Version:    0.7.0.1

 */