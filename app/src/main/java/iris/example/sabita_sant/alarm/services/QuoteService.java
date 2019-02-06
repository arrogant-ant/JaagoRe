package iris.example.sabita_sant.alarm.services;

import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import iris.example.sabita_sant.alarm.models.Quote;
import iris.example.sabita_sant.alarm.utils.Constants;

/*
    Responsible for getting quotes from server
 */
public class QuoteService extends IntentService {
    private static final String TAG = "QuoteService";
    SharedPreferences preferences;
    Quote firebaseQuote;
    private int pos;
    private FirebaseDatabase database;
    private DatabaseReference dbRef, quoteRef, countRef;


    public QuoteService() {
        super("QuoteWorker");
        Log.d("QuoteService", "service constructor");
        database = FirebaseDatabase.getInstance();
        dbRef = database.getReference("Quotes");
        countRef = dbRef.child("count");

    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d("QuoteService", "service started");
        //get total quotes count from firebase
        countRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                final int count = dataSnapshot.getValue(Integer.class);
                updateQuoteCache(count);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        Log.d("QuoteService", "service stopped");

    }

    private void updateQuoteCache(int count) {
        preferences = getSharedPreferences("Alarm", MODE_PRIVATE);
        pos = preferences.getInt("pos", 0);
        for (int i = 0; i < Constants.QUOTES_CACHE_SIZE; i++) {
            int index = (pos + i) % count;
            getAndStoreQuote(index);
            Log.i(TAG, "onHandleIntent: quotes at" + pos + " " + count);
        }

    }

    private void getAndStoreQuote(int pos) {
        quoteRef = dbRef.child(String.valueOf(pos));
        quoteRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                firebaseQuote = dataSnapshot.getValue(Quote.class);
                QuoteHelper helper = new QuoteHelper(QuoteService.this);
                helper.writeQuote(firebaseQuote);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


}
