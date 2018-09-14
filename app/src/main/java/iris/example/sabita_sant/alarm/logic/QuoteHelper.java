package iris.example.sabita_sant.alarm.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import iris.example.sabita_sant.alarm.backend.AlarmDatabase;
import iris.example.sabita_sant.alarm.backend.Quote;

/**
 * Created by Sud on 10/20/17.
 * Read and write quotes from QuoteCache (local storage)
 */

public class QuoteHelper {

    private static final String TAG = "QuoteHelper";
    private Context context;
    private final String FILENAME = "firebaseQuote.txt";
    private AlarmDatabase db;

    public QuoteHelper(Context context) {
        this.context = context;
        db = AlarmDatabase.getInstance(context);
    }
    public Quote getQuote()
    {
        SharedPreferences preferences = context.getSharedPreferences("Alarm", Context.MODE_PRIVATE);
        int s_no = preferences.getInt("pos", 0);
        Quote quote = readQuote(s_no);
        s_no =quote.getS_no()+1;
        SharedPreferences.Editor editor = preferences.edit();
     //   editor.putBoolean("isQuoteUsed", true);
        editor.putInt("pos", s_no);
        editor.apply();

        Log.i(TAG, "getQuote: "+quote.getQuote());
        return quote;
    }

    void writeQuote(Quote quote) {

        Log.i(TAG, "writeQuote: "+quote.getQuote()+" s no " + quote.getS_no());
        db.quoteDao().addQuote(quote);
    }

    private Quote readQuote(int s_no){
        Quote quote;
        quote = db.quoteDao().getQuote(s_no);
        //if no quote is returned from cache
        if(quote == null)
            quote = new Quote();
        Log.i(TAG, "readQuote: "+quote.getQuote()+" s no " + s_no);
        return quote;
    }



}
