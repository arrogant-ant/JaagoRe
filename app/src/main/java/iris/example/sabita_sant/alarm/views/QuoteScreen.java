package iris.example.sabita_sant.alarm.views;


import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.InterstitialAd;
import com.facebook.ads.InterstitialAdListener;

import java.util.Calendar;

import iris.example.sabita_sant.alarm.R;
import iris.example.sabita_sant.alarm.utils.Animatation;


public class QuoteScreen extends AppCompatActivity implements InterstitialAdListener {
    private static final String TAG = "QuoteScreen";
    TextView title_tv;
    //    Timer callback;
    long delay = 10000;// 30 sec
    //    InterstitialAd ad;
    private View parentView;
    InterstitialAd ad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        loadInterstitialAd();
        title_tv = findViewById(R.id.quote_title);
        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 11) {
            title_tv.setText("Quote of the Day");
        }
        /*quote_tx = findViewById(R.id.quote);
        author_tx = findViewById(R.id.aurthor);
        setUI();*/
        //setAd();
        parentView = findViewById(R.id.parent);
//        callback = new Timer();
//        callback.schedule(callback_task, delay); removing surprise snooze


    }

    //schedulling call to parent activity
    /*TimerTask callback_task = new TimerTask() {
        @Override
        public void run() {
            Intent parent_intent = new Intent(QuoteScreen.this, Home.class);
            int parent = getIntent().getIntExtra("parent", AlarmActivity.QuoteScreen);
            switch (parent) {
                case AlarmActivity.HomeScreen:
                    parent_intent = new Intent(QuoteScreen.this, Home.class);
                    break;
                case AlarmActivity.QuoteScreen:
                    parent_intent = new Intent(QuoteScreen.this, Home.class);
                    break;
                case AlarmActivity.AlarmScreen:
                    int alarmID = getIntent().getIntExtra(Constants.ALARM_ID_KEY, 0);
                    AlarmHelper helper = new AlarmHelper(QuoteScreen.this, alarmID);
                    helper.snoozeAlarm(61000); // alarm ring in 61 secs
                    *//*parent_intent = new Intent(QuoteScreen.this, AlarmScreen.class);
                    parent_intent.putExtra(Constants.ALARM_ID_KEY, getIntent().getIntExtra(Constants.ALARM_ID_KEY, 0));*//*
                    QuoteScreen.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //Toast.makeText(QuoteScreen.this,"SURPRISE SNOOZE",Toast.LENGTH_LONG).show();
                            Snackbar.make(parentView, "SURPRISE SNOOZE", Snackbar.LENGTH_SHORT).show();
                        }
                    });
                    break;
            }
            startActivity(parent_intent);
        }
    };*/


    //onClick
    public void done(View view) {
        Animatation.spin(view)
                .addListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animator) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animator) {
                        if (ad != null && ad.isAdLoaded() && !ad.isAdInvalidated()) {
                            ad.show();
                        }
                        finish();
                    }

                    @Override
                    public void onAnimationCancel(Animator animator) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animator) {

                    }
                });
        // callback.cancel();
        Intent intent = new Intent("iris.jaagore.sabita_sant.alarm.GET_QUOTE");
        sendBroadcast(intent);
    }

    //stop callback when activity goes in background

    //load interstitial all
    private void loadInterstitialAd() {
        ad = new InterstitialAd(this, "1405894542877981_1407783742689061");
        ad.setAdListener(this);
        ad.loadAd();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // callback.cancel();
        finish();
    }

    @Override
    public void onInterstitialDisplayed(Ad ad) {

    }

    @Override
    public void onInterstitialDismissed(Ad ad) {
        finish();
    }

    @Override
    public void onError(Ad ad, AdError adError) {
        finish();
    }

    @Override
    public void onAdLoaded(Ad ad) {

    }

    @Override
    public void onAdClicked(Ad ad) {

    }

    @Override
    public void onLoggingImpression(Ad ad) {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ad.destroy();
    }
}
