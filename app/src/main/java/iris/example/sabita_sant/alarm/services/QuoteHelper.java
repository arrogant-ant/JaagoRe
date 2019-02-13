package iris.example.sabita_sant.alarm.services;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import iris.example.sabita_sant.alarm.models.AlarmDatabase;
import iris.example.sabita_sant.alarm.models.Quote;

/**
 * Created by Sud on 10/20/17.
 * Read and write quotes from QuoteCache (local storage)
 */

public class QuoteHelper {

    private static final String TAG = "QuoteHelper";
    private final String FILENAME = "firebaseQuote.txt";
    private Context context;
    private AlarmDatabase db;

    public QuoteHelper(Context context) {
        this.context = context;
        db = AlarmDatabase.getInstance(context);
    }

    public Quote getQuote() {
        SharedPreferences preferences = context.getSharedPreferences("Alarm", Context.MODE_PRIVATE);
        int s_no = preferences.getInt("pos", 0);
        Quote quote = readQuote(s_no);
        s_no = quote.getS_no() + 1;
        SharedPreferences.Editor editor = preferences.edit();
        //   editor.putBoolean("isQuoteUsed", true);
        editor.putInt("pos", s_no);
        editor.apply();
        if (s_no < db.quoteDao().getQuotesCount()) {
            // android 9 throws except when called from worker
            try {
                context.startService(new Intent(context, QuoteService.class));
            } catch (IllegalStateException ignored) {
            }
        }
        Log.i(TAG, "getQuote: " + quote.getQuote());
        return quote;
    }

    void writeQuote(Quote quote) {
        if (quote == null)
            return;
        Log.i(TAG, "writeQuote: " + quote.getQuote() + " s no " + quote.getS_no());
        db.quoteDao().addQuote(quote);
    }

    private Quote readQuote(int s_no) {
        Quote quote;
        quote = db.quoteDao().getQuote(s_no);
        //if no quote is returned from cache
        if (quote == null)
            quote = new Quote();
        Log.i(TAG, "readQuote: " + quote.getQuote() + " s no " + s_no);
        return quote;
    }


}
