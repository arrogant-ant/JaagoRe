package iris.example.sabita_sant.alarm;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import iris.example.sabita_sant.alarm.utils.Constants;


public class QuoteScreen extends AppCompatActivity {
    private static final String TAG = "QuoteScreen";
    TextView title_tv;
    Timer callback;
    long delay = 30000;// 30 sec
    //    InterstitialAd ad;
    private View parentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        title_tv = findViewById(R.id.quote_title);
        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 11) {
            title_tv.setText("Quote of the Day");
        }
        /*quote_tx = findViewById(R.id.quote);
        author_tx = findViewById(R.id.aurthor);
        setUI();*/
        //setAd();
        parentView = findViewById(R.id.parent);
        callback = new Timer();
        callback.schedule(callback_task, delay);


    }
//interstitial ad setup
  /*  private void setAd() {
        ad=new InterstitialAd(this);
        ad.setAdUnitId(getString(R.string.interstitial_ad));
        ad.loadAd(new AdRequest.Builder().build());
        ad.setAdListener(new AdListener(){
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                finish();
            }
        });
    }*/


    /**
     * returns next quote to display
     */
   /* private Quote getQuote()
    {
        Quote quote;
        int s_no;

        QuoteHelper helper = new QuoteHelper(QuoteScreen.this);
        SharedPreferences preferences = getSharedPreferences("Alarm", Context.MODE_PRIVATE);
        s_no = preferences.getInt("pos", 0);
        quote = helper.readQuote(s_no);
        s_no =quote.getS_no()+1;
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isQuoteUsed", true);
        editor.putInt("pos", s_no);
        editor.apply();

        Log.i(TAG, "getQuote: "+quote.getQuote());
        return quote;

    }*/

    //schedulling call to parent activity
    TimerTask callback_task = new TimerTask() {
        @Override
        public void run() {
            Intent parent_intent;
            int parent = getIntent().getIntExtra("parent", AlarmActivity.QuoteScreen);

            switch (parent) {
                case AlarmActivity.AddAlarm:
                    parent_intent = new Intent(QuoteScreen.this, Home.class);
                    break;
                case AlarmActivity.QuoteScreen:
                    parent_intent = new Intent(QuoteScreen.this, Home.class);
                    break;
                case AlarmActivity.AlarmScreen:
                    parent_intent = new Intent(QuoteScreen.this, AlarmScreen.class);
                    parent_intent.putExtra(Constants.ALARM_ID_KEY, getIntent().getIntExtra(Constants.ALARM_ID_KEY, 0));
                    QuoteScreen.this.runOnUiThread(new Runnable() {
                        public void run() {
                            //Toast.makeText(QuoteScreen.this,"SURPRISE SNOOZE",Toast.LENGTH_LONG).show();
                            Snackbar.make(parentView, "SURPRISE SNOOZE", Snackbar.LENGTH_SHORT).show();
                        }
                    });

                    break;
                default:
                    parent_intent = new Intent(QuoteScreen.this, Home.class);
                    finish();
            }
            startActivity(parent_intent);
            finish();
        }
    };


    //onClick

    public void done(View view) {
        callback.cancel();
        Intent intent = new Intent("iris.jaagore.sabita_sant.alarm.GET_QUOTE");
        sendBroadcast(intent);
       /* if(ad.isLoaded())
            ad.show();*/
        finish();
    }

    //stop callback when activity goes in background

    @Override
    protected void onPause() {
        super.onPause();
        callback.cancel();
        finish();
    }


}
