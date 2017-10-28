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

import java.util.ArrayList;
import java.util.Calendar;


public class QuoteActivity extends AppCompatActivity {
    TextView title_tx,quote_tx,author_tx;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quote);
        final Window win = getWindow();
        win.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
        win.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
        title_tx= (TextView) findViewById(R.id.quote_title);
        quote_tx= (TextView) findViewById(R.id.quote);
        author_tx= (TextView) findViewById(R.id.aurthor);
        setUI();

    }

    private void setUI() {

        ArrayList<Quote> quotes;

        Quote quote;
        QuoteHelper helper=new QuoteHelper(QuoteActivity.this);
        quotes=helper.readQuote();
        quote=quotes.get(0);
        quote_tx.setText(quote.getQuote());
        author_tx.setText(quote.getAuthor());
        if(Calendar.getInstance().get(Calendar.HOUR_OF_DAY)>12)
        {
            title_tx.setText("Quote of the Day");
        }
        //setting font
        Typeface font=Typeface.createFromAsset(getAssets(),"fonts/Raleway-SemiBold.ttf");
        quote_tx.setTypeface(font);


        //moving the quote to the last
        quotes.remove(0);
        quotes.add(quotes.size(),quote);
        helper.writeQuote(quotes);
        SharedPreferences preferences=getSharedPreferences("Alarm", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean("isQuoteUsed",true);
        editor.apply();


    }

    public void done(View view) {
        Intent intent=new Intent("iris.jaagore.sabita_sant.alarm.GET_QUOTE");
        sendBroadcast(intent);
        finish();
    }
}
