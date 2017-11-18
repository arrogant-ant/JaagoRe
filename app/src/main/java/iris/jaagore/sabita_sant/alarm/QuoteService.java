package iris.jaagore.sabita_sant.alarm;

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class QuoteService extends IntentService {
    URL url;
    HttpURLConnection conn;

    public QuoteService() {
        super("QuoteWorker");
        Log.d("QuoteService", "service constructor");

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("QuoteService", "service started");
        BufferedReader reader;
        StringBuilder response = new StringBuilder();
        try {

            url = new URL("https://andruxnet-random-famous-quotes.p.mashape.com/?cat=movies&count=2");
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("X-Mashape-Key", "mx1in3H0wymshOpz13lJlrDWyFusp1ZEt0sjsnz7jewyAPJCJS");
            conn.setRequestProperty("Accept", "application/json");
            reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            parseResponse(QuoteService.this, response.toString());

        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d("QuoteService", "service stopped");

    }

    private void parseResponse(Context context, String s) {
        ArrayList<Quote> quotes = new ArrayList<>();
        try {

            JSONArray jsonArray = new JSONArray(s);
            for (int i = 0; i < jsonArray.length(); i++) {
                Quote quote = new Quote();
                JSONObject obj = jsonArray.getJSONObject(i);
                quote.setQuote(obj.getString("quote"));
                quote.setAuthor(obj.getString("author"));
                quotes.add(quote);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        QuoteHelper helper = new QuoteHelper(context);
        helper.writeQuote(quotes);
        //update quote status
        SharedPreferences preferences=context.getSharedPreferences("Alarm",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor=preferences.edit();
        editor.putBoolean("isQuoteUsed",false);
        editor.commit();

    }


}
