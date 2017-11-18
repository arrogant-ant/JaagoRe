package iris.jaagore.sabita_sant.alarm;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;


import java.io.IOException;


public class AlarmScreen extends AppCompatActivity {

    AlarmNotification alarmNotification;
    Arithmetic arithmetic;
    MediaPlayer mediaPlayer;
    Vibrator vibrator;
    TextView op1, op2, operator, alert;
    EditText res;
    ProgressBar progressBarCircle;
    Typeface heading;
    private CountDownTimer countDownTimer;
    private TimerStatus timerStatus = TimerStatus.STOPPED;
    private AlarmHelper alarmHelper;
    private long timeCountInMilliSeconds;
    int i;
    long pattern[]={0,200,1000,200};


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
        alarmNotification = new AlarmNotification(context);
        alarmHelper = new AlarmHelper(context);
        arithmetic = new Arithmetic();
        heading = Typeface.createFromAsset(getAssets(), "fonts/Raleway-SemiBold.ttf");
        timeCountInMilliSeconds = 60000;
        vibrator= (Vibrator) getSystemService(context.VIBRATOR_SERVICE);
        i = 1;

    }



    private void initializeUI() {
        op1 = (TextView) findViewById(R.id.operand1);
        op2 = (TextView) findViewById(R.id.operand2);
        operator = (TextView) findViewById(R.id.operator);
        res = (EditText) findViewById(R.id.res);
        alert = (TextView) findViewById(R.id.alert_text);
        progressBarCircle = (ProgressBar) findViewById(R.id.progressBarCircle);
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
        alarmNotification.setActive();

        //UI setup
        op1.setText(String.valueOf(arithmetic.getNum1()));
        op2.setText(String.valueOf(arithmetic.getNum2()));
        operator.setText(String.valueOf(arithmetic.getOperator()));
        res.setText("");
        mediaPlayer.start();
        mediaPlayer.setLooping(true);
        vibrator.vibrate(pattern,0);


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
            if (alarmHelper.isRepeat())
                alarmHelper.setRepeatAlarm();
            else
                alarmHelper.stopAlarm();
            //calling quotes activity
            startActivity(new Intent(AlarmScreen.this,QuoteActivity.class));
            finish();
        } else {
            if (this.i < 4) {
                setScreen();
                this.alert.setText("Sorry!! " + (4 - this.i) + " more try left");
            } else {
                alarmHelper.snoozeAlarm();
                finish();
            }

        }




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
                alarmHelper.snoozeAlarm();
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
                        Toast.makeText(AlarmScreen.this, "Time up", Toast.LENGTH_SHORT).show();
                        mediaPlayer.stop();
                        vibrator.cancel();
                        alarmHelper.snoozeAlarm();
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
