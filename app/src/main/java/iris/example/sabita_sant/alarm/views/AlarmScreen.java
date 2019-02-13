package iris.example.sabita_sant.alarm.views;

import android.Manifest;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
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

import com.google.firebase.perf.FirebasePerformance;
import com.google.firebase.perf.metrics.Trace;

import java.util.Calendar;

import iris.example.sabita_sant.alarm.R;
import iris.example.sabita_sant.alarm.controller.AlarmHelper;
import iris.example.sabita_sant.alarm.controller.AlarmMethod;
import iris.example.sabita_sant.alarm.controller.ArithmeticHelper;
import iris.example.sabita_sant.alarm.models.Alarm;
import iris.example.sabita_sant.alarm.models.AlarmDatabase;
import iris.example.sabita_sant.alarm.utils.AlarmAlertWakeLock;
import iris.example.sabita_sant.alarm.utils.AlarmNotification;
import iris.example.sabita_sant.alarm.utils.AlarmType;
import iris.example.sabita_sant.alarm.utils.Constants;


public class AlarmScreen extends AppCompatActivity {

    private static final String TAG = "AlarmScreen";
    private final long timeCountInMilliSeconds = 60000;
    AlarmNotification alarmNotification;
    ArithmeticHelper arithmetic;
    MediaPlayer mediaPlayer;
    Vibrator vibrator;
    TextView alert;
    EditText res;
    ProgressBar progressBarCircle;
    Typeface heading;
    Button submit_bt;
    int i;
    int bt_width;
    long pattern[] = {0, 200, 1000, 200};
    private CountDownTimer countDownTimer;
    private TimerStatus timerStatus = TimerStatus.STOPPED;
    //private AlarmHelper alarmHelper;
    private int alarmID;
    private Alarm alarm;
    private AlarmHelper helper;
    private View parent;
    private AlarmMethod methodFragment;
    private Trace suddenStopTrace;
    private Uri toneUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm_screen);
        suddenStopTrace = FirebasePerformance.getInstance().newTrace("sudden_stop");
        suddenStopTrace.start();
        AlarmAlertWakeLock.acquireScreenCpuWakeLock(this);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        initializeObjects(AlarmScreen.this);
        if (alarm == null || !alarm.isActive() || Math.abs(Calendar.getInstance().getTimeInMillis() - alarm.getAlarmTime()) > 60000) {
            killActivity();
            return;
        }
        initializeUI();
        setScreen();


    }

    private void initializeObjects(Context context) {

        alarmID = getIntent().getIntExtra(Constants.ALARM_ID_KEY, 0);
        if (alarmID == 0) {
            killActivity();
            return;
        }
        Log.i(TAG, "initializeObjects: alarm id" + alarmID);
        AlarmDatabase db = AlarmDatabase.getInstance(context);
        alarm = db.alarmDao().getAlarm(alarmID);
        // no alarm found OR alarm is not active OR not Proper time
        if (alarm == null || !alarm.isActive() || Math.abs(Calendar.getInstance().getTimeInMillis() - alarm.getAlarmTime()) > 60000) {
            Log.e(TAG, "initializeObjects: " + "improper alarm");
            killActivity();
            return;
        }
        AlarmType type = alarm.getType();
        // if no tone is selected
        try {
            toneUri = Uri.parse(alarm.getToneURI());
        } catch (NullPointerException e) {
            toneUri = null;
        }
        Log.i(TAG, "initializeObjects: alarm type " + type);
        switch (type) {
            case ARIHEMATIC:
                methodFragment = new ArithematicFragment();
                break;
            case PHRASE:
                methodFragment = new PhraseFragment();
                break;
            case SIMPLE:
                methodFragment = new SimpleFragment();
                break;
            default:
                methodFragment = new ArithematicFragment();
        }
        alarmNotification = new AlarmNotification(context);
        //alarmHelper = new AlarmHelper(context);
        helper = new AlarmHelper(context, alarmID);
        arithmetic = new ArithmeticHelper();
        heading = Typeface.createFromAsset(getAssets(), "fonts/Raleway-SemiBold.ttf");
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        i = 1;

    }

    private void initializeUI() {
        parent = findViewById(R.id.parent);
        alert = findViewById(R.id.alert_text);
        progressBarCircle = findViewById(R.id.progressBarCircle);
        submit_bt = findViewById(R.id.submit_bt);
        submit_bt.setElevation(25);
        submit_bt.setTranslationZ(25);
        alert.setTypeface(heading);
        FragmentManager manager = getSupportFragmentManager();
        manager.beginTransaction().add(R.id.method_fragment, (Fragment) methodFragment).commit();

    }

    // setup player
    private MediaPlayer setPlayer(Context context) {
        MediaPlayer player;
        if (toneUri == null ||
                (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                        && context.checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            int m = (int) (Math.random() * 10 % 3);
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

            }
        } else {
            player = MediaPlayer.create(context, toneUri);
        }

        final AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        audioManager.setMode(AudioManager.MODE_NORMAL);
        int alarmVolume = audioManager.getStreamVolume(AudioManager.STREAM_ALARM);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, alarmVolume, 0);
        if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) != 0) {
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
        }
//        player.setVolume(1, 1);
        player.setLooping(true);
        return player;
    }

    // ringing state
    private void setScreen() {
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
        mediaPlayer.start();
        vibrator.vibrate(pattern, 0);
        animateSubmitButton();


    }

    private void animateSubmitButton() {
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        int screen_width = display.widthPixels;
        bt_width = submit_bt.getMeasuredWidth();
        int end = screen_width - bt_width;

        ObjectAnimator animator = ObjectAnimator.ofFloat(submit_bt, View.TRANSLATION_X, 0, end);
        animator.setRepeatCount(ObjectAnimator.INFINITE);
        animator.setRepeatMode(ObjectAnimator.REVERSE);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.setDuration(6000);
        animator.start();
    }

    public void check(View view) {
        mediaPlayer.stop();
        vibrator.cancel();
        stopCountDownTimer();
        timerStatus = TimerStatus.STOPPED;
        alarmNotification.cancel();

        //if correct answer
        // call validate
        if (methodFragment.isValidResponse()) {
            helper.stopAlarm();
            //calling quotes activity
            Intent quote = new Intent(AlarmScreen.this, QuoteScreen.class);
            quote.putExtra("parent", AlarmActivity.AlarmScreen);
            quote.putExtra(Constants.ALARM_ID_KEY, alarmID);
            startActivity(quote);
            killActivity();
        } else {
            if (i < 4) {
                setScreen();
                methodFragment.populate();
                this.alert.setText("Sorry!! " + (4 - this.i) + " more try left");
            } else {
                //alarmHelper.snoozeAlarm();
                snoozeAlarm(parent);
//                Intent quote = new Intent(AlarmScreen.this, QuoteScreen.class);
//                startActivity(quote);
//                killActivity();
            }

        }


    }

    @Override
    public void onBackPressed() {
        // back button wont work
        //super.onBackPressed();
        //snoozeAlarm(parent);
    }
    //on removing alarm screen

    //onClick snooze button
    public void snoozeAlarm(View view) {
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.stop();
            vibrator.cancel();
        }
        stopCountDownTimer();
        timerStatus = TimerStatus.STOPPED;
        alarmNotification.cancel();
        helper.snoozeAlarm();
        Intent quote = new Intent(AlarmScreen.this, QuoteScreen.class);
        startActivity(quote);
        killActivity();
    }

    @Override
    protected void onUserLeaveHint() {
        super.onUserLeaveHint();
        if (timerStatus == TimerStatus.STARTED) {
            finish();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (this.isFinishing()) {
            suddenStopTrace.stop();
            if (timerStatus == TimerStatus.STARTED) {
                mediaPlayer.stop();
                mediaPlayer.release();
                AlarmAlertWakeLock.releaseCpuLock();
                vibrator.cancel();
                //alarmHelper.snoozeAlarm();
                helper.snoozeAlarm();
            }
        }

    }
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
                        Snackbar.make(parent, "Time Up!!!", Snackbar.LENGTH_SHORT).show();
                        try {
                            if (mediaPlayer != null && mediaPlayer.isPlaying())
                                mediaPlayer.stop();
                            vibrator.cancel();
                            //alarmHelper.snoozeAlarm();
                            helper.snoozeAlarm();
                        } catch (IllegalStateException e) {
                            //alarmHelper.snoozeAlarm();
                            helper.snoozeAlarm();
                        }
                    }
                    timerStatus = TimerStatus.STOPPED;
                    AlarmAlertWakeLock.releaseCpuLock();
                }
                finish();
            }

        }.start();
        countDownTimer.start();
    }

    //progress related stuff

    /**
     * method to stop count down timer
     */
    private void stopCountDownTimer() {
        timerStatus = TimerStatus.STOPPED;
        // coming from snooze
        if (countDownTimer == null)
            return;
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

    private void killActivity() {
        this.finish();
    }

    private enum TimerStatus {
        STARTED,
        STOPPED
    }
}
