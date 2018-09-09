package iris.jaagore.sabita_sant.alarm.logic;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;

import iris.jaagore.sabita_sant.alarm.backend.AlarmDatabase;
import iris.jaagore.sabita_sant.alarm.backend.Quote;
import iris.jaagore.sabita_sant.alarm.backend.QuoteDao;

/**
 * Created by Sud on 10/20/17.
 * Read and write quotes from QuoteCache (local storage)
 */

public class QuoteHelper {

    private static final String TAG = "QuoteHelper";
    Context context;
    final String FILENAME = "firebaseQuote.txt";
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
    public ArrayList<Quote> readQuote() {
        ArrayList<Quote> quotes = new ArrayList<>();
        Quote quote;
        try {
            FileInputStream fis = context.openFileInput(FILENAME);
            ObjectInputStream ois = new ObjectInputStream(fis);
            while ((quote = (Quote) ois.readObject()) != null) {
                quotes.add(quote);
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        if (quotes.size() == 0) {
            Quote defaultQuote = new Quote();
            defaultQuote.setQuote("Pain is temporary. Quitting last forever.");
            defaultQuote.setAuthor("Lance Armstrong");
            defaultQuote.setS_no(-1);
            quotes.add(defaultQuote);
        }
        return quotes;

    }

    public void writeQuote(ArrayList<Quote> quotes) {

        try {
            FileOutputStream fos = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            for (Quote quote : quotes)
                oos.writeObject(quote);

            //update cache quote status
            SharedPreferences preferences = context.getSharedPreferences("Alarm", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isQuoteUsed", false);
            editor.apply();
            oos.flush();
            oos.close();

        } catch (IOException e) {
            e.printStackTrace();
        }


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
