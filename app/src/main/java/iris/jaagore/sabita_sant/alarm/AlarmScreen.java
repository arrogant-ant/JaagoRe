package iris.jaagore.sabita_sant.alarm;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;


import java.io.IOException;

import iris.jaagore.sabita_sant.alarm.backend.Alarm;
import iris.jaagore.sabita_sant.alarm.backend.AlarmDatabase;
import iris.jaagore.sabita_sant.alarm.logic.AlarmHelper;
import iris.jaagore.sabita_sant.alarm.utils.Constants;


public class AlarmScreen extends AppCompatActivity {

    AlarmNotification alarmNotification;
    Arithmetic arithmetic;
    MediaPlayer mediaPlayer;
    Vibrator vibrator;
    TextView op1, op2, operator, alert;
    EditText res;
    ProgressBar progressBarCircle;
    Typeface heading;
    Button submit_bt;
    private CountDownTimer countDownTimer;
    private TimerStatus timerStatus = TimerStatus.STOPPED;
    //private AlarmHelper alarmHelper;
    private int alarmID;
    private Alarm alarm;
    private long timeCountInMilliSeconds;
    int i;
    int bt_width;
    long pattern[]={0,200,1000,200};
    private AlarmHelper helper;
    private View parent;

    private static final String TAG = "AlarmScreen";
    private enum TimerStatus {
        STARTED,
        STOPPED
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_screen);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        initializeObjects(AlarmScreen.this);
        initializeUI();
        setScreen();


    }


    private void initializeObjects(Context context) {

        alarmID = getIntent().getIntExtra(Constants.ALARM_ID_KEY,0);
        Log.i(TAG, "initializeObjects: alam id"+alarm);
        AlarmDatabase db = AlarmDatabase.getInstance(context);
        alarm = db.alarmDao().getAlarm(alarmID);
        alarmNotification = new AlarmNotification(context);
        //alarmHelper = new AlarmHelper(context);
        helper = new AlarmHelper(context, alarmID);
        arithmetic = new Arithmetic();
        heading = Typeface.createFromAsset(getAssets(), "fonts/Raleway-SemiBold.ttf");
        timeCountInMilliSeconds = 60000;
        vibrator= (Vibrator) getSystemService(VIBRATOR_SERVICE);
        i = 1;

    }



    private void initializeUI() {
        parent = findViewById(R.id.parent);
        op1 = findViewById(R.id.operand1);
        op2 = findViewById(R.id.operand2);
        operator = findViewById(R.id.operator);
        res = findViewById(R.id.res);
        alert = findViewById(R.id.alert_text);
        progressBarCircle = findViewById(R.id.progressBarCircle);
        submit_bt= findViewById(R.id.submit_bt);
        submit_bt.setElevation(25);
        submit_bt.setTranslationZ(25);
        alert.setTypeface(heading);

    }

    // setup player
    private MediaPlayer setPlayer(Context context) {
        Log.e("AlarmScreen","setPlayer");
        MediaPlayer player;
        int m = (int) (Math.random() * 10 % 3);
        Log.e("mediaPlayer", "m= " + m);
        switch (m) {
            case 0:
                player = MediaPlayer.create(context, R.raw.tone1);
                break;
            case 1:
                player = MediaPlayer.create(context, R.raw.tone2);
                break;
            case 2:
                player = MediaPlayer.create(context, R.raw.tone3);
                break;
            default:
                player = MediaPlayer.create(context, R.raw.tone1);


                player.setVolume(1, 1);
                player.setLooping(true);
                try {
                    player.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
        Log.e("start", "mediaplayer created");

        return player;
    }

    private void setScreen() {
        Log.e("AlarmScreen","setScreen");
        mediaPlayer = setPlayer(AlarmScreen.this);
        i++;
        //progress bar setup
        if (timerStatus == TimerStatus.STARTED)
            stopCountDownTimer();
        setProgressBarValues();
        startCountDownTimer();
        timerStatus = TimerStatus.STARTED;
        //notification setup
        alarmNotification.setActive(alarm);

        //UI setup
        op1.setText(String.valueOf(arithmetic.getNum1()));
        op2.setText(String.valueOf(arithmetic.getNum2()));
        operator.setText(String.valueOf(arithmetic.getOperator()));
        res.setText("");
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
        vibrator.vibrate(pattern,0);
        animateSubmitButton();


    }


    private void animateSubmitButton() {
        DisplayMetrics display=new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        int screen_width=display.widthPixels;
        bt_width=submit_bt.getMeasuredWidth();
        int end=screen_width-bt_width;

        ObjectAnimator animator=ObjectAnimator.ofFloat(submit_bt,View.TRANSLATION_X,0,end);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(6000);
        animator.start();
    }

    public void check(View view) {
        Log.e("AlarmScreen","check");
        mediaPlayer.stop();
        vibrator.cancel();
        stopCountDownTimer();
        timerStatus = TimerStatus.STOPPED;
        alarmNotification.cancel();
        String result[]=res.getText().toString().split("\\.");

        //if correct answer
        if (String.valueOf(arithmetic.getResult()).equals(result[0]) ){
            vibrator.cancel();
           /* if (alarmHelper.getRepeatCount())
                alarmHelper.setRepeatAlarm();
            else
                alarmHelper.stopAlarm();*/
           helper.stopAlarm();
            //calling quotes activity
            Intent quote=new Intent(AlarmScreen.this,QuoteScreen.class);
            quote.putExtra("parent",AlarmActivity.AlarmScreen);
            quote.putExtra(Constants.ALARM_ID_KEY,alarmID);
            startActivity(quote);
            finish();
        } else {
            if (this.i < 4) {
                setScreen();
                this.alert.setText("Sorry!! " + (4 - this.i) + " more try left");
            } else {
                //alarmHelper.snoozeAlarm();
                helper.snoozeAlarm();
                finish();
            }

        }




    }
    //on removing alarm screen


    @Override
    public void onBackPressed() {

    }

    //onClick snooze button
    public void snoozeAlarm(View view) {
        mediaPlayer.stop();
        vibrator.cancel();
        stopCountDownTimer();
        timerStatus = TimerStatus.STOPPED;
        alarmNotification.cancel();
        //alarmHelper.snoozeAlarm();
        helper.snoozeAlarm();
        finish();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        finish();
    }

    @Override
    protected void onStop(){
        super.onStop();
        Log.e("AlarmScreen","onStop");
       if (this.isFinishing()) {
           Log.e("AlarmScreen","finishing");
            if (timerStatus == TimerStatus.STARTED) {
                Log.e("AlarmScreen","Timer "+timerStatus);
                mediaPlayer.stop();
                mediaPlayer.release();
                vibrator.cancel();
                //alarmHelper.snoozeAlarm();
                helper.snoozeAlarm();
            }
        }

    }

        //progress related stuff

        /**
         * method to start count down timer
         */

    private void startCountDownTimer() {

        countDownTimer = new CountDownTimer(timeCountInMilliSeconds, 50) {
            @Override
            public void onTick(long millisUntilFinished) {

                progressBarCircle.setProgress((int) (millisUntilFinished / 1000));

            }

            @Override
            public void onFinish() {


                if (timerStatus == TimerStatus.STARTED) {
                    {
                        //Toast.makeText(AlarmScreen.this, "Time up", Toast.LENGTH_SHORT).show();
                        Snackbar.make(parent,"Time Up!!!",Snackbar.LENGTH_SHORT).show();
                        try {
                            if (mediaPlayer != null && mediaPlayer.isPlaying())
                                mediaPlayer.stop();
                            vibrator.cancel();
                            //alarmHelper.snoozeAlarm();
                            helper.snoozeAlarm();
                        }
                        catch (IllegalStateException e)
                        {
                            //alarmHelper.snoozeAlarm();
                            helper.snoozeAlarm();
                        }
                    }
                    timerStatus = TimerStatus.STOPPED;
                }
                finish();
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
}
