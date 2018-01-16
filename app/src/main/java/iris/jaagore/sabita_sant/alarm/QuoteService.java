package iris.jaagore.sabita_sant.alarm;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;
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
    SharedPreferences preferences;
    int pos;

    public QuoteService() {
        super("QuoteWorker");
        Log.d("QuoteService", "service constructor");

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("QuoteService", "service started");
        preferences = getSharedPreferences("Alarm", MODE_PRIVATE);
        pos = preferences.getInt("pos", 0);
        BufferedReader reader;
        StringBuilder response = new StringBuilder();
        try {

            url = new URL("http://techdrona.net/jaagore/getquotes.php?pos=" + pos);
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
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
            JSONObject response = jsonArray.getJSONObject(0);
            if (response.getBoolean("response")) {
                for (int i = 1; i < jsonArray.length(); i++) {
                    Quote quote = new Quote();
                    JSONObject obj = jsonArray.getJSONObject(i);
                    quote.setQuote(obj.getString("Quote"));
                    quote.setAuthor(obj.getString("Author"));
                    quote.setS_no(obj.getInt("S_No"));
                    quotes.add(quote);
                }
            }
            QuoteHelper helper = new QuoteHelper(context);
            helper.writeQuote(quotes);
            //update quote status
            SharedPreferences preferences = context.getSharedPreferences("Alarm", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isQuoteUsed", false);
            editor.apply();
        }catch(JSONException e){
            e.printStackTrace();
        }

    }


}
