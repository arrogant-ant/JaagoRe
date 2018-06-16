package iris.jaagore.sabita_sant.alarm;


import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import iris.jaagore.sabita_sant.alarm.logic.Quote;
import iris.jaagore.sabita_sant.alarm.logic.QuoteHelper;


public class QuoteScreen extends AppCompatActivity {
    TextView title_tx, quote_tx, author_tx;
    Timer callback;
    long delay= 30000;// 30 sec
    InterstitialAd ad;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        title_tx = (TextView) findViewById(R.id.quote_title);
        quote_tx = (TextView) findViewById(R.id.quote);
        author_tx = (TextView) findViewById(R.id.aurthor);
        setUI();
        setAd();
        callback=new Timer();
        callback.schedule(callback_task,delay);
        Log.e("QouteActivity","onCreate");


    }
//interstitial ad setup
    private void setAd() {
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
    }

    private void setUI() {
        ArrayList<Quote> quotes;
        int s_no;
        Quote quote;
        QuoteHelper helper = new QuoteHelper(QuoteScreen.this);
        quotes = helper.readQuote();
        quote = quotes.get(0);
        quote_tx.setText(quote.getQuote());
        author_tx.setText(quote.getAuthor());
        s_no = quote.getS_no();
        if (Calendar.getInstance().get(Calendar.HOUR_OF_DAY) > 11) {
            title_tx.setText("Quote of the Day");
        }
        //setting font
        Typeface dancingScript = Typeface.createFromAsset(getAssets(), "fonts/DancingScript-Regular.ttf");
        quote_tx.setTypeface(dancingScript);

        Typeface raleway=Typeface.createFromAsset(getAssets(),"fonts/Raleway-SemiBold.ttf");
        author_tx.setTypeface(raleway);
        title_tx.setTypeface(raleway);


        //moving the quote to the last
        quotes.remove(0);
        quotes.add(quotes.size(), quote);
        helper.writeQuote(quotes);
        SharedPreferences preferences = getSharedPreferences("Alarm", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("isQuoteUsed", true);
        editor.putInt("pos", s_no);
        editor.apply();


    }

    //schedulling call to parent activity
    TimerTask callback_task = new TimerTask() {
        @Override
        public void run() {
            Intent parent_intent;
            int parent = getIntent().getExtras().getInt("parent");

            switch (parent) {
                case AlarmActivity.AddAlarm:
                    parent_intent = new Intent(QuoteScreen.this, AddAlarm.class);
                    break;
                case AlarmActivity.QuoteScreen:
                    parent_intent = new Intent(QuoteScreen.this, AddAlarm.class);
                    break;
                case AlarmActivity.AlarmScreen:
                    parent_intent = new Intent(QuoteScreen.this, AlarmScreen.class);
                    QuoteScreen.this.runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(QuoteScreen.this,"SURPRISE SNOOZE",Toast.LENGTH_LONG).show();
                        }
                    });

                    break;
                default:
                    parent_intent = new Intent(QuoteScreen.this, AddAlarm.class);
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
        if(ad.isLoaded())
            ad.show();
        finish();
    }

    //stop callback when activity goes in background

    @Override
    protected void onPause() {
        super.onPause();
        Log.e("QouteActivity","onPause");
        callback.cancel();
        finish();
    }

    public void nextQuote(View view) {
        Intent intent = new Intent("iris.jaagore.sabita_sant.alarm.GET_QUOTE");
        sendBroadcast(intent);
        Intent next=new Intent(this,QuoteScreen.class);
        next.putExtra("parent",AlarmActivity.QuoteScreen);
        startActivity(next);
        finish();


    }
}
